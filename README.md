
 # <img src="https://raw.githubusercontent.com/Azure/azure-event-hubs-emulator-installer/main/azure-eventhubs-emulator.svg" alt="Event-Hubs Logo" width="50">    Azure Event Hubs Emulator Installer

This repository contains the scripts required to install and run the  [Azure Event Hubs Emulator](https://learn.microsoft.com/en-us/azure/event-hubs/overview-emulator).

- [Azure Event Hubs](#About-Azure-Event-Hubs)
  - [Emulator Overview](#About-Azure-Event-Hubs-Emulator)
  - [Prerequisites](#Prerequisites)
  - [Running Emulator](#Running-the-emulator)
    - [Using Automated Script](#Using-Automated-Script)
    - [Using Docker Compose](#Using-Docker-Compose-Linux-Container)
  - [Interacting with Emulator](#Interacting-with-emulator)
  - [Support](#Support)
  - [License](#License)

## About Azure Event Hubs

Azure Event Hubs is a cloud native data streaming service that can stream millions of events per second, with low latency, from any source to any destination. Event Hubs is compatible with Apache Kafka, and it enables you to run existing Kafka workloads without any code changes. Read more [here](https://learn.microsoft.com/en-us/azure/event-hubs/event-hubs-about).

## About Azure Event Hubs Emulator 

Azure Event Hubs emulator is designed to offer a local development experience for Azure Event Hubs, enabling you to develop and test code against our services in isolation, free from cloud interference.

>[!CAUTION]
>Emulator is intended solely for development and testing scenarios.Any kind of Production use is strictly discouraged. There is no official support provided for Emulator.
> Any issues/suggestions should be reported via GitHub issues on [GitHub project](https://github.com/Azure/azure-event-hubs-emulator-installer/issues).
## Run Azure Event Hubs Emulator 

This section summarizes the steps to develop and test locally with Event hubs Emulator. To read more about Event hubs, read [here](event-hubs-about.md).

## Prerequisites

- Docker 
  - [Docker Desktop](https://docs.docker.com/desktop/install/windows-install/#:~:text=Install%20Docker%20Desktop%20on%20Windows%201%20Download%20the,on%20your%20choice%20of%20backend.%20...%20More%20items) 
- Minimum hardware Requirements:
  - 2 GB RAM
  - 5 GB of Disk space
- WSL Enablement (Only for Windows):
  - [Install Windows Subsystem for Linux (WSL) | Microsoft Learn](https://learn.microsoft.com/en-us/windows/wsl/install)
  -  [Configure Docker to use WSL](https://docs.docker.com/desktop/wsl/#:~:text=Turn%20on%20Docker%20Desktop%20WSL%202%201%20Download,engine%20..%20...%206%20Select%20Apply%20%26%20Restart.)

>[!NOTE]
>Before you continue with the subsequent steps, make sure Docker Engine is operational in the background.

## Running the Emulator 

This section highlights different steps to run Event Hubs Emulator. Details are as follows:

#### [Using Automated Script](#tab/automated-script)

 Before running automated script, clone the installation [repository](https://github.com/Azure/azure-event-hubs-emulator-installer) locally.
 
### Windows

After completing the prerequisites, you can proceed with the following steps to run the Event Hubs Emulator locally on Windows: 

1. **Open PowerShell** and navigate to the directory where the common scripts [folder](EventHub-Emulator/Scripts/Common) is cloned using `cd`:
   ```powershell
   cd <path to your common scripts folder> # Update this path
      
2. Issue wsl command to open WSL at this directory.
   ```powershell
   wsl

3. **Run the setup script** *./LaunchEmulator.sh* Running the script brings up two containers: the Event Hubs emulator and Azurite (a dependency for the emulator).
   ```bash
   ./Launchemulator.sh
 

### Linux & macOS
After completing the prerequisites, you can proceed with the following steps to run the Event Hubs Emulator locally. 

1. Execute the setup script [LaunchEmulator.sh](EventHub-Emulator/Scripts/Common/LaunchEmulator.sh) . Running the script would  bring up two containers – Event Hubs Emulator & Azurite (dependency for Emulator)

1. Execute the same script `LaunchEmulator.sh` with the option `--compose-down=Y` to issue a `docker compose down` to terminate the containers.

```shell
LaunchEmulator.sh --compose-down=Y
```

#### [Using Docker Compose (Linux Container)](#tab/docker-linux-container)

You can also spin up Emulator using Docker Compose file from Microsoft Container Registry. Refer [here](https://mcr.microsoft.com/en-us/product/azure-messaging/eventhubs-emulator/about#usage) for details. 

Once the steps are successful, Emulator compose set can be found in running in Docker.

![image](https://github.com/Azure/azure-event-hubs-emulator-installer/assets/62641016/f7c8d2ad-dea1-4fd5-84b6-8f105ce2b602)

## Interacting with Emulator

1. You can use the following connection string to connect to Azure Event Hubs Emulator.
```
"Endpoint=sb://localhost;SharedAccessKeyName=RootManageSharedAccessKey;SharedAccessKey=SAS_KEY_VALUE;UseDevelopmentEmulator=true;"
```
2. With the latest client SDK releases, you can interact with the Emulator in various programming language. For details, refer [here](https://learn.microsoft.com/en-us/azure/event-hubs/sdks#client-sdks)

To get started, refer to our GitHub Samples [here](https://github.com/Azure/azure-event-hubs-emulator-installer/tree/main/Sample-Code-Snippets).
  
>[!TIP]
> $Default [consumer group](https://learn.microsoft.com/en-us/azure/event-hubs/event-hubs-features#consumer-groups) is created by default when emulator runs. You can't create $default consumer group with supplied configuration.

### Networking options
You can run and connect to Emulator in multiple ways. Use a `Connection String` from following as per your use-case:

- When the emulator container and interacting application are running natively on local machine:
```
"Endpoint=sb://localhost;SharedAccessKeyName=RootManageSharedAccessKey;SharedAccessKey=SAS_KEY_VALUE;UseDevelopmentEmulator=true;"
```

- Applications (Containerized/Non-containerized) on the different machine and same local network can interact with Emulator using the IPv4 address of the machine:
```
"Endpoint=sb://192.168.y.z;SharedAccessKeyName=RootManageSharedAccessKey;SharedAccessKey=SAS_KEY_VALUE;UseDevelopmentEmulator=true;"
```

- Application containers on the same bridge network can interact with Emulator using its alias or IP. Following connection string assumes the name of Emulator has default value i.e."eventhubs-emulator":
```
Endpoint=sb://eventhubs-emulator;SharedAccessKeyName=RootManageSharedAccessKey;SharedAccessKey=SAS_KEY_VALUE;UseDevelopmentEmulator=true;"
```

- Application containers on the different bridge network can interact with Emulator using the "host.docker.internal" as host:
```
"Endpoint=sb://host.docker.internal;SharedAccessKeyName=RootManageSharedAccessKey;SharedAccessKey=SAS_KEY_VALUE;UseDevelopmentEmulator=true;"
```

> **Note**
If you are using the Kafka protocol, ensure that you update the `Bootstrap Servers` property with the appropriate host from the options above, based on your use case.


## Support

There is no official support provided for Emulator.Any issues/suggestions should be reported via GitHub issues on [installation repo](https://github.com/Azure/azure-event-hubs-emulator-installer/issues).

## License

The scripts and documentation in this project are released under the MIT License.

The software (Azure Event Hubs Emulator) that the scripts in this repository install is licensed under separate terms. See the [End User License Agreement](https://github.com/Azure/azure-event-hubs-emulator-installer/blob/main/EMULATOR_EULA.md) for the terms governing the software.






   


