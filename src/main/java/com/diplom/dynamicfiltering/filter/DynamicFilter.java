package com.diplom.dynamicfiltering.filter;

import java.util.List;

public interface DynamicFilter
{
	boolean	shouldProcess(Long delay, Integer popularity);

	double calculateBackpressureIndicator(List<Long> delays);

	Double calculatePopularityDropPercentage(Integer popularity);
}
