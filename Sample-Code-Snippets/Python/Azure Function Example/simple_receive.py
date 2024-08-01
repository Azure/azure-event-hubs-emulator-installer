import asyncio
import os
from azure.eventhub.aio import EventHubConsumerClient
from azure.identity.aio import DefaultAzureCredential


async def on_event(partition_context, event):
    # Print the event data.
    print(f'Received an event: {event.body_as_str(encoding="UTF-8")} from the partition with ID: "{partition_context.partition_id}"')
    await partition_context.update_checkpoint(event)

async def on_partition_initialize(partition_context):
    print(f"Partition: {partition_context.partition_id} has been initialized.")

async def on_partition_close(partition_context, reason):
    print(f"Partition: {partition_context.partition_id} has been closed for {reason}.")

async def on_error(partition_context, error):
    print(f"Partition: {partition_context.partition_id} has errored. {error}")


async def main():


    event_hub_right_path = 'right_eh'
    event_hub_left_path = 'left_eh'
    right_cg = "rcg1"
    left_cg = "lcg1"

    # Create a consumer client for the event hub.
    consumer = EventHubConsumerClient.from_connection_string(
        conn_str="Endpoint=sb://localhost;SharedAccessKeyName=RootManageSharedAccessKey;SharedAccessKey=SAS_KEY_VALUE;UseDevelopmentEmulator=true;",
        eventhub_name=event_hub_left_path,
        consumer_group=left_cg)
    async with consumer:
        await consumer.receive(
            on_event=on_event,
            on_error=on_error,
            on_partition_close=on_partition_close,
            on_partition_initialize=on_partition_initialize,
            starting_position="-1"
        )

if __name__ == "__main__":
    # Run the main method.
    asyncio.run(main())