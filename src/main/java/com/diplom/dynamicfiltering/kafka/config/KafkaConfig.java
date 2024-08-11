package com.diplom.dynamicfiltering.kafka.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class KafkaConfig
{
	@Value("${kafka.bootstrap.servers:localhost:9092}")
	private String bootstrapServers;

	@Value("${kafka.consumer.client.id:test}")
	private String clientId;

	@Value("${kafka.producer.client.id:test}")
	private String producerClientId;

	@Value("${kafka.topic:test}")
	private String kafkaTopic;
}
