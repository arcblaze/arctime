package com.arcblaze.arctime.rest.admin.stats;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Map.Entry;
import java.util.SortedMap;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.time.DateUtils;

import com.arcblaze.arctime.db.DaoFactory;
import com.arcblaze.arctime.db.DatabaseException;
import com.arcblaze.arctime.db.dao.TransactionDao;
import com.arcblaze.arctime.rest.BaseResource;
import com.codahale.metrics.Timer;

/**
 * The REST end-point for retrieving system revenue statistics.
 */
@Path("/admin/stats/revenue")
public class RevenueResource extends BaseResource {
	@Context
	private ServletContext servletContext;

	/**
	 * @return the relevant system statistics
	 * 
	 * @throws DatabaseException
	 *             if there is an error communicating with the back-end
	 */
	@GET
	@Produces({ MediaType.TEXT_PLAIN })
	public String getRevenue() throws DatabaseException {
		try (Timer.Context timer = getTimer(this.servletContext,
				"/admin/stats/revenue")) {
			Date begin = DateUtils.truncate(
					DateUtils.addDays(new Date(), -365), Calendar.MONTH);
			Date end = DateUtils.addDays(new Date(), 1);

			TransactionDao dao = DaoFactory.getTransactionDao();
			SortedMap<Date, BigDecimal> data = dao.getSumByMonth(begin, end);

			StringBuilder ret = new StringBuilder();
			ret.append("index\tamount\n");
			int index = 0;
			for (Entry<Date, BigDecimal> entry : data.entrySet()) {
				ret.append(index++);
				ret.append("\t");
				ret.append(entry.getValue().toPlainString());
				ret.append("\n");
			}
			return ret.toString();
		}
	}
}
