package com.arcblaze.arctime.rest.admin.stats;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.time.DateUtils;

import com.arcblaze.arctime.db.DaoFactory;
import com.arcblaze.arctime.db.DatabaseException;
import com.arcblaze.arctime.db.dao.TransactionDao;
import com.arcblaze.arctime.db.dao.UserDao;
import com.arcblaze.arctime.rest.BaseResource;
import com.codahale.metrics.Timer;

/**
 * The REST end-point for retrieving system statistics.
 */
@Path("/admin/stats/system")
public class SystemStatsResource extends BaseResource {
	@Context
	private ServletContext servletContext;

	@XmlRootElement
	static class SystemStat {
		@XmlElement
		public String name;

		@XmlElement
		public String value;
	}

	@XmlRootElement
	static class Stats {
		@XmlElement(name = "stat")
		public List<SystemStat> statList;
	}

	/**
	 * @return the relevant system statistics
	 * 
	 * @throws DatabaseException
	 *             if there is an error communicating with the back-end
	 */
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Stats getStats() throws DatabaseException {
		try (Timer.Context timer = getTimer(this.servletContext,
				"/admin/stats/system")) {
			List<FutureTask<SystemStat>> tasks = Arrays.asList(getRevenueYTD(),
					getRevenueYear(), getActiveUsers(), getActiveCompanies());

			for (FutureTask<SystemStat> task : tasks)
				task.run();

			List<SystemStat> statList = new ArrayList<>(tasks.size());
			for (FutureTask<SystemStat> task : tasks)
				statList.add(task.get());

			Stats stats = new Stats();
			stats.statList = statList;
			return stats;
		} catch (InterruptedException | ExecutionException error) {
			throw serverError(error);
		}
	}

	protected FutureTask<SystemStat> getRevenueYTD() throws DatabaseException {
		return new FutureTask<SystemStat>(new Callable<SystemStat>() {
			@Override
			public SystemStat call() throws DatabaseException {
				TransactionDao transactionDao = DaoFactory.getTransactionDao();
				Date tomorrow = DateUtils.addDays(new Date(), 1);
				Date jan1 = DateUtils.truncate(new Date(), Calendar.YEAR);

				SystemStat revenueYTD = new SystemStat();
				revenueYTD.name = "Revenue YTD";
				revenueYTD.value = String.format("$%.2f",
						transactionDao.amountBetween(jan1, tomorrow));
				return revenueYTD;
			}
		});
	}

	protected FutureTask<SystemStat> getRevenueYear() throws DatabaseException {
		return new FutureTask<SystemStat>(new Callable<SystemStat>() {
			@Override
			public SystemStat call() throws DatabaseException {
				TransactionDao transactionDao = DaoFactory.getTransactionDao();
				Date tomorrow = DateUtils.addDays(new Date(), 1);
				Date yearAgo = DateUtils.addDays(new Date(), -365);

				SystemStat revenueYear = new SystemStat();
				revenueYear.name = "Revenue Year";
				revenueYear.value = String.format("$%.2f",
						transactionDao.amountBetween(yearAgo, tomorrow));
				return revenueYear;
			}
		});
	}

	protected FutureTask<SystemStat> getActiveUsers() throws DatabaseException {
		return new FutureTask<SystemStat>(new Callable<SystemStat>() {
			@Override
			public SystemStat call() throws DatabaseException {
				UserDao userDao = DaoFactory.getUserDao();
				SystemStat activeUsers = new SystemStat();
				activeUsers.name = "Active Users";
				activeUsers.value = String.valueOf(userDao.count(false));
				return activeUsers;
			}
		});
	}

	protected FutureTask<SystemStat> getActiveCompanies()
			throws DatabaseException {
		return new FutureTask<SystemStat>(new Callable<SystemStat>() {
			@Override
			public SystemStat call() throws DatabaseException {
				UserDao companyDao = DaoFactory.getUserDao();
				SystemStat activeCompanies = new SystemStat();
				activeCompanies.name = "Active Companies";
				activeCompanies.value = String.valueOf(companyDao.count(false));
				return activeCompanies;
			}
		});
	}
}
