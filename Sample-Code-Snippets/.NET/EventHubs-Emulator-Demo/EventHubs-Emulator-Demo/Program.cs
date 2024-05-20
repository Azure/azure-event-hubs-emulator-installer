using Azure.Messaging.EventHubs.Producer;
using Azure.Messaging.EventHubs;
using System.Text;
using Azure.Messaging.EventHubs.Consumer;
using Azure.Messaging.EventHubs.Processor;
using Azure.Storage.Blobs;

internal class Program
{
    private static string checkpointBlobContainer = Guid.NewGuid().ToString();

    private static string eventHubNamespaceConnectionString = "Endpoint=sb://localhost;SharedAccessKeyName=RootManageSharedAccessKey;SharedAccessKey=EMULATOR_DEV_SAS_VALUE;UseDevelopmentEmulator=true;";
    private static string eventHubName = "eh1";

    public static async Task Main(string[] args)
    {
        //Sends a batch of events to the event hub
        await Send();

        //Receives events from the event hub
        await Recieve();

        //Receives the same events from the event hub using an event processor
        await CreateCheckpointBlobForTests(checkpointBlobContainer);
        await ReceiveWithEventProcessor();
        
        Console.ReadKey();
    }

    private static async Task Send()
    {
        // number of events to be sent to the event hub
        int numOfEvents = 100;

        EventHubProducerClient producerClient = new EventHubProducerClient(eventHubNamespaceConnectionString, eventHubName);

        // Create a batch of events 
        using EventDataBatch eventBatch = await producerClient.CreateBatchAsync();

        for (int i = 1; i <= numOfEvents; i++)
        {
            if (!eventBatch.TryAdd(new EventData(Encoding.UTF8.GetBytes($"Event {i}"))))
            {
                // if it is too large for the batch
                throw new Exception($"Event {i} is too large for the batch and cannot be sent.");
            }
        }

        try
        {
            // Use the producer client to send the batch of events to the event hub
            await producerClient.SendAsync(eventBatch);
            Console.WriteLine($"A batch of {numOfEvents} events has been published.");
        }
        finally
        {
            await producerClient.DisposeAsync();
        }
    }

    private static async Task Recieve()
    {
        var consumer = new EventHubConsumerClient(EventHubConsumerClient.DefaultConsumerGroupName, eventHubNamespaceConnectionString, eventHubName);

        await foreach (PartitionEvent partitionEvent in consumer.ReadEventsAsync(new ReadEventOptions { MaximumWaitTime = TimeSpan.FromSeconds(2) }))
        {
            if (partitionEvent.Data != null)
            {
                string messageBody = Encoding.UTF8.GetString(partitionEvent.Data.Body.ToArray());
                Console.WriteLine($"Message received : '{messageBody}'");
            }
            else
            {
                break;
            }
        }
    }


    /// <summary>
    /// The Reciever uses a Checkpoint blob created on top of Azure Storage Emulator aka Azurite
    /// In Prod Environment user is responsible for provisioning and maintaining his checkpoints blobs 
    /// </summary>
    /// <returns></returns>
    private static async Task ReceiveWithEventProcessor()
    {
      

        BlobContainerClient blobContainerClient = new BlobContainerClient("UseDevelopmentStorage=true", checkpointBlobContainer);

        // Create an event processor client to process events in the event hub
        // TODO: Replace the <EVENT_HUBS_NAMESPACE> and <HUB_NAME> placeholder values
        var processor = new EventProcessorClient(blobContainerClient, EventHubConsumerClient.DefaultConsumerGroupName, eventHubNamespaceConnectionString, eventHubName);

        // Register handlers for processing events and handling errors
        processor.ProcessEventAsync += ProcessEventHandler;
        processor.ProcessErrorAsync += ProcessErrorHandler;

        // Start the processing
        await processor.StartProcessingAsync();

        // Wait for 30 seconds for the events to be processed
        await Task.Delay(TimeSpan.FromSeconds(10));

        // Stop the processing
        await processor.StopProcessingAsync();

        
    }

    private static Task ProcessEventHandler(ProcessEventArgs eventArgs)
    {
        // Write the body of the event to the console window
        Console.WriteLine("Received event using Event Processor: {0}", Encoding.UTF8.GetString(eventArgs.Data.Body.ToArray()));
        return Task.CompletedTask;
    }

    private static Task ProcessErrorHandler(ProcessErrorEventArgs eventArgs)
    {
        // Write details about the error to the console window
        Console.WriteLine($"\tPartition '{eventArgs.PartitionId}': an unhandled exception was encountered. This was not expected to happen.");
        Console.WriteLine(eventArgs.Exception.Message);
        return Task.CompletedTask;
    }


    /// <summary>
    /// Crates a Azure Blob for use as a checkpoint store on Azurite Storage Emulator
    /// Strictly for EH Emulator Testing ; not intended for production use
    /// </summary>
    /// <param name="blobContainerName"></param>
    /// <returns></returns>
    private static async Task CreateCheckpointBlobForTests(string blobContainerName)
    {
        BlobServiceClient client = new BlobServiceClient("UseDevelopmentStorage=true");
        try
        {
            await client.CreateBlobContainerAsync(blobContainerName);
        }
        catch (Exception ex)
        {
            Console.WriteLine(ex.ToString());
        }
    }
}