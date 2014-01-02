package com.arcblaze.arctime.tasks;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;

/**
 * Responsible for periodically logging system memory usage.
 */
public class MemoryUsageLoggingTask extends BackgroundTask {
	private final static Logger log = LoggerFactory
			.getLogger(MemoryUsageLoggingTask.class);

	/**
	 * @param metricRegistry
	 *            the registry of metrics used to track system performance
	 *            information
	 * @param healthCheckRegistry
	 *            the registry of system health and status information
	 */
	public MemoryUsageLoggingTask(MetricRegistry metricRegistry,
			HealthCheckRegistry healthCheckRegistry) {
		super(metricRegistry, healthCheckRegistry);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void schedule(ScheduledExecutorService executor) {
		executor.scheduleAtFixedRate(this, 0, 3, TimeUnit.MINUTES);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void process() throws BackgroundTaskException {
		MemoryUsage heap = ManagementFactory.getMemoryMXBean()
				.getHeapMemoryUsage();
		MemoryUsage nonHeap = ManagementFactory.getMemoryMXBean()
				.getNonHeapMemoryUsage();

		double usedHeapMegs = heap.getUsed() / 1024d / 1024d;
		double maxHeapMegs = heap.getMax() / 1024d / 1024d;
		double heapPercentUsed = usedHeapMegs / maxHeapMegs * 100d;

		double usedNonHeapMegs = nonHeap.getUsed() / 1024d / 1024d;
		double maxNonHeapMegs = nonHeap.getMax() / 1024d / 1024d;
		double nonHeapPercentUsed = usedNonHeapMegs / maxNonHeapMegs * 100d;

		log.info(String.format("Memory Usage: Heap %.2f%% of %.0fM, "
				+ "Non Heap %.2f%% of %.0fM", heapPercentUsed, maxHeapMegs,
				nonHeapPercentUsed, maxNonHeapMegs));
	}
}
