package com.arcblaze.arctime.db.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;

import com.arcblaze.arctime.db.ConnectionManager;
import com.arcblaze.arctime.db.DatabaseException;
import com.arcblaze.arctime.db.dao.AssignmentDao;
import com.arcblaze.arctime.model.Assignment;
import com.arcblaze.arctime.model.PayPeriod;

/**
 * Manages assignments within the back-end database.
 */
public class JdbcAssignmentDao implements AssignmentDao {
	protected Assignment fromResultSet(ResultSet rs) throws SQLException {
		Assignment assignment = new Assignment();
		assignment.setId(rs.getInt("id"));
		assignment.setCompanyId(rs.getInt("company_id"));
		assignment.setTaskId(rs.getInt("task_id"));
		assignment.setUserId(rs.getInt("user_id"));
		assignment.setLaborCat(rs.getString("labor_cat"));
		assignment.setItemName(rs.getString("item_name"));
		Date begin = rs.getDate("begin");
		if (begin != null)
			assignment.setBegin(begin);
		Date end = rs.getDate("end");
		if (end != null)
			assignment.setEnd(end);
		return assignment;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Assignment get(Integer companyId, Integer assignmentId)
			throws DatabaseException {
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");
		if (assignmentId == null)
			throw new IllegalArgumentException("Invalid null assignment id");

		String sql = "SELECT * FROM assignments WHERE company_id = ? AND id = ?";

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, companyId);
			ps.setInt(2, assignmentId);

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
	public Set<Assignment> getForUser(Integer companyId, Integer userId,
			Date day) throws DatabaseException {
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");
		if (userId == null)
			throw new IllegalArgumentException("Invalid null user id");

		String sql = "SELECT * FROM assignments WHERE company_id = ? AND "
				+ "user_id = ?";

		if (day != null) {
			sql += " AND (begin IS NULL OR begin <= ?)";
			sql += " AND (end IS NULL OR end >= ?)";
		}

		Set<Assignment> assignments = new TreeSet<>();
		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, companyId);
			ps.setInt(2, userId);
			if (day != null) {
				ps.setTimestamp(3, new Timestamp(day.getTime()));
				ps.setTimestamp(4, new Timestamp(day.getTime()));
			}

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next())
					assignments.add(fromResultSet(rs));
			}

			return assignments;
		} catch (SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * Used for timesheet enrichment.
	 */
	protected Set<Assignment> getForPayPeriod(Connection conn,
			Integer companyId, Integer userId, PayPeriod payPeriod)
			throws DatabaseException {
		if (conn == null)
			throw new IllegalArgumentException("Invalid null connection");
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");
		if (userId == null)
			throw new IllegalArgumentException("Invalid null user id");
		if (payPeriod == null)
			throw new IllegalArgumentException("Invalid null pay period");

		String sql = "SELECT * FROM assignments WHERE company_id = ? AND "
				+ "user_id = ? AND (begin IS NULL OR begin <= ?) AND "
				+ "(end IS NULL OR end >= ?)";

		Set<Assignment> assignments = new TreeSet<>();
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, companyId);
			ps.setInt(2, userId);
			ps.setTimestamp(3, new Timestamp(payPeriod.getEnd().getTime()));
			ps.setTimestamp(4, new Timestamp(payPeriod.getBegin().getTime()));

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next())
					assignments.add(fromResultSet(rs));
			}

			return assignments;
		} catch (SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(Integer companyId, Collection<Assignment> assignments)
			throws DatabaseException {
		if (assignments == null || assignments.isEmpty())
			return;
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");

		String sql = "INSERT INTO assignments (company_id, task_id, "
				+ "user_id, labor_cat, item_name, begin, end) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?)";

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql,
						Statement.RETURN_GENERATED_KEYS)) {
			for (Assignment assignment : assignments) {
				int index = 1;
				assignment.setCompanyId(companyId);
				ps.setInt(index++, assignment.getCompanyId());
				ps.setInt(index++, assignment.getTaskId());
				ps.setInt(index++, assignment.getUserId());
				ps.setString(index++, assignment.getLaborCat());
				ps.setString(index++, assignment.getItemName());
				if (assignment.getBegin() == null)
					ps.setNull(index++, Types.DATE);
				else
					ps.setTimestamp(index++, new Timestamp(assignment
							.getBegin().getTime()));
				if (assignment.getEnd() == null)
					ps.setNull(index++, Types.DATE);
				else
					ps.setTimestamp(index++, new Timestamp(assignment.getEnd()
							.getTime()));
				ps.executeUpdate();

				try (ResultSet rs = ps.getGeneratedKeys()) {
					if (rs.next())
						assignment.setId(rs.getInt(1));
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
	public void update(Integer companyId, Collection<Assignment> assignments)
			throws DatabaseException {
		if (assignments == null || assignments.isEmpty())
			return;
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");

		String sql = "UPDATE assignments SET task_id = ?, user_id = ?, "
				+ "labor_cat = ?, item_name = ?, begin = ?, end = ? "
				+ "WHERE id = ? AND company_id = ?";

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			for (Assignment assignment : assignments) {
				int index = 1;
				ps.setInt(index++, assignment.getTaskId());
				ps.setInt(index++, assignment.getUserId());
				ps.setString(index++, assignment.getLaborCat());
				ps.setString(index++, assignment.getItemName());
				if (assignment.getBegin() == null)
					ps.setNull(index++, Types.DATE);
				else
					ps.setTimestamp(index++, new Timestamp(assignment
							.getBegin().getTime()));
				if (assignment.getEnd() == null)
					ps.setNull(index++, Types.DATE);
				else
					ps.setTimestamp(index++, new Timestamp(assignment.getEnd()
							.getTime()));
				ps.setInt(index++, assignment.getId());
				ps.setInt(index++, companyId);
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
	public void delete(Integer companyId, Collection<Integer> ids)
			throws DatabaseException {
		if (ids == null || ids.isEmpty())
			return;
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");

		String sql = String.format(
				"DELETE FROM assignments WHERE company_id = %d AND id IN (%s)",
				companyId, StringUtils.join(ids, ","));

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.executeUpdate();
		} catch (SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}
}
