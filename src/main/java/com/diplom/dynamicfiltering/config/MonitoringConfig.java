package com.diplom.dynamicfiltering.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.config.NamingConvention;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MonitoringConfig
{

	@Bean
	public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags(
			@Value("${spring.application.name}") String appName)
	{
		return registry -> registry.config()
								   .commonTags("application_name", appName);
	}

	@Bean
	public JvmThreadMetrics threadMetrics(){
		return new JvmThreadMetrics();
	}
}
