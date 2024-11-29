package com.Azure.Testing.EventHubBinder;

import com.azure.messaging.eventhubs.EventProcessorClientBuilder;
import com.azure.messaging.eventhubs.checkpointstore.blob.BlobCheckpointStore;
import com.azure.spring.messaging.checkpoint.Checkpointer;
import com.azure.spring.messaging.eventhubs.support.EventHubsHeaders;
import com.azure.storage.blob.BlobContainerAsyncClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import java.util.function.Consumer;
import java.util.function.Supplier;
import static com.azure.spring.messaging.AzureHeaders.CHECKPOINTER;

@SpringBootApplication
public class EventHubBinderApplication implements CommandLineRunner{

	private static final Logger LOGGER = LoggerFactory.getLogger(EventHubBinderApplication.class);
	private static final Sinks.Many<Message<String>> many = Sinks.many().unicast().onBackpressureBuffer();

	public static void main(String[] args) {
		SpringApplication.run(EventHubBinderApplication.class, args);
	}

	@Bean
	public Supplier<Flux<Message<String>>> supply() {
		return ()->many.asFlux()
				.doOnNext(m->LOGGER.info("Manually sending message {}", m))
				.doOnError(t->LOGGER.error("Error encountered", t));
	}

	@Bean
	public BlobContainerAsyncClient blobContainerAsyncClient() {
		var blobContainerAsyncClient = new BlobContainerClientBuilder()
				.connectionString("UseDevelopmentStorage=true")
				.containerName("sample-container")
				.buildAsyncClient();

		blobContainerAsyncClient.createIfNotExists().block();
		return blobContainerAsyncClient;
	}

	@Bean
	public BlobCheckpointStore blobCheckpointStore(BlobContainerAsyncClient blobContainerAsyncClient) {
		return new BlobCheckpointStore(blobContainerAsyncClient);
	}

	public EventProcessorClientBuilder eventProcessorClientBuilder(BlobCheckpointStore blobCheckpointStore){
		return new EventProcessorClientBuilder().checkpointStore(blobCheckpointStore);
	}

	@Bean
	public Consumer<Message<String>> consume() {
		return message->{
			Checkpointer checkpointer = (Checkpointer) message.getHeaders().get(CHECKPOINTER);
			LOGGER.info("New message received: '{}', partition key: {}, sequence number: {}, offset: {}, enqueued "
							+"time: {}",
					message.getPayload(),
					message.getHeaders().get(EventHubsHeaders.PARTITION_KEY),
					message.getHeaders().get(EventHubsHeaders.SEQUENCE_NUMBER),
					message.getHeaders().get(EventHubsHeaders.OFFSET),
					message.getHeaders().get(EventHubsHeaders.ENQUEUED_TIME)
			);
			checkpointer.success()
					.doOnSuccess(success->LOGGER.info("Message '{}' successfully checkpointed",
							message.getPayload()))
					.doOnError(error->LOGGER.error("Exception found", error))
					.block();
		};
	}

	@Override
	public void run(String... args) {
		LOGGER.info("Going to add message {} to sendMessage.", "Hello World");
		try {
			Thread.sleep(2000); // Adding a delay of 2 seconds
		} catch (InterruptedException e) {
			LOGGER.error("Thread interrupted", e);
		}
		many.emitNext(MessageBuilder.withPayload("Hello World").build(), Sinks.EmitFailureHandler.FAIL_FAST);
	}

}
