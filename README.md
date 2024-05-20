
 # <img src="https://github.com/Azure/azure-event-hubs-emulator/blob/main/azure-eventhubs-emulator.svg" alt="Alt text" width="50">    Azure Event Hubs emulator

- [Overview](#Overview-of-Azure-Event-Hubs-emulator)
  - [Benefits](#Benefits-of-Emulator)
  - [Features](#Features-of-Emulator)
  - [Known Limitations](#Known-Limitations)
  - [Difference between emulator and cloud service](#Difference-between-emulator-and-Event-hubs-cloud-service)
  - [Managing Quotas & Configuration](#Managing-Quotas-and-Configuration)
  - [Making Configuration changes](#Making-configuration-changes)
  - [Drill through available Logs](#Drill-through-available-logs)
  - [Support](#Support)
  - [License](#License)
- [Test locally with emulator](#test-locally-with-Event-Hubs-emulator)
  - [PreRequisites](#Prerequisites)
  - [Running Emulator](#Running-the-emulator)
    - [Using Automated Script](#Using-Automated-Script)
    - [Using Docker Compose](#Using-Docker-Compose-(-Linux-Container-))
  - [Interacting with Emulator](#Interacting-with-emulator)
 - [ChangeLog](#ChangeLog)
  

    

## Overview of Azure Event Hubs emulator

Azure Event Hubs emulator is designed to offer a local development experience for [Azure Event Hubs](https://learn.microsoft.com/en-us/azure/event-hubs/event-hubs-about), enabling you to develop and test code against our services in isolation, free from cloud interference.

### Benefits of emulator

The primary advantages of using the emulator are:

- Local Development: The Emulator provides a local development experience, enabling developers to work offline and avoid network latency.
- Cost-Efficiency: With the Emulator, developers can test their applications without incurring any cloud usage costs.
- Isolated Testing Environment: You can test your code in isolation, ensuring that the tests aren't impacted by other activities in the cloud.
- Optimized Inner Development loop: Developers can use the Emulator to quickly prototype and test their applications before deploying them to the cloud.

>[!NOTE]
> Event Hubs emulator is licensed under End user License Agreement. For more details, refer [here.](https://github.com/Azure/azure-event-hubs-emulator/blob/main/LICENSE)

### Features of Emulator

This section highlights different features provided with Emulator:

- Containerized Deployment: The Emulator runs as a Docker container (Linux-based).
- Cross-Platform Compatibility: It can be used on any platform, including Windows, macOS, and Linux.
- Managing Entity Configuration: Users can manage number of event hubs, partition count etc. using JSON supplied Configuration.
- Streaming Support: The Emulator supports streaming messages using AMQP (Advanced Message Queuing Protocol).
- Observability: It provides observability features, including console and file logging.

### Known Limitations

Current version of emulator has the following limitations:

- It can't stream messages using Kafka protocol.  
- It doesn't support  on fly management operations using Client side SDK. 

> [!NOTE]
> In case of container restart,data and entities are not persisted in emulator.

### Difference between emulator and Event hubs cloud service

Since Emulator is only meant for development and test purpose, there are functional differences between emulator and cloud service. Here are the high-level features that aren't supported in the Event Hubs emulator:

-  Azure Goodness – VNet Integration/ Microsoft Entra ID integration/ Activity Logs/ UI Portal etc.
-  Event Hubs Capture
-  Resource Governance features like Application Groups
-  Auto scale capabilities
-  Geo DR capabilities
-  Schema Registry Integration.
-  Visual Metrics/ Alerts

>[!CAUTION]
>The emulator is intended solely for development and testing scenarios.Any kind of Production use is strictly discouraged. There is no official support provided for emulator.
> Any issues/suggestions should be reported via GitHub issues on emulator [GitHub project](https://github.com/Azure/azure-event-hubs-emulator/issues).

### Managing Quotas and Configuration

Like our cloud service, Azure Event Hubs emulator provides below quotas for usage: 

| Property| Value| User Configurable within limits
| ----|----|----|
Number of supported namespaces| 1 |No| 
Maximum number of Event Hubs within namespace| 10| Yes| 
Maximum number of consumer groups within event hub| 20 |Yes| 
Maximum number of partitions in event hub |32 |Yes 
Maximum size of event being published to event hub (batch/nonbatch) |1 MB |No
Minimum event retention time | 1 hr | No


### Making configuration changes

You could use config.json to configure quotas associated with Event Hubs. By default, emulator would run with following [configuration](https://github.com/Azure/azure-event-hubs-emulator/blob/main/EventHub-Emulator/Config/Config.json). Under the configuration file, you could make following edits as per needs: 

- **Entities**: You could add more entities (event hubs), with customized partition count and consumer groups count as per supported quotas.
- **Logging**: Emulator supports Logging in file or console or both. You could set as per your personal preference.

>[!IMPORTANT]
> Any changes in JSON configuration must be supplied before running emulator and isn't honoured on fly. For subsequent changes to take effect, container restart is required.
>You cannot rename the preset namespace ("name") in configuration file.

### Drill through available logs
During testing phase, logs help in debugging unexpected failures. For this reason, Emulator supports logging in forms of Console and File. Follow below steps to review the logs: 
- **Console Logs**: On docker desktop UI, click on the container name to open Console Logs.
- **File Logs**: These would be present at /home/app/EmulatorLogs within the container.

### Support
There is no official support provided for emulator.Any issues/suggestions should be reported via GitHub issues on emulator [GitHub project](https://github.com/Azure/azure-event-hubs-emulator/issues).

### License
For more details, refer [here.](https://github.com/Azure/azure-event-hubs-emulator/blob/main/LICENSE)

---
## Test locally with Event Hubs emulator 

This section summarizes the steps to develop and test locally with Event hubs emulator. To read more about Event hubs, read [here.](event-hubs-about.md)

## Prerequisites

- Docker emulator
  - [Docker Desktop](https://docs.docker.com/desktop/install/windows-install/#:~:text=Install%20Docker%20Desktop%20on%20Windows%201%20Download%20the,on%20your%20choice%20of%20backend.%20...%20More%20items) 
- Minimum hardware Requirements:
  - 2 GB RAM
  - 5 GB of Disk space
- WSL Enablement (Only for Windows):
  - [Install Windows Subsystem for Linux (WSL) | Microsoft Learn](https://learn.microsoft.com/en-us/windows/wsl/install)
  -  [Configure Docker to use WSL](https://docs.docker.com/desktop/wsl/#:~:text=Turn%20on%20Docker%20Desktop%20WSL%202%201%20Download,engine%20..%20...%206%20Select%20Apply%20%26%20Restart.)

>[!NOTE]
>Before you continue with the subsequent steps, make sure Docker Desktop is operational in the background.

## Running the emulator 

This section highlights different steps to run Event Hubs emulator. Details are as follows:

#### [Using Automated Script](#tab/automated-script)

 Before running automated script, clone the Event Hubs emulator GitHub [repository](https://github.com/Azure/azure-event-hubs-emulator) locally.
 
### Windows
After completing the prerequisites, you can proceed with the following steps to run the Event Hubs emulator locally. 
1. Before executing the setup script, we need to allow execution of unsigned scripts. Run the below command in the PowerShell window:

`$>Start-Process powershell -Verb RunAs -ArgumentList 'Set-ExecutionPolicy Bypass –Scope CurrentUser’`

2. Execute setup script `LaunchEmulator.ps1`. Running the script would bring up two containers – Event Hubs emulator & Azurite (dependency for Emulator)
3. Once the steps are successful, you could find containers running in Docker Desktop.

### Linux & macOS
After completing the prerequisites, you can proceed with the following steps to run the Event Hubs emulator locally. 

1. Execute the setup script `LaunchEmulator.sh` . Running the script would  bring up two containers – Event Hubs emulator & Azurite (dependency for Emulator)
2. Once the steps are successful, you could find containers running in Docker.



#### [Using Docker Compose (Linux Container)](#tab/docker-linux-container)
1. To start the emulator, you should supply configuration for the entities you want to use. Save the following JSON file locally as config.json

```JSON
{
    "UserConfig": {
        "NamespaceConfig": [
        {
            "Type": "EventHub",
            "Name": "emulatorNs1",
            "Entities": [
            {
                "Name": "eh1",
                "PartitionCount": "2",
                "ConsumerGroups": [
                {
                    "Name": "cg1"
                }
                ]
            }
            ]
        }
        ], 
        "LoggingConfig": {
            "Type": "File"
        }
    }
}

```
>[!TIP]
> $Default [consumer group](https://learn.microsoft.com/en-us/azure/event-hubs/event-hubs-features#consumer-groups) is created by default when emulator runs. You can't create $default consumer group with supplied configuration.

2. Save following yaml file as docker-compose.yaml to spin up containers for Event Hubs emulator.
```
name: microsoft-azure-eventhubs
services:
  emulator:
    container_name: "eventhubs-emulator"
    image: "mcr.microsoft.com/azure-messaging/eventhubs-emulator:latest"
    volumes:
      - "${CONFIG_PATH}:/Eventhubs_Emulator/ConfigFiles/Config.json"
    ports:
      - "5672:5672"
    environment:
      BLOB_SERVER: azurite
      METADATA_SERVER: azurite
      ACCEPT_EULA: ${ACCEPT_EULA}
    depends_on:
      - azurite
    networks:
      eh-emulator:
        aliases:
          - "eventhubs-emulator"
  azurite:
    container_name: "azurite"
    image: "mcr.microsoft.com/azure-storage/azurite:latest"
    ports:
      - "10000:10000"
      - "10001:10001"
      - "10002:10002"
    networks:
      eh-emulator:
        aliases:
          - "azurite"
networks:
  eh-emulator:
```
To run emulator in host networking mode, copy OS specific .yaml file from [here](https://github.com/Azure/azure-event-hubs-emulator/tree/main/Docker-Compose-Template)

3. Create .env file to declare the environment variables for event hubs emulator. 

```
# Centralized environment variables store for docker-compose
 
# 1. CONFIG_PATH: Path to config.json file
CONFIG_PATH="<Replace with path to config.json file>" 
 
# 2. ACCEPT_EULA: Pass 'Y' to accept license terms. 
ACCEPT_EULA="N"

```

>[!IMPORTANT]
>Argument 'ACCEPT_EULA' is to confirm on the license terms, read more about the license [here](https://github.com/Azure/azure-event-hubs-emulator/blob/main/LICENSE)
>. Ensure to place .env file in same directory to docker-compose.yaml file.
>. When specifying file paths in Windows, use double backslashes (`\\`) instead of single backslashes (`\`) to avoid issues with escape characters.

4. **Run following command to run emulator**

```
 docker compose -f <PathToDockerComposeFile> up -d
```
---  
## Interacting with Emulator

You can use the following connection string to connect to Azure Event Hubs emulator.
```
"Endpoint=sb://localhost;SharedAccessKeyName=RootManageSharedAccessKey;SharedAccessKey=SAS_KEY_VALUE;UseDevelopmentEmulator=true;"
```
To get started, refer to our GitHub Samples [here.](https://github.com/Azure/azure-event-hubs-emulator/tree/main/Sample-Code-Snippets)

## Change Log

You can find more details about the changes in latest versions [here](https://github.com/Azure/azure-event-hubs-emulator/blob/main/CHANGELOG.md)

