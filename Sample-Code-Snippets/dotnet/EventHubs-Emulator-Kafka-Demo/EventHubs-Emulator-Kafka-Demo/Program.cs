using Confluent.Kafka;

class Program
{
    public static async Task Main(string[] args)
    {
        // Configuration
        var kafkaBootstrapServers = "localhost:9092";
        var eventHubsConnectionString = "Endpoint=sb://localhost;SharedAccessKeyName=RootManageSharedAccessKey;SharedAccessKey=SAS_KEY_VALUE;UseDevelopmentEmulator=true;";
        string eventHubName = "eh1";


        // Producer
        int produceCount = 100;
        var producerConfig = new ProducerConfig
        {
            BootstrapServers = kafkaBootstrapServers,
            SecurityProtocol = SecurityProtocol.SaslPlaintext,
            SaslMechanism = SaslMechanism.Plain,
            SaslUsername = "$ConnectionString",
            SaslPassword = eventHubsConnectionString
        };
        using (var producer = new ProducerBuilder<Null, string>(producerConfig).Build())
        {
            for (int i = 0; i < produceCount; i++)
            {
                var message = new Message<Null, string> { Value = $"Message {i}" };
                var deliveryResult = await producer.ProduceAsync(eventHubName, message);
                Console.WriteLine($"Delivered '{deliveryResult.Value}' to '{deliveryResult.TopicPartitionOffset}'");
            }
        }


        // Consumer
        int receiveCount = 0;
        string consumerGroupId = "cg1";
        var consumerConfig = new ConsumerConfig
        {
            BootstrapServers = kafkaBootstrapServers,
            SecurityProtocol = SecurityProtocol.SaslPlaintext,
            SaslMechanism = SaslMechanism.Plain,
            SaslUsername = "$ConnectionString",
            SaslPassword = eventHubsConnectionString,
            GroupId = consumerGroupId,
            EnableAutoCommit = true,
            AutoOffsetReset = AutoOffsetReset.Earliest
        };
        using (var consumer = new ConsumerBuilder<Null, string>(consumerConfig).Build())
        {
            consumer.Subscribe(eventHubName);

            CancellationTokenSource cts = new CancellationTokenSource(TimeSpan.FromSeconds(15));
            Console.CancelKeyPress += (_, e) => {
                e.Cancel = true;
                cts.Cancel();
            };

            try
            {
                while (receiveCount < produceCount)
                {
                    var cr = consumer.Consume(cts.Token);
                    Console.WriteLine($"Consumed message '{cr.Message.Value}' from: '{cr.TopicPartitionOffset}'.");
                    receiveCount += 1;
                }
            }
            catch (OperationCanceledException)
            {
                Console.WriteLine("Cancelled!");
            }

            consumer.Close();
        }

        Console.WriteLine($"Produced: {produceCount} messages");
        Console.WriteLine($"Consumed: {receiveCount} messages");
    }
}
