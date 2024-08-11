package com.diplom.dynamicfiltering.rest;

import com.diplom.dynamicfiltering.service.SampleProducerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("private/health")
public class HealthController
{
	private static final Logger log = LoggerFactory.getLogger(SampleProducerService.class);


	@GetMapping
	public ResponseEntity<String> getHealth()
	{
		return ResponseEntity.ok("Perfect health.");
	}
}
