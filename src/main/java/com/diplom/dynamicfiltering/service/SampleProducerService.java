package com.diplom.dynamicfiltering.service;

import com.diplom.dynamicfiltering.kafka.model.KafkaTestMessage;
import com.diplom.dynamicfiltering.kafka.producer.KafkaTestMessageSender;
import it.unimi.dsi.util.XoRoShiRo128PlusRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class SampleProducerService
{

	private static final Logger logger = LoggerFactory.getLogger(SampleProducerService.class);
	private static final int MESSAGES_PER_RUN = 100;

	private static final  int TIME_PERIOD = 1;
	private final KafkaTestMessageSender kafkaTestMessageSender;

	private final XoRoShiRo128PlusRandom random;

	public SampleProducerService(KafkaTestMessageSender kafkaTestMessageSender)
	{
		this.kafkaTestMessageSender = kafkaTestMessageSender;
		this.random = new XoRoShiRo128PlusRandom();
	}


	@Scheduled(fixedRate = TIME_PERIOD, timeUnit = TimeUnit.MINUTES)
	private void produceMessages()
	{
		for (int i = 0; i < MESSAGES_PER_RUN; i++)
		{
			KafkaTestMessage user = new KafkaTestMessage("John Doe", random.nextInt(100), 30, System.currentTimeMillis());

			try
			{
				Thread.sleep(MESSAGES_PER_RUN / TIME_PERIOD);
			}
			catch (InterruptedException e)
			{
				throw new RuntimeException(e);
			}

			kafkaTestMessageSender.sendMessage(user);
		}

		logger.info("Successfully has sent all the messages for the minute. Count of sent messages: " + MESSAGES_PER_RUN);
	}
}
