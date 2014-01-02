package com.arcblaze.arctime.tasks;

import java.util.concurrent.ScheduledExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.codahale.metrics.health.HealthCheck;
import com.codahale.metrics.health.HealthCheckRegistry;

/**
 * The base class for background tasks running within this system.
 */
public abstract class BackgroundTask extends HealthCheck implements Runnable {
	private final static Logger log = LoggerFactory
			.getLogger(BackgroundTask.class);

	/** Used to manage application metrics. */
	private final MetricRegistry metricRegistry;

	/** Used to manage application health and status. */
	private final HealthCheckRegistry healthCheckRegistry;

	/** In the event of a task failure, this will capture the error. */
	private BackgroundTaskException failure;

	/**
	 * @param metricRegistry
	 *            the registry of metrics used to track system performance
	 *            information
	 * @param healthCheckRegistry
	 *            the registry of system health and status information
	 */
	public BackgroundTask(MetricRegistry metricRegistry,
			HealthCheckRegistry healthCheckRegistry) {
		this.metricRegistry = metricRegistry;
		this.healthCheckRegistry = healthCheckRegistry;

		this.healthCheckRegistry
				.register(this.getClass().getSimpleName(), this);
	}

	/**
	 * @return a reference to the system metric registry
	 */
	public MetricRegistry getMetricRegistry() {
		return this.metricRegistry;
	}

	/**
	 * @return a reference to the system health check registry
	 */
	public HealthCheckRegistry getHealthCheckRegistry() {
		return this.healthCheckRegistry;
	}

	/**
	 * Schedule this background task using the provided executor.
	 * 
	 * @param executor
	 *            the {@link ScheduledExecutorService} responsible for
	 *            scheduling and running this task
	 */
	public abstract void schedule(ScheduledExecutorService executor);

	/**
	 * Responsible for performing the background task processing.
	 * 
	 * @throws BackgroundTaskException
	 *             if there is a problem during execution of the background task
	 */
	public abstract void process() throws BackgroundTaskException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void run() {
		String name = this.getClass().getSimpleName();
		try (Timer.Context timer = getMetricRegistry().timer(name).time()) {
			try {
				process();

				this.failure = null;
			} catch (BackgroundTaskException taskFailed) {
				log.error("Background task processing failed.", taskFailed);
				this.failure = taskFailed;
			} catch (Throwable somethingBad) {
				log.error("Unexpected background task failure.", somethingBad);
				this.failure = new BackgroundTaskException(
						"Unexpected failure: " + somethingBad.getMessage(),
						somethingBad);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Result check() throws Exception {
		if (this.failure != null)
			return Result.unhealthy(this.failure);
		return Result.healthy();
	}
}
