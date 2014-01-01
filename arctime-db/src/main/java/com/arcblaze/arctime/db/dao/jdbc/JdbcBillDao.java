package com.arcblaze.arctime.db.dao.jdbc;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;

import com.arcblaze.arctime.db.ConnectionManager;
import com.arcblaze.arctime.db.DatabaseException;
import com.arcblaze.arctime.db.dao.BillDao;
import com.arcblaze.arctime.model.Bill;

/**
 * Manages bills within the back-end database.
 */
public class JdbcBillDao implements BillDao {
	protected Bill fromResultSet(ResultSet rs) throws SQLException {
		Bill bill = new Bill();
		bill.setId(rs.getInt("id"));
		int assignmentId = rs.getInt("assignment_id");
		if (!rs.wasNull())
			bill.setAssignmentId(assignmentId);
		bill.setTaskId(rs.getInt("task_id"));
		bill.setUserId(rs.getInt("user_id"));
		bill.setDay(rs.getDate("day"));
		bill.setHours(new BigDecimal(rs.getString("hours")));
		bill.setTimestamp(rs.getTimestamp("timestamp"));
		return bill;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Bill get(Integer id) throws DatabaseException {
		if (id == null)
			throw new IllegalArgumentException("Invalid null id");

		String sql = "SELECT * FROM bills WHERE id = ?";

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, id);

			try (ResultSet rs = ps.executeQuery();) {
				if (rs.next())
					return fromResultSet(rs);
			}

			return null;
		} catch (SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<Bill> getForTimesheet(Integer timesheetId)
			throws DatabaseException {
		if (timesheetId == null)
			throw new IllegalArgumentException("Invalid null timesheet id");

		String sql = "SELECT b.* FROM bills b JOIN pay_periods p ON "
				+ "(b.day >= p.begin AND b.day <= p.end) "
				+ "JOIN timesheets t ON (t.pp_begin = p.begin AND t.id = ?)";

		Set<Bill> bills = new TreeSet<>();
		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, timesheetId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next())
					bills.add(fromResultSet(rs));
			}

			return bills;
		} catch (SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	protected Map<Integer, Set<Bill>> getForTimesheets(Connection conn,
			Set<Integer> timesheetIds) throws DatabaseException {
		if (timesheetIds == null || timesheetIds.isEmpty())
			return Collections.emptyMap();

		String sql = String.format(
				"SELECT t.id AS timesheet_id, b.* FROM bills b "
						+ "JOIN pay_periods p ON "
						+ "(b.day >= p.begin AND b.day <= p.end) "
						+ "JOIN timesheets t ON (t.pp_begin = p.begin) "
						+ "WHERE t.id IN (%s)",
				StringUtils.join(timesheetIds, ","));

		Map<Integer, Set<Bill>> billMap = new HashMap<>();
		try (PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				Bill bill = fromResultSet(rs);
				int timesheetId = rs.getInt("timesheet_id");

				Set<Bill> bills = billMap.get(timesheetId);
				if (bills == null) {
					bills = new TreeSet<>();
					billMap.put(timesheetId, bills);
				}
				bills.add(bill);
			}

			return billMap;
		} catch (SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(Bill... bills) throws DatabaseException {
		this.add(bills == null ? null : Arrays.asList(bills));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(Collection<Bill> bills) throws DatabaseException {
		if (bills == null || bills.isEmpty())
			return;

		String sql = "INSERT INTO bills (assignment_id, task_id, user_id, "
				+ "day, hours, timestamp) VALUES (?, ?, ?, ?, ?, ?)";

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql,
						Statement.RETURN_GENERATED_KEYS)) {
			for (Bill bill : bills) {
				int index = 1;
				if (bill.getAssignmentId() != null)
					ps.setInt(index++, bill.getAssignmentId());
				else
					ps.setNull(index++, Types.INTEGER);
				ps.setInt(index++, bill.getTaskId());
				ps.setInt(index++, bill.getUserId());
				ps.setDate(index++, new Date(bill.getDay().getTime()));
				ps.setString(index++, bill.getHours().toPlainString());
				ps.setTimestamp(index++, new Timestamp(bill.getTimestamp()
						.getTime()));
				ps.executeUpdate();

				try (ResultSet rs = ps.getGeneratedKeys()) {
					if (rs.next())
						bill.setId(rs.getInt(1));
				}
			}
		} catch (SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update(Bill... bills) throws DatabaseException {
		this.update(bills == null ? null : Arrays.asList(bills));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update(Collection<Bill> bills) throws DatabaseException {
		if (bills == null || bills.isEmpty())
			return;

		String sql = "UPDATE bills SET assignment_id = ?, task_id = ?, "
				+ "user_id = ?, day = ?, hours = ?, timestamp = ? "
				+ "WHERE id = ?";

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			for (Bill bill : bills) {
				int index = 1;
				if (bill.getAssignmentId() != null)
					ps.setInt(index++, bill.getAssignmentId());
				else
					ps.setNull(index++, Types.INTEGER);
				ps.setInt(index++, bill.getTaskId());
				ps.setInt(index++, bill.getUserId());
				ps.setDate(index++, new Date(bill.getDay().getTime()));
				ps.setString(index++, bill.getHours().toPlainString());
				ps.setTimestamp(index++, new Timestamp(bill.getTimestamp()
						.getTime()));
				ps.setInt(index++, bill.getId());
				ps.executeUpdate();
			}
		} catch (SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete(Integer... ids) throws DatabaseException {
		this.delete(ids == null ? null : Arrays.asList(ids));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete(Collection<Integer> ids) throws DatabaseException {
		if (ids == null || ids.isEmpty())
			return;

		String sql = String.format("DELETE FROM bills WHERE id IN (%s)",
				StringUtils.join(ids, ","));

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.executeUpdate();
		} catch (SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}
}
