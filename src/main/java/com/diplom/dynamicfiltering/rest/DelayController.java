package com.diplom.dynamicfiltering.rest;

import com.diplom.dynamicfiltering.config.DelayConfig;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/private/delay")
public class DelayController
{
	private final DelayConfig delayConfig;

	public DelayController(DelayConfig delayConfig)
	{
		this.delayConfig = delayConfig;
	}

	@PostMapping
	public void changeDelayRatio(@RequestParam Double ratio)
	{
		delayConfig.setMessagesPerBatch((int) (delayConfig.getDefaultMessagesPerBatch() * ratio));
	}
}
