package com.diplom.dynamicfiltering.kafka.config;

import com.diplom.dynamicfiltering.service.ProducerService;
import io.micrometer.core.instrument.MeterRegistry;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig
{

	private final KafkaConfig kafkaConfig;

	private static final Logger logger = LoggerFactory.getLogger(ProducerService.class);

	public KafkaProducerConfig(KafkaConfig kafkaConfig)
	{
		this.kafkaConfig = kafkaConfig;
	}

	@Bean
	@Qualifier("kafkaProducerStringString")
	public KafkaTemplate<String, String> equityAccountsMigrationKafkaTemplate(MeterRegistry meterRegistry,
																			  KafkaTemplateProducerListener<String, String> kafkaTemplateProducerListener)
	{
		return kafkaTemplateCriticalLongToBytes(kafkaTemplateProducerListener, meterRegistry, kafkaConfig.getProducerClientId());
	}

	public KafkaTemplate<String, String> kafkaTemplateCriticalLongToBytes(KafkaTemplateProducerListener<String, String> kafkaTemplateProducerListener,
																		  MeterRegistry meterRegistry,
																		  String clientId)
	{
		return kafkaTemplateLongToBytes(kafkaTemplateProducerListener, meterRegistry, clientId, kafkaConfig.getBootstrapServers());
	}

	public KafkaTemplate<String, String> kafkaTemplateLongToBytes(KafkaTemplateProducerListener<String, String> kafkaTemplateProducerListener,
																  MeterRegistry meterRegistry,
																  String clientId,
																  String bootstrapServers)
	{
		try
		{
			Map<String, Object> props = new HashMap<>();
			props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
			props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
			props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
			props.put(ProducerConfig.CLIENT_ID_CONFIG, clientId);

			DefaultKafkaProducerFactory<String, String> producerFactory = new DefaultKafkaProducerFactory<>(props);

			KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>(producerFactory);
			kafkaTemplate.setProducerListener(kafkaTemplateProducerListener);
			return kafkaTemplate;
		}
		catch (Exception e)
		{
			logger.error("Error constructing the kafka producer.", e);
			throw e;
		}
	}
}
