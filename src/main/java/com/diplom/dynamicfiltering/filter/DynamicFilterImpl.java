package com.diplom.dynamicfiltering.filter;

import com.diplom.dynamicfiltering.kafka.consumer.KafkaTestMessageConsumer;
import it.unimi.dsi.util.XoRoShiRo128PlusRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class DynamicFilterImpl implements DynamicFilter
{

	private static final Logger logger = LoggerFactory.getLogger(KafkaTestMessageConsumer.class);

	private static final Double maxDroppingPercentage = 90.0;

	private final List<Long> delays = new ArrayList<>();

	private double droppingPercentage = 1;

	private double initialDroppingSteep = -0.9;

	private Long lastDelta = 0L;

	private final XoRoShiRo128PlusRandom random = new XoRoShiRo128PlusRandom();


	@Value("${filter.time.period:60000}")
	private Long timePeriod; // In milliseconds

	@Override
	public boolean shouldProcess(final Long delay, final Integer popularity)
	{
		delays.add(delay);

		final double dropPercentageForPopularity = calculatePopularityDropPercentage(transformPopularity(popularity));

		logger.info("Drop percentage for a record with pop: " + popularity + " is:" + dropPercentageForPopularity);

		if (dropPercentageForPopularity > 0)
		{
			return true;
		}

		return !(Math.abs(dropPercentageForPopularity) <= random.nextInt(100));
	}

	@Override
	public double calculateBackpressureIndicator(final List<Long> delays)
	{
		if (!(delays.size() > 2))
		{
			return 0;
		}

		final Long startDelay = delays.getFirst();
		final Long endDelay = delays.getLast();

		final Long delta = startDelay - endDelay;

		final double deltaPercentageDiff = calculatePercentageDiff(delta, lastDelta);

		droppingPercentage *= deltaPercentageDiff;

		if (droppingPercentage < 0)
		{
			droppingPercentage = 1;
		}
		if (droppingPercentage > maxDroppingPercentage)
		{
			droppingPercentage = maxDroppingPercentage;
		}

		lastDelta = delta;

		return droppingPercentage;
	}

	@Override
	// f(x)=-a x+90-b
	public Double calculatePopularityDropPercentage(final Integer popularity)
	{
		return initialDroppingSteep * popularity + (90 - droppingPercentage);
	}

	@Scheduled(fixedRate = 10_000, timeUnit = TimeUnit.MILLISECONDS)
	private void recalculateDropPercentage()
	{
		droppingPercentage = calculateBackpressureIndicator(delays);
		logger.info("New dropping percentage: " + droppingPercentage);
	}

	private int transformPopularity(int popularity)
	{
		return 100 - popularity;
	}

	private double calculatePercentageDiff(final Long num1, final Long num2)
	{
		return (double) (Math.abs(num1 - num2)) / ((double) (num1 + num2) / num2);
	}
}
