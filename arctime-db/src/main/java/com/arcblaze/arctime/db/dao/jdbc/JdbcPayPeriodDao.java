package com.arcblaze.arctime.db.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.arcblaze.arctime.db.ConnectionManager;
import com.arcblaze.arctime.db.DatabaseException;
import com.arcblaze.arctime.db.dao.PayPeriodDao;
import com.arcblaze.arctime.model.PayPeriod;
import com.arcblaze.arctime.model.PayPeriodType;

/**
 * Performs operations on pay periods in the system.
 */
public class JdbcPayPeriodDao implements PayPeriodDao {
	protected PayPeriod fromResultSet(ResultSet rs) throws SQLException {
		PayPeriod payPeriod = new PayPeriod();
		payPeriod.setCompanyId(rs.getInt("company_id"));
		payPeriod.setBegin(rs.getDate("begin"));
		payPeriod.setEnd(rs.getDate("end"));
		payPeriod.setType(PayPeriodType.parse(rs.getString("type")));
		return payPeriod;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PayPeriod get(Integer companyId, Date begin)
			throws DatabaseException {
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");
		if (begin == null)
			throw new IllegalArgumentException("Invalid null begin date");

		String sql = "SELECT * FROM pay_periods WHERE company_id = ? AND "
				+ "begin = ?";

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, companyId);
			ps.setDate(2, new java.sql.Date(begin.getTime()));

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return fromResultSet(rs);

				return null;
			}
		} catch (SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PayPeriod getContaining(Integer companyId, Date day)
			throws DatabaseException {
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");
		if (day == null)
			throw new IllegalArgumentException("Invalid null day");

		Date truncated = DateUtils.truncate(day, Calendar.DATE);

		String sql = "SELECT * FROM pay_periods WHERE company_id = ? AND "
				+ "begin <= ? AND end >= ?";

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, companyId);
			ps.setDate(2, new java.sql.Date(truncated.getTime()));
			ps.setDate(3, new java.sql.Date(truncated.getTime()));

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return fromResultSet(rs);
			}

			// A pay period for the requested day does not exist. Create the
			// necessary pay periods and return the result.
			return addThrough(companyId, day);
		} catch (SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * Make sure all of the pay periods exist in the database through the
	 * specified day (either forwards or backwards).
	 * 
	 * @param companyId
	 *            the company for which timesheets should be created
	 * @param day
	 *            the {@link Date} for which pay periods should be created
	 *            through
	 * 
	 * @return the created pay period that contains the specified day, or
	 *         {@code null} if there are no existing pay periods from which to
	 *         derive the pay period that contains the specified day
	 * 
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	protected PayPeriod addThrough(Integer companyId, Date day)
			throws DatabaseException {
		PayPeriod latest = getLatest(companyId);
		PayPeriod earliest = getEarliest(companyId);

		// Make sure at least one pay period exists in the database for this
		// company.
		if (latest == null || earliest == null)
			return null;

		PayPeriod payPeriod = null;
		if (earliest.isAfter(day))
			payPeriod = earliest;
		else if (latest.isBefore(day))
			payPeriod = latest;
		else {
			// An unexpected situation where a pay period containing the
			// specified date does not exist, though there are pay periods
			// both before and after?
			throw new DatabaseException("Unexpected missing pay period "
					+ "that should contain: " + day);
		}

		List<PayPeriod> toAdd = new LinkedList<PayPeriod>();
		while (!payPeriod.contains(day)) {
			if (payPeriod.isAfter(day))
				payPeriod = payPeriod.getPrevious();
			else
				payPeriod = payPeriod.getNext();
			toAdd.add(payPeriod);
		}

		add(companyId, toAdd);

		return payPeriod;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PayPeriod getCurrent(Integer companyId) throws DatabaseException {
		return getContaining(companyId, new Date());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PayPeriod getEarliest(Integer companyId) throws DatabaseException {
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");

		String sql = "SELECT * FROM pay_periods WHERE company_id = ? "
				+ "ORDER BY begin LIMIT 1";

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, companyId);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return fromResultSet(rs);

				return null;
			}
		} catch (SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PayPeriod getLatest(Integer companyId) throws DatabaseException {
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");

		String sql = "SELECT * FROM pay_periods WHERE company_id = ? "
				+ "ORDER BY begin DESC LIMIT 1";

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, companyId);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return fromResultSet(rs);

				return null;
			}
		} catch (SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * Used to enrich timesheet data.
	 */
	protected Map<Integer, PayPeriod> getForTimesheets(Connection conn,
			Integer companyId, Set<Integer> timesheetIds)
			throws DatabaseException {
		if (timesheetIds == null || timesheetIds.isEmpty())
			return Collections.emptyMap();
		if (conn == null)
			throw new IllegalArgumentException("Invalid null connection");
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");

		String sql = String.format("SELECT t.id, p.* FROM timesheets t "
				+ "JOIN pay_periods p ON (p.begin = t.pp_begin AND "
				+ "p.company_id = t.company_id) WHERE p.company_id = %d AND "
				+ "t.id IN (%s)", companyId,
				StringUtils.join(timesheetIds, ","));

		Map<Integer, PayPeriod> timesheetMap = new TreeMap<>();
		try (PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				PayPeriod payPeriod = fromResultSet(rs);
				int timesheetId = rs.getInt("id");

				timesheetMap.put(timesheetId, payPeriod);
			}

			return timesheetMap;
		} catch (SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(Integer companyId, PayPeriod... payPeriods)
			throws DatabaseException {
		this.add(companyId,
				payPeriods == null ? null : Arrays.asList(payPeriods));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(Integer companyId, Collection<PayPeriod> payPeriods)
			throws DatabaseException {
		if (payPeriods == null || payPeriods.isEmpty())
			return;
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");

		String sql = "INSERT INTO pay_periods (company_id, begin, end, type) "
				+ "VALUES (?, ?, ?, ?)";

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			for (PayPeriod payPeriod : payPeriods) {
				int index = 1;
				payPeriod.setCompanyId(companyId);
				ps.setInt(index++, companyId);
				ps.setDate(index++, new java.sql.Date(payPeriod.getBegin()
						.getTime()));
				ps.setDate(index++, new java.sql.Date(payPeriod.getEnd()
						.getTime()));
				ps.setString(index++, payPeriod.getType().name());
				ps.executeUpdate();
			}
		} catch (SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}
}
