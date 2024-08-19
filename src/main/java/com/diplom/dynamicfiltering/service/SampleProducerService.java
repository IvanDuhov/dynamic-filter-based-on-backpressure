package com.diplom.dynamicfiltering.service;

import com.diplom.dynamicfiltering.config.DelayConfig;
import com.diplom.dynamicfiltering.kafka.model.KafkaTestMessage;
import com.diplom.dynamicfiltering.kafka.producer.KafkaTestMessageSender;
import it.unimi.dsi.util.XoRoShiRo128PlusRandom;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class SampleProducerService
{

	private static final int TIME_PERIOD = 1;
	private final KafkaTestMessageSender kafkaTestMessageSender;
	private final XoRoShiRo128PlusRandom random;
	private final DelayConfig delayConfig;

	public SampleProducerService(KafkaTestMessageSender kafkaTestMessageSender, DelayConfig delayConfig)
	{
		this.kafkaTestMessageSender = kafkaTestMessageSender;
		this.delayConfig = delayConfig;
		this.random = new XoRoShiRo128PlusRandom();
	}


	@Scheduled(fixedRate = TIME_PERIOD, timeUnit = TimeUnit.MINUTES)
	private void produceMessages()
	{
		for (int i = 0; i < delayConfig.getMessagesPerBatch(); i++)
		{
			KafkaTestMessage user = new KafkaTestMessage("John Doe", random.nextInt(100), 30, System.currentTimeMillis());

			try
			{
				Thread.sleep(delayConfig.getMessagesPerBatch() / TIME_PERIOD);
			}
			catch (InterruptedException e)
			{
				throw new RuntimeException(e);
			}

			kafkaTestMessageSender.sendMessage(user);
		}
	}
}
