package com.diplom.dynamicfiltering.kafka.producer;

import com.diplom.dynamicfiltering.kafka.config.KafkaProducerConfig;
import com.diplom.dynamicfiltering.kafka.model.KafkaTestMessage;
import com.diplom.dynamicfiltering.service.ProducerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaTestMessageSender
{

	private static final Logger logger = LoggerFactory.getLogger(ProducerService.class);
	private final KafkaTemplate<String, String> kafkaTemplate;
	private final ObjectMapper objectMapper;

	public KafkaTestMessageSender(@Qualifier("kafkaProducerStringString") final KafkaTemplate<String, String> kafkaTemplate)
	{
		this.kafkaTemplate = kafkaTemplate;
		this.objectMapper = new ObjectMapper();
	}

	public void sendMessage(final KafkaTestMessage message)
	{
		try
		{
			final String serializedMsg = objectMapper.writeValueAsString(message);

			final ProducerRecord<String, String> record = new ProducerRecord<>(KafkaProducerConfig.TEST_TOPIC, serializedMsg);

			kafkaTemplate.send(record);
		}
		catch (Exception e)
		{
			logger.error("Failed sending kafka test message. Exception: " + e);
		}
	}
}
