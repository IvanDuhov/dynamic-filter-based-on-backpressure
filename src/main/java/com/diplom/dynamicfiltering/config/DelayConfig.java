package com.diplom.dynamicfiltering.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Data
@Service
public class DelayConfig
{

	@Value("${producer.messages.batch:100}")
	private int defaultMessagesPerBatch;

	@Value("${producer.messages.batch:100}")
	private int messagesPerBatch;

	@Value("${producer.time:1}")
	private int cronMinutes;

	@Value("${filter.min.delay:30}")
	private long minDelay;

	@Value("${filter.max.delay:300}")
	private long maxDelay;
}
