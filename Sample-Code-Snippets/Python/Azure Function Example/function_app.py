import azure.functions as func
import azure.durable_functions as df
import json
import logging
import os

from example_orchestrator import orchestrator

app = df.DFApp(http_auth_level=func.AuthLevel.ANONYMOUS)
app.register_blueprint(orchestrator)

@app.event_hub_message_trigger(connection="EventHubConnection", 
    event_hub_name=os.environ["EventHubTopic"], 
    arg_name="event", 
    consumer_group=os.environ["ConsumerGroups"])
@app.durable_client_input(client_name="client")
async def inbound_eh(event: func.EventHubEvent, client: df.DurableOrchestrationClient):
    #Grab the message
    message = event.get_body().decode("utf-8")
    logging.info(f"{message}")
    instance_id = await client.start_new("orchestrator_function", None, message)
    logging.info(f"Started orchestration with ID {instance_id}.")


