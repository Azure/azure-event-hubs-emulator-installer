# JavaScript EventHub Trigger Function App example

This app is an azure function with an eventHub trigger type.

## Local Development using the EventHub Emulator

1) Start the EventHub Emulator for your OS.

```shell
cd EventHub-Emulator/Scripts/Mac
./LaunchEmulator.sh --ACCEPT_EULA=Y

# or run the docker compose file directly. From the root of the repo:
ACCEPT_EULA=Y \
  CONFIG_PATH=$(pwd)/EventHub-Emulator/Config/Config.json \
  docker compose -f Docker-Compose-Template/docker-compose-default.yml up
```

2) Start the Azure Function App

```shell
cd Sample-Code-Snippets/JavaScript/EventHubTrigger-FunctionApp

npm install

npm run start
# or func start --verbose
```

# The problem:

This azure function is not able to start and prints error:

```
|% npm run start                                                                                 

> JavaScript@1.0.0 start
> func start

Azure Functions Core Tools
Core Tools Version:       4.0.5801 Commit hash: N/A +5ac2f09758b98257e728dd1b5576ce5ea9ef68ff (64-bit)
Function Runtime Version: 4.34.1.22669

[2024-06-21T19:25:04.665Z] Worker process started and initialized.
[2024-06-21T19:25:04.760Z] A host error has occurred during startup operation '596df785-fe3e-4bed-904d-7a832201ea36'.
[2024-06-21T19:25:04.760Z] Microsoft.Azure.WebJobs.Extensions.EventHubs: EventHub account connection string 'Endpoint=sb://localhost;SharedAccessKeyName=RootManageSharedAccessKey;SharedAccessKey=SAS_KEY_VALUE;UseDevelopmentEmulator=true;' does not exist.Make sure that it is a defined App Setting.
[2024-06-21T19:25:04.767Z] Failed to stop host instance 'e9ea8dd3-7bfb-4d6b-a3d3-eed7fd8c69e9'.
[2024-06-21T19:25:04.767Z] Microsoft.Azure.WebJobs.Host: The host has not yet started. Value cannot be null. (Parameter 'provider')
[2024-06-21T19:25:04.794Z] Host startup operation has been canceled
```

Verbose Logs can be found:

- [func-start-verbose_log.txt](/Sample-Code-Snippets/JavaScript/EventHubTrigger-FunctionApp/func-start-verbose_log.txt)

## What I've Tried:

1) Start the eventhubs-emulator using the
   docs: https://learn.microsoft.com/en-us/azure/event-hubs/test-locally-with-event-hub-emulator?tabs=automated-script#linux-and-macos
2) Use the extension bundles in the host.json file as explained
   here: https://learn.microsoft.com/en-us/azure/azure-functions/functions-bindings-register#extension-bundles
   - This was my first attempt and where the problem was caused.
3) Convert to Explicitly install
   extensions: https://learn.microsoft.com/en-us/azure/azure-functions/functions-bindings-register#explicitly-install-extensions
   - I used the latest version available https://www.nuget.org/packages/Microsoft.Azure.WebJobs.Extensions.EventHubs/
     at the time.
      - `func extensions install --package Microsoft.Azure.WebJobs.Extensions.EventHubs --version 6.3.3`
   - This didn't work either.
