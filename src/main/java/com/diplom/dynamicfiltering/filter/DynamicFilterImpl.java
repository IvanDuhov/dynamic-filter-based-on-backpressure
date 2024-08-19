package com.diplom.dynamicfiltering.filter;

import com.diplom.dynamicfiltering.config.DelayConfig;
import com.diplom.dynamicfiltering.kafka.consumer.KafkaTestMessageConsumer;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import it.unimi.dsi.util.XoRoShiRo128PlusRandom;
import jakarta.annotation.PostConstruct;
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

	private static final int recalculatePeriod = 10;

	private final DelayConfig delayConfig;

	private final MeterRegistry meterRegistry;

	private static final Double maxDroppingPercentage = 90.0;

	private final List<Long> delays = new ArrayList<>();

	private Double droppingPercentage = 1d;

	private Long currentDelay = 0L;

	private double initialDroppingSteep = 0.9;

	private Long lastDelta = 0L;

	private final XoRoShiRo128PlusRandom random = new XoRoShiRo128PlusRandom();

	@Value("${filter.time.period:60000}")
	private Long timePeriod; // In milliseconds

	public DynamicFilterImpl(DelayConfig delayConfig, MeterRegistry meterRegistry)
	{
		this.delayConfig = delayConfig;
		this.meterRegistry = meterRegistry;
	}

	@PostConstruct
	public void init()
	{
		Gauge.builder("dropping_percentage", this, obj -> obj.droppingPercentage)
			 .register(meterRegistry);
		Gauge.builder("delay", this, obj -> obj.currentDelay)
			 .register(meterRegistry);
		Gauge.builder("delta", this, obj -> obj.lastDelta)
			 .register(meterRegistry);
	}

	@Override
	public boolean shouldProcess(final Long delay, final Integer popularity)
	{
		processDelay(delay);

		final int transformedPopularity = transformPopularity(popularity);

		final double dropPercentageForPopularity = calculatePopularityDropPercentage(transformedPopularity);

		if (dropPercentageForPopularity > 0)
		{
			return true;
		}

		final int randomness = random.nextInt(100);

		return !(Math.abs(dropPercentageForPopularity) >= randomness);
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

		if (lastDelta == 0 || delta == 0)
		{
			lastDelta = delta;
			return 0;
		}

		final double deltaPercentageDiff = calculatePercentageDiff(delta, lastDelta);

		double tempDropPercentage = getTempDropPercentage(delta, deltaPercentageDiff);

		lastDelta = delta;

		return tempDropPercentage;
	}

	private double getTempDropPercentage(final Long delta, final double deltaPercentageDiff)
	{
		if (deltaPercentageDiff == 0)
		{
			return droppingPercentage;
		}

		// 10 is initial dropping percentage
		double tempDropPercentage = droppingPercentage == 0 ? 10 : droppingPercentage;

		long periodsBetweenNowAndMaxDelay = (delayConfig.getMaxDelay() - Math.max(1, currentDelay)) / recalculatePeriod;

		if (periodsBetweenNowAndMaxDelay < 0 && droppingPercentage >= maxDroppingPercentage)
		{
			initialDroppingSteep += Math.max(0.25, periodsBetweenNowAndMaxDelay / Math.abs(deltaPercentageDiff));
			initialDroppingSteep = Math.min(initialDroppingSteep, 3);
			return droppingPercentage;
		}
		else
		{
			initialDroppingSteep = 0.9;
		}

		// 1.25 is the max increase step
		double multiplier = deltaPercentageDiff * Math.max(5, 1 + (double) 1 / Math.min(1, periodsBetweenNowAndMaxDelay));

		if (delta < 0 && lastDelta < 0)
		{
			tempDropPercentage *= (1 + Math.abs(multiplier) / 100);
		}
		else
		{
			// TODO: calculate if the current going down rate is enough
			tempDropPercentage /= (1 + Math.abs(multiplier) / 100);
			logger.info("Decreasing dropping percentage from: " + droppingPercentage + " to " + tempDropPercentage);
		}

		if (tempDropPercentage < 0)
		{
			tempDropPercentage = 0;
		}
		if (tempDropPercentage > maxDroppingPercentage)
		{
			tempDropPercentage = maxDroppingPercentage;
		}

		logger.info("delay is: " + currentDelay + ". Delta percentage diff is: " + deltaPercentageDiff + "Periods to burn down: " +
					periodsBetweenNowAndMaxDelay + ". Multiplier is: " + multiplier + ". Delta is: " + delta +
					". New drop % is: " + tempDropPercentage + ". Dropping steep is: " + initialDroppingSteep);

		return tempDropPercentage;
	}

	@Override
	// f(x)=-a x+90-b
	public Double calculatePopularityDropPercentage(final Integer popularity)
	{
		return -initialDroppingSteep * popularity + (90 - droppingPercentage);
	}

	@Scheduled(fixedRate = recalculatePeriod, timeUnit = TimeUnit.SECONDS)
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

	private double calculatePercentageIncrease(final Long start, final Long end)
	{
		return (double) (end - start) / Math.abs(start) * 100;
	}

	private void processDelay(long delay)
	{
		delays.add(delay);
		this.currentDelay = delay;
	}
}
