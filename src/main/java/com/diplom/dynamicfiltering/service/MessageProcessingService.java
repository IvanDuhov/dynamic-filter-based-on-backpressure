package com.diplom.dynamicfiltering.service;

import com.diplom.dynamicfiltering.filter.DynamicFilter;
import com.diplom.dynamicfiltering.kafka.consumer.KafkaTestMessageConsumer;
import com.diplom.dynamicfiltering.kafka.model.KafkaTestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MessageProcessingService
{

	private static final Logger logger = LoggerFactory.getLogger(KafkaTestMessageConsumer.class);

	private final DynamicFilter dynamicFilter;

	public MessageProcessingService(DynamicFilter dynamicFilter)
	{
		this.dynamicFilter = dynamicFilter;
	}

	public void process(final KafkaTestMessage message)
	{
		long delayInS = (System.currentTimeMillis() - message.getPublishedMsTime()) / 1000;

		if (!dynamicFilter.shouldProcess(delayInS, message.getPopularity()))
		{
			logger.info("Skipping record with delay of: " + delayInS + " and popularity of: " + message.getPopularity());
			return;
		}

		try
		{
			Thread.sleep(1000);

			logger.info("Processed record with with a delay of " + delayInS + " seconds");
		}
		catch (InterruptedException e)
		{
			throw new RuntimeException(e);
		}
	}
}
