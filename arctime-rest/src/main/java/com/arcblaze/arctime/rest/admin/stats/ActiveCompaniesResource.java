package com.arcblaze.arctime.rest.admin.stats;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map.Entry;
import java.util.SortedMap;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.arcblaze.arctime.db.DaoFactory;
import com.arcblaze.arctime.db.DatabaseException;
import com.arcblaze.arctime.db.dao.CompanyDao;
import com.arcblaze.arctime.rest.BaseResource;
import com.codahale.metrics.Timer;

/**
 * The REST end-point for retrieving active company statistics.
 */
@Path("/admin/stats/companies")
public class ActiveCompaniesResource extends BaseResource {
	/** The format used to parse dates passed into this resource. */
	private final static String[] FMT = { "yyyy-MM-dd" };

	@Context
	private ServletContext servletContext;

	/**
	 * @param beginStr
	 *            the beginning date range to use, possibly null
	 * @param endStr
	 *            the ending date range to use, possibly null
	 * 
	 * @return the relevant revenue information
	 * 
	 * @throws DatabaseException
	 *             if there is an error communicating with the back-end
	 */
	@GET
	@Produces({ MediaType.TEXT_PLAIN })
	public String activeCompanies(@QueryParam("begin") String beginStr,
			@QueryParam("end") String endStr) throws DatabaseException {
		try (Timer.Context timer = getTimer(this.servletContext,
				"/admin/stats/users")) {
			Date begin = StringUtils.isBlank(beginStr) ? DateUtils.truncate(
					DateUtils.addDays(new Date(), -365), Calendar.MONTH)
					: DateUtils.parseDate(beginStr, FMT);
			Date end = StringUtils.isBlank(endStr) ? DateUtils.addDays(
					new Date(), 1) : DateUtils.parseDate(endStr, FMT);

			CompanyDao dao = DaoFactory.getCompanyDao();
			SortedMap<Date, Integer> data = dao.getActiveByMonth(begin, end);

			StringBuilder ret = new StringBuilder();
			ret.append("index\tcount\n");
			int index = 1;
			for (Entry<Date, Integer> entry : data.entrySet()) {
				ret.append(index++);
				ret.append("\t");
				ret.append(entry.getValue());
				ret.append("\n");
			}
			return ret.toString();
		} catch (ParseException badDate) {
			throw badRequest("Invalid date: " + badDate.getMessage());
		}
	}
}
