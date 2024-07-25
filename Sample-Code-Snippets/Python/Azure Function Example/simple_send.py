import asyncio
import random
from datetime import datetime


from azure.eventhub.aio import EventHubProducerClient
from azure.identity.aio import DefaultAzureCredential
from azure.eventhub.exceptions import EventHubError
from azure.eventhub import EventData

con_string = "Endpoint=sb://localhost;SharedAccessKeyName=RootManageSharedAccessKey;SharedAccessKey=SAS_KEY_VALUE;UseDevelopmentEmulator=true;"

event_message_left = """{"direction_data":{"direction": "left"}}"""
event_message_right = """{"direction_data":{"direction": "right"}}"""

async def send_event_as_list(producer):
    direction = random.randrange(1, 100)
    event_data_list = []

    # If our random number is even, set direction to the right
    if(direction%2 == 0):
        event_data_list = [EventData(event_message_right)]
    else:
        event_data_list = [EventData(event_message_left)]
    await producer.send_batch(event_data_list)

async def run():
    event_hub_path = "inbound_eh"
    producer = EventHubProducerClient.from_connection_string(
        conn_str=con_string, eventhub_name=event_hub_path
    )
    o = 0
    async with producer:
        while(o<10):
            await send_event_as_list(producer=producer)
            o = o+1
    await producer.close()

asyncio.run(run())