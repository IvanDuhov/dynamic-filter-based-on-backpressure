package com.diplom.dynamicfiltering.kafka.config;

import com.diplom.dynamicfiltering.service.SampleProducerService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Service("kafkaTemplateProducerListenerEngine")
public class KafkaTemplateProducerListener<K, V> implements org.springframework.kafka.support.ProducerListener<K, V>
{

	private static final Logger logger = LoggerFactory.getLogger(SampleProducerService.class);

	private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
	private final AtomicLong messageCount = new AtomicLong();

	@PostConstruct
	public void startLogging()
	{
		Runnable runnable = () -> {
			if (messageCount.get() > 0)
			{
				logger.info("{} messages was sent to kafka successfully for the last minute", messageCount.get());

				messageCount.set(0);
			}
			else
			{
				logger.info("No records was sent to kafka for the last minute");
			}
		};

		scheduledExecutorService.scheduleAtFixedRate(runnable, 1, 1, TimeUnit.MINUTES);
	}

	@Override
	public void onSuccess(ProducerRecord producerRecord, RecordMetadata recordMetadata)
	{
		messageCount.incrementAndGet();
	}

	@Override
	public void onError(ProducerRecord producerRecord, @Nullable RecordMetadata recordMetadata, Exception exception)
	{
		logger.error((producerRecord.toString()), "Sent message to kafka failed with: {}", exception.getMessage(), exception);
	}

	@PreDestroy
	public void destroy()
	{
		scheduledExecutorService.shutdown();
	}
}
