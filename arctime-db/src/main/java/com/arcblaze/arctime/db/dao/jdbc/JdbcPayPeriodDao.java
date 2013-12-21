package com.arcblaze.arctime.db.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;

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
