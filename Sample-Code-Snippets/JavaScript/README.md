# JavaScript EventHub Trigger Function App example

This app is an azure function with an eventHub trigger type.

## Local Development using the EventHub Emulator

1) Start the EventHub Emulator for your OS.

```shell
cd EventHub-Emulator/Scripts/Mac
./LaunchEmulator.sh --ACCEPT_EULA=Y
```

2) Start the Azure Function App

```shell
cd Sample-Code-Snippets/JavaScript
npm install
npm run start
```
