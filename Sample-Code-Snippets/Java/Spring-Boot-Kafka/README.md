Ensure Eventhubs Emulator is up and running.<br>
To modify connection-string & bootstrap-servers use `resources/application.yml` file.


``` bash
cd ./Sample-Code-Snippets/Java/Spring-Boot-Kafka/eventhubs-client

../mvnw clean install

java -jar ./target/eventhubs-client-0.0.1-SNAPSHOT.jar
```