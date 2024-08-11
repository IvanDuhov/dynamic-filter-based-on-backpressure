package com.diplom.dynamicfiltering.rest;

import com.diplom.dynamicfiltering.service.ProducerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("private/health")
public class HealthController
{
	private static final Logger log = LoggerFactory.getLogger(ProducerService.class);


	@GetMapping
	public void getHealth()
	{
		log.info("Perfect health.");
	}

}
