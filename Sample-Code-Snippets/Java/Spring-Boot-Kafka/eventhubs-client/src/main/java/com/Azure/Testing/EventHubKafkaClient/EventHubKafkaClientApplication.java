package com.Azure.Testing.EventHubKafkaClient;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import static org.apache.kafka.clients.consumer.ConsumerConfig.*;
import static org.apache.kafka.clients.producer.ProducerConfig.*;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import static org.apache.kafka.clients.CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.CommonClientConfigs.SECURITY_PROTOCOL_CONFIG;
import static org.apache.kafka.common.config.SaslConfigs.SASL_JAAS_CONFIG;
import static org.apache.kafka.common.config.SaslConfigs.SASL_MECHANISM;


import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class EventHubKafkaClientApplication implements CommandLineRunner {

	private static final Logger LOGGER = LoggerFactory.getLogger(EventHubKafkaClientApplication.class);

	// Configurations

	@Value("${spring.cloud.azure.eventhubs.bootstrap-servers}")
	private String kafkaBootstrapServers;

	@Value("${spring.cloud.azure.eventhubs.connection-string}")
	private String eventHubsConnectionString;

	@Value("${spring.cloud.azure.eventhubs.connection-username}")
	private String username;

	@Value("${spring.cloud.azure.eventhubs.event-hub-name}")
	private String eventhubName;

	@Value("${spring.cloud.azure.eventhubs.consumer.consumer-group}")
	private String consumerGroupId;

	public static void main(String[] args) {
		System.out.println(System.getProperty("user. dir"));
		SpringApplication.run(EventHubKafkaClientApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		String saslJassConfig = "org.apache.kafka.common.security.plain.PlainLoginModule required " +
								"username=\"" + username + "\" password=\"" + eventHubsConnectionString + "\";";

		// Producer
		final Properties producerProperties = new Properties() {{
			put(BOOTSTRAP_SERVERS_CONFIG,      kafkaBootstrapServers);
			put(SECURITY_PROTOCOL_CONFIG,      "SASL_PLAINTEXT");
			put(SASL_MECHANISM,                "PLAIN");
			put(SASL_JAAS_CONFIG,              saslJassConfig);
			put(KEY_SERIALIZER_CLASS_CONFIG,   StringSerializer.class.getCanonicalName());
			put(VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getCanonicalName());
		}};
		try (final Producer<String, String> producer = new KafkaProducer<>(producerProperties)) {
			final int numMessages = 100;
			for (int i = 0; i < numMessages; i++) {
				String key = "Key: " + i;
				String value = "Value: " + i;
				producer.send(
						new ProducerRecord<>(eventhubName, key, value),
						(event, ex) -> {
							if (ex != null)
								ex.printStackTrace();
							else
								System.out.printf("Produced event to topic %s [key: %s value: %s] %n", eventhubName, key, value);
						});
			}
			System.out.printf("%s events were produced to topic %s%n", numMessages, eventhubName);
		}

		TimeUnit.SECONDS.sleep(2);


		// Consumer
		final Properties consumerProperties = new Properties() {{
			put(BOOTSTRAP_SERVERS_CONFIG,        kafkaBootstrapServers);
			put(SECURITY_PROTOCOL_CONFIG,        "SASL_PLAINTEXT");
			put(SASL_MECHANISM,                  "PLAIN");
			put(SASL_JAAS_CONFIG,                saslJassConfig);
			put(GROUP_ID_CONFIG,                 consumerGroupId);
			put(AUTO_OFFSET_RESET_CONFIG,        "earliest");
			put(ENABLE_AUTO_COMMIT_CONFIG,       true);
			put(KEY_DESERIALIZER_CLASS_CONFIG,   StringDeserializer.class.getCanonicalName());
			put(VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getCanonicalName());
		}};

		try (final Consumer<String, String> consumer = new KafkaConsumer<>(consumerProperties)) {
			consumer.subscribe(Collections.singletonList(eventhubName));
			while (true) {
				ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
				for (ConsumerRecord<String, String> record : records) {
					System.out.printf("Consumed event from topic %s: key = %-10s value = %s%n", eventhubName, record.key(), record.value());
				}
			}
		}
	}
}
