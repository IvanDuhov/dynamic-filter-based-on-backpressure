package com.diplom.dynamicfiltering.kafka.consumer;

import com.diplom.dynamicfiltering.kafka.config.KafkaProducerConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import com.diplom.dynamicfiltering.kafka.model.KafkaTestMessage;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

@Service
public class KafkaMessageConsumer
{

	private static final Logger log = LoggerFactory.getLogger(KafkaMessageConsumer.class);


	@PostConstruct
	public void init() throws InterruptedException, JsonProcessingException
	{
//		main(null);
	}

	public static void main(String[] args) throws InterruptedException, JsonProcessingException
	{
		log.info("I am a Kafka Consumer");

		String bootstrapServers = "127.0.0.1:9092";

		// create consumer configs
		Properties properties = new Properties();
		properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, KafkaProducerConfig.TEST_GROUP_ID + 1);
		properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

		// create consumer
		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);

		// subscribe consumer to our topic(s)
		consumer.subscribe(Collections.singletonList(KafkaProducerConfig.TEST_TOPIC));

		ObjectMapper mapper = new ObjectMapper();

		// poll for new data
		while (true)
		{
			ConsumerRecords<String, String> records =
					consumer.poll(Duration.ofMillis(100));

			for (ConsumerRecord<String, String> record : records)
			{
				KafkaTestMessage message = mapper.readValue( record.value(), KafkaTestMessage.class);
				long delayInS = (System.currentTimeMillis() - message.getPublishedMsTime()) / 1000;
				log.info("Processed record with key: " + record.key() + " with a delay of " + delayInS + " seconds");

//				log.info("Key: " + record.key() + ", Value: " + message);
//				log.info("Partition: " + record.partition() + ", Offset:" + record.offset());
			}

//			Thread.sleep(200);
		}
	}

}
