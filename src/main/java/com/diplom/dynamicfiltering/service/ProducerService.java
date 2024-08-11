package com.diplom.dynamicfiltering.service;

import com.diplom.dynamicfiltering.kafka.model.KafkaTestMessage;
import com.diplom.dynamicfiltering.kafka.producer.KafkaTestMessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class ProducerService
{

	private static final Logger logger = LoggerFactory.getLogger(ProducerService.class);
	private static final int MESSAGES_PER_MINUTE = 1;
	private final KafkaTestMessageSender kafkaTestMessageSender;

	public ProducerService(KafkaTestMessageSender kafkaTestMessageSender)
	{
		this.kafkaTestMessageSender = kafkaTestMessageSender;
	}


	@Scheduled(fixedRate = 1, timeUnit = TimeUnit.MINUTES)
	private void producerMessages()
	{
		KafkaTestMessage user = new KafkaTestMessage("John Doe", 30, System.currentTimeMillis());

		for (int i = 0; i < MESSAGES_PER_MINUTE; i++)
		{
			kafkaTestMessageSender.sendMessage(user);
		}

		logger.info("Successfully has sent all the messages for the minute. Count of sent messages: " + MESSAGES_PER_MINUTE);
	}
}
