package com.diplom.dynamicfiltering.kafka.consumer;

import com.diplom.dynamicfiltering.kafka.model.KafkaTestMessage;
import com.diplom.dynamicfiltering.service.MessageProcessingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaTestMessageConsumer
{

	private static final Logger logger = LoggerFactory.getLogger(KafkaTestMessageConsumer.class);

	private final MessageProcessingService processingService;

	private final ObjectMapper objectMapper;

	public KafkaTestMessageConsumer(MessageProcessingService processingService, ObjectMapper objectMapper)
	{
		this.processingService = processingService;
		this.objectMapper = objectMapper;
	}

	@KafkaListener(topics = {"${kafka.topic:test}"},
				   containerFactory = "testConsumer")
	public void onKafkaTestMessage(ConsumerRecord<String, String> record)
	{
		try
		{
			KafkaTestMessage message = objectMapper.readValue(record.value(), KafkaTestMessage.class);

			processingService.process(message);
		}
		catch (Exception e)
		{
			logger.error("Error occurred while processing platform settings kafka message.", e);
		}
	}
}
