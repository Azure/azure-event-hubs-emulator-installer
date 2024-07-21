import azure.functions as func
import logging
import os

app = func.FunctionApp()

@app.event_hub_message_trigger(connection="EventHubConnection", 
    event_hub_name=os.environ["EventHubTopic"], 
    arg_name="event", 
    consumer_group=os.environ["ConsumerGroups"])
def inbound_eh(event: func.EventHubEvent):
    logging.info("Python EventHub trigger processed an event: %s", event.get_body().decode('utf-8'))

