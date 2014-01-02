package com.arcblaze.arctime.tasks;

import java.util.Date;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.arcblaze.arctime.db.DaoFactory;
import com.arcblaze.arctime.db.DatabaseException;
import com.arcblaze.arctime.db.dao.CompanyDao;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;

/**
 * Responsible for periodically tracking the number of active companies in the
 * system.
 */
public class ActiveCompanyCounterTask extends BackgroundTask {
	/**
	 * @param metricRegistry
	 *            the registry of metrics used to track system performance
	 *            information
	 * @param healthCheckRegistry
	 *            the registry of system health and status information
	 */
	public ActiveCompanyCounterTask(MetricRegistry metricRegistry,
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
			CompanyDao companyDao = DaoFactory.getCompanyDao();
			companyDao.setActiveCompanies(new Date(), companyDao.count(false));
		} catch (DatabaseException databaseError) {
			throw new BackgroundTaskException(
					"Failed to update active company count.", databaseError);
		}
	}
}
