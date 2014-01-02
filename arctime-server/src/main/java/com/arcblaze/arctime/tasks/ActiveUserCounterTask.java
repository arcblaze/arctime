package com.arcblaze.arctime.tasks;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.arcblaze.arctime.db.DaoFactory;
import com.arcblaze.arctime.db.DatabaseException;
import com.arcblaze.arctime.db.dao.UserDao;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;

/**
 * Responsible for periodically tracking the number of active users available
 * for each company, information that is used for billing customers.
 */
public class ActiveUserCounterTask extends BackgroundTask {
	/**
	 * @param metricRegistry
	 *            the registry of metrics used to track system performance
	 *            information
	 * @param healthCheckRegistry
	 *            the registry of system health and status information
	 */
	public ActiveUserCounterTask(MetricRegistry metricRegistry,
			HealthCheckRegistry healthCheckRegistry) {
		super(metricRegistry, healthCheckRegistry);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void schedule(ScheduledExecutorService executor) {
		executor.scheduleAtFixedRate(this, 0, 4, TimeUnit.HOURS);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void process() throws BackgroundTaskException {
		try {
			UserDao userDao = DaoFactory.getUserDao();
			Map<Integer, Integer> activeUsersPerCompany = userDao
					.countPerCompany(false);
			userDao.setActiveUsers(new Date(), activeUsersPerCompany);
		} catch (DatabaseException databaseError) {
			throw new BackgroundTaskException(
					"Failed to update active user counts.", databaseError);
		}
	}
}
