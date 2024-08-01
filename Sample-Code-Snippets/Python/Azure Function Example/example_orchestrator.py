import azure.functions as func
import azure.durable_functions as df
import json
import logging
import os

orchestrator = df.Blueprint(http_auth_level=func.AuthLevel.ANONYMOUS)


@orchestrator.orchestration_trigger(context_name="context")
def orchestrator_function(context: df.DurableOrchestrationContext):

    # Grab the message incoming from EventHub
    incoming_message = context.get_input()
    # Check if message says Left or Right...
    direction = yield context.call_activity("check_state", incoming_message)
    result = False
    match(direction):
        case "left":
            result = yield context.call_activity("left_only", incoming_message)
        case "right":
            result = yield context.call_activity("right_only", incoming_message)
    return result

@orchestrator.activity_trigger(input_name="incoming")
def check_state(incoming):
    logging.info(f"Checking if the message goes to the left or the right queue.")
    message = json.loads(incoming)
    direction = message['direction_data']['direction']
    logging.info(f"We are going {direction}!")
    return direction

@orchestrator.event_hub_output(arg_name="dleft", event_hub_name=os.environ['LEventHubTopic'], connection="EventHubConnection")
@orchestrator.activity_trigger(input_name="left")
def left_only(dleft: func.Out[str], left):
    logging.info(msg=f"Sending message to left eventhub.")
    dleft.set(left)
    return True

@orchestrator.event_hub_output(arg_name="dright", event_hub_name=os.environ['REventHubTopic'], connection="EventHubConnection")
@orchestrator.activity_trigger(input_name="right")
def right_only(dright: func.Out[str], right):
    logging.info(msg=f"Sending message to right eventhub.")
    dright.set(right)
    return True