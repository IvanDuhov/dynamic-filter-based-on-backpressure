package com.diplom.dynamicfiltering.kafka.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class KafkaTestMessage
{
	private String name;
	private int popularity;
	private int age;
	private long publishedMsTime;
}
