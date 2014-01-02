package com.arcblaze.arctime.tasks;

import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;

/**
 * Responsible for periodically performing health checks on all system
 * components.
 */
public class SystemHealthCheckTask extends BackgroundTask {
	private final static Logger log = LoggerFactory
			.getLogger(SystemHealthCheckTask.class);

	/**
	 * @param metricRegistry
	 *            the registry of metrics used to track system performance
	 *            information
	 * @param healthCheckRegistry
	 *            the registry of system health and status information
	 */
	public SystemHealthCheckTask(MetricRegistry metricRegistry,
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
		SortedMap<String, Result> results = getHealthCheckRegistry()
				.runHealthChecks();

		int healthy = 0, unhealthy = 0;
		for (Result result : results.values()) {
			healthy += result.isHealthy() ? 1 : 0;
			unhealthy += result.isHealthy() ? 0 : 1;
		}

		if (unhealthy > 0)
			log.warn("Health Checks: {} healthy, {} unhealthy", healthy,
					unhealthy);
		else
			log.info("Health Checks: {} healthy, {} unhealthy", healthy,
					unhealthy);

		if (unhealthy > 0) {
			for (Entry<String, Result> entry : results.entrySet()) {
				Result result = entry.getValue();
				if (result.isHealthy())
					continue;

				log.error("{} => {}", entry.getKey(), entry.getValue());
			}
		}
	}
}
