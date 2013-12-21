package com.arcblaze.arctime.db.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;

import com.arcblaze.arctime.db.ConnectionManager;
import com.arcblaze.arctime.db.DatabaseException;
import com.arcblaze.arctime.db.dao.AuditLogDao;
import com.arcblaze.arctime.model.AuditLog;

/**
 * Manages audit logs within the back-end database.
 */
public class JdbcAuditLogDao implements AuditLogDao {
	protected AuditLog fromResultSet(ResultSet rs) throws SQLException {
		AuditLog auditLog = new AuditLog();
		auditLog.setCompanyId(rs.getInt("company_id"));
		auditLog.setTimesheetId(rs.getInt("timesheet_id"));
		auditLog.setLog(rs.getString("log"));
		auditLog.setTimestamp(rs.getTimestamp("timestamp"));
		return auditLog;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<AuditLog> getForTimesheet(Integer companyId, Integer timesheetId)
			throws DatabaseException {
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");
		if (timesheetId == null)
			throw new IllegalArgumentException("Invalid null timesheet id");

		String sql = "SELECT * FROM audit_logs WHERE company_id = ? AND "
				+ "timesheet_id = ?";

		Set<AuditLog> auditLogs = new TreeSet<>();
		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next())
				auditLogs.add(fromResultSet(rs));

			return auditLogs;
		} catch (SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * Used for performing enrichment on timesheets.
	 */
	protected Map<Integer, Set<AuditLog>> getForTimesheets(Connection conn,
			Integer companyId, Set<Integer> timesheetIds)
			throws DatabaseException {
		if (timesheetIds == null || timesheetIds.isEmpty())
			return Collections.emptyMap();
		if (conn == null)
			throw new IllegalArgumentException("Invalid null connection");
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");

		String sql = String.format("SELECT * FROM audit_logs WHERE "
				+ "company_id = %d AND timesheet_id IN (%s)", companyId,
				StringUtils.join(timesheetIds, ","));

		Map<Integer, Set<AuditLog>> auditLogMap = new TreeMap<>();
		try (PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				AuditLog auditLog = fromResultSet(rs);
				Set<AuditLog> auditLogs = auditLogMap.get(auditLog
						.getTimesheetId());
				if (auditLogs == null) {
					auditLogs = new TreeSet<>();
					auditLogMap.put(auditLog.getTimesheetId(), auditLogs);
				}
				auditLogs.add(auditLog);
			}

			return auditLogMap;
		} catch (SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(Integer companyId, AuditLog... auditLogs)
			throws DatabaseException {
		this.add(companyId, auditLogs == null ? null : Arrays.asList(auditLogs));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(Integer companyId, Collection<AuditLog> auditLogs)
			throws DatabaseException {
		if (auditLogs == null || auditLogs.isEmpty())
			return;
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");

		String sql = "INSERT INTO audit_logs (company_id, timesheet_id, log, "
				+ "timestamp) VALUES (?, ?, ?, ?)";

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			for (AuditLog auditLog : auditLogs) {
				int index = 1;
				auditLog.setCompanyId(companyId);
				ps.setInt(index++, companyId);
				ps.setInt(index++, auditLog.getTimesheetId());
				ps.setString(index++, auditLog.getLog());
				ps.setTimestamp(index++, new Timestamp(auditLog.getTimestamp()
						.getTime()));
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
	public void delete(Integer companyId, Integer... timesheetIds)
			throws DatabaseException {
		this.delete(companyId,
				timesheetIds == null ? null : Arrays.asList(timesheetIds));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete(Integer companyId, Collection<Integer> timesheetIds)
			throws DatabaseException {
		if (timesheetIds == null || timesheetIds.isEmpty())
			return;
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");

		String sql = String.format(
				"DELETE FROM audit_logs WHERE company_id = %d AND "
						+ "timesheet_id IN (%s)", companyId,
				StringUtils.join(timesheetIds, ","));

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.executeUpdate();
		} catch (SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}
}
