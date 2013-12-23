package com.arcblaze.arctime.db.dao.jdbc;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;

import com.arcblaze.arctime.db.ConnectionManager;
import com.arcblaze.arctime.db.DatabaseException;
import com.arcblaze.arctime.db.dao.TimesheetDao;
import com.arcblaze.arctime.model.AuditLog;
import com.arcblaze.arctime.model.Enrichment;
import com.arcblaze.arctime.model.Holiday;
import com.arcblaze.arctime.model.PayPeriod;
import com.arcblaze.arctime.model.Task;
import com.arcblaze.arctime.model.Timesheet;
import com.arcblaze.arctime.model.User;
import com.arcblaze.arctime.model.util.HolidayConfigurationException;

/**
 * Manages users within the back-end database.
 */
public class JdbcTimesheetDao implements TimesheetDao {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Timesheet get(Integer companyId, Integer timesheetId,
			Enrichment... enrichments) throws DatabaseException,
			HolidayConfigurationException {
		Set<Enrichment> enrichmentSet = enrichments == null ? null
				: new LinkedHashSet<>(Arrays.asList(enrichments));
		return this.get(companyId, timesheetId, enrichmentSet);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Timesheet get(Integer companyId, Integer timesheetId,
			Set<Enrichment> enrichments) throws DatabaseException,
			HolidayConfigurationException {
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");
		if (timesheetId == null)
			throw new IllegalArgumentException("Invalid null timesheet id");

		String sql = "SELECT t.* FROM timesheets t JOIN users e ON "
				+ "(t.user_id = e.id AND e.company_id = ?) WHERE t.id = ?";

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, companyId);
			ps.setInt(2, timesheetId);

			try (ResultSet rs = ps.executeQuery();) {
				if (rs.next()) {
					Timesheet timesheet = new Timesheet();
					timesheet.setId(rs.getInt("id"));
					timesheet.setCompanyId(rs.getInt("company_id"));
					timesheet.setUserId(rs.getInt("user_id"));
					timesheet.setBegin(rs.getDate("pp_begin"));
					timesheet.setCompleted(rs.getBoolean("completed"));
					timesheet.setApproved(rs.getBoolean("approved"));
					timesheet.setVerified(rs.getBoolean("verified"));
					timesheet.setExported(rs.getBoolean("exported"));

					int approverId = rs.getInt("approver_id");
					if (!rs.wasNull())
						timesheet.setApproverId(approverId);
					int verifierId = rs.getInt("verifier_id");
					if (!rs.wasNull())
						timesheet.setVerifierId(verifierId);
					int exporterId = rs.getInt("exporter_id");
					if (!rs.wasNull())
						timesheet.setExporterId(exporterId);

					enrich(conn, companyId, Collections.singleton(timesheet),
							enrichments);

					return timesheet;
				}
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
	public Set<Timesheet> getGroup(Integer companyId,
			Set<Integer> timesheetIds, Enrichment... enrichments)
			throws DatabaseException, HolidayConfigurationException {
		Set<Enrichment> enrichmentSet = enrichments == null ? null
				: new LinkedHashSet<>(Arrays.asList(enrichments));
		return this.getGroup(companyId, timesheetIds, enrichmentSet);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<Timesheet> getGroup(Integer companyId,
			Set<Integer> timesheetIds, Set<Enrichment> enrichments)
			throws DatabaseException, HolidayConfigurationException {
		if (timesheetIds == null || timesheetIds.isEmpty())
			return Collections.emptySet();
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");

		String sql = String.format("SELECT t.* FROM timesheets t JOIN "
				+ "users e ON (t.user_id = e.id AND "
				+ "e.company_id = %d) WHERE t.id IN (%s)", companyId,
				StringUtils.join(timesheetIds, ","));

		Set<Timesheet> timesheets = new TreeSet<>();
		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				Timesheet timesheet = new Timesheet();
				timesheet.setId(rs.getInt("id"));
				timesheet.setCompanyId(rs.getInt("company_id"));
				timesheet.setUserId(rs.getInt("user_id"));
				timesheet.setBegin(rs.getDate("pp_begin"));
				timesheet.setCompleted(rs.getBoolean("completed"));
				timesheet.setApproved(rs.getBoolean("approved"));
				timesheet.setVerified(rs.getBoolean("verified"));
				timesheet.setExported(rs.getBoolean("exported"));

				int approverId = rs.getInt("approver_id");
				if (!rs.wasNull())
					timesheet.setApproverId(approverId);
				int verifierId = rs.getInt("verifier_id");
				if (!rs.wasNull())
					timesheet.setVerifierId(verifierId);
				int exporterId = rs.getInt("exporter_id");
				if (!rs.wasNull())
					timesheet.setExporterId(exporterId);

				timesheets.add(timesheet);
			}

			enrich(conn, companyId, timesheets, enrichments);

			return timesheets;
		} catch (SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Timesheet getForUser(Integer companyId, Integer userId,
			PayPeriod payPeriod, Enrichment... enrichments)
			throws DatabaseException, HolidayConfigurationException {
		Set<Enrichment> enrichmentSet = enrichments == null ? null
				: new LinkedHashSet<>(Arrays.asList(enrichments));
		return this.getForUser(companyId, userId, payPeriod, enrichmentSet);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Timesheet getForUser(Integer companyId, Integer userId,
			PayPeriod payPeriod, Set<Enrichment> enrichments)
			throws DatabaseException, HolidayConfigurationException {
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");
		if (userId == null)
			throw new IllegalArgumentException("Invalid null user id");
		if (payPeriod == null)
			throw new IllegalArgumentException("Invalid null pay period");

		String sql = "SELECT t.* FROM timesheets t JOIN users e ON "
				+ "(t.user_id = e.id AND e.company_id = ?) "
				+ "WHERE t.user_id = ? AND t.pp_begin = ?";

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, companyId);
			ps.setInt(2, userId);
			ps.setDate(3, new Date(payPeriod.getBegin().getTime()));

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					Timesheet timesheet = new Timesheet();
					timesheet.setId(rs.getInt("id"));
					timesheet.setCompanyId(rs.getInt("company_id"));
					timesheet.setUserId(rs.getInt("user_id"));
					timesheet.setBegin(rs.getDate("pp_begin"));
					timesheet.setCompleted(rs.getBoolean("completed"));
					timesheet.setApproved(rs.getBoolean("approved"));
					timesheet.setVerified(rs.getBoolean("verified"));
					timesheet.setExported(rs.getBoolean("exported"));

					int approverId = rs.getInt("approver_id");
					if (!rs.wasNull())
						timesheet.setApproverId(approverId);
					int verifierId = rs.getInt("verifier_id");
					if (!rs.wasNull())
						timesheet.setVerifierId(verifierId);
					int exporterId = rs.getInt("exporter_id");
					if (!rs.wasNull())
						timesheet.setExporterId(exporterId);

					enrich(conn, companyId, Collections.singleton(timesheet),
							enrichments);

					return timesheet;
				}
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
	public Timesheet getLatestForUser(Integer companyId, Integer userId,
			Enrichment... enrichments) throws DatabaseException,
			HolidayConfigurationException {
		Set<Enrichment> enrichmentSet = enrichments == null ? null
				: new LinkedHashSet<>(Arrays.asList(enrichments));
		return getLatestForUser(companyId, userId, enrichmentSet);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Timesheet getLatestForUser(Integer companyId, Integer userId,
			Set<Enrichment> enrichments) throws DatabaseException,
			HolidayConfigurationException {
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");
		if (userId == null)
			throw new IllegalArgumentException("Invalid null user id");

		String sql = "SELECT * FROM timesheets WHERE company_id = ? AND "
				+ "user_id = ? ORDER BY completed, pp_begin DESC LIMIT 1";

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, companyId);
			ps.setInt(2, userId);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					Timesheet timesheet = new Timesheet();
					timesheet.setId(rs.getInt("id"));
					timesheet.setCompanyId(rs.getInt("company_id"));
					timesheet.setUserId(rs.getInt("user_id"));
					timesheet.setBegin(rs.getDate("pp_begin"));
					timesheet.setCompleted(rs.getBoolean("completed"));
					timesheet.setApproved(rs.getBoolean("approved"));
					timesheet.setVerified(rs.getBoolean("verified"));
					timesheet.setExported(rs.getBoolean("exported"));

					int approverId = rs.getInt("approver_id");
					if (!rs.wasNull())
						timesheet.setApproverId(approverId);
					int verifierId = rs.getInt("verifier_id");
					if (!rs.wasNull())
						timesheet.setVerifierId(verifierId);
					int exporterId = rs.getInt("exporter_id");
					if (!rs.wasNull())
						timesheet.setExporterId(exporterId);

					enrich(conn, companyId, Collections.singleton(timesheet),
							enrichments);

					return timesheet;
				}
			}

			return null;
		} catch (SQLException sqlException) {
			throw new DatabaseException(sqlException);
		} catch (Throwable th) {
			th.printStackTrace();
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(Integer companyId, Timesheet... timesheets)
			throws DatabaseException {
		this.add(companyId,
				timesheets == null ? null : Arrays.asList(timesheets));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(Integer companyId, Collection<Timesheet> timesheets)
			throws DatabaseException {
		if (timesheets == null || timesheets.isEmpty())
			return;
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");

		String sql = "INSERT INTO timesheets (company_id, user_id, "
				+ "pp_begin, completed, approved, verified, exported) VALUES "
				+ "(?, ?, ?, ?, ?, ?, ?)";

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql,
						Statement.RETURN_GENERATED_KEYS)) {
			for (Timesheet timesheet : timesheets) {
				int index = 1;
				timesheet.setCompanyId(companyId);
				ps.setInt(index++, timesheet.getCompanyId());
				ps.setInt(index++, timesheet.getUserId());
				ps.setDate(index++, new Date(timesheet.getBegin().getTime()));
				ps.setBoolean(index++, timesheet.isCompleted());
				ps.setBoolean(index++, timesheet.isApproved());
				ps.setBoolean(index++, timesheet.isVerified());
				ps.setBoolean(index++, timesheet.isExported());
				ps.executeUpdate();

				try (ResultSet rs = ps.getGeneratedKeys()) {
					if (rs.next())
						timesheet.setId(rs.getInt(1));
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
	public void complete(Integer companyId, boolean completed,
			Integer... timesheetIds) throws DatabaseException {
		this.complete(companyId, completed, timesheetIds == null ? null
				: Arrays.asList(timesheetIds));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void complete(Integer companyId, boolean completed,
			Collection<Integer> timesheetIds) throws DatabaseException {
		if (timesheetIds == null || timesheetIds.isEmpty())
			return;
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");

		String sql = "UPDATE timesheets SET completed = ? "
				+ "WHERE id = ? AND company_id = ?";

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			for (Integer timesheetId : timesheetIds) {
				int index = 1;
				ps.setBoolean(index++, completed);
				ps.setInt(index++, timesheetId);
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
	public void approve(Integer companyId, boolean approved, User approver,
			Integer... timesheetIds) throws DatabaseException {
		this.approve(companyId, approved, approver, timesheetIds == null ? null
				: Arrays.asList(timesheetIds));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void approve(Integer companyId, boolean approved, User approver,
			Collection<Integer> timesheetIds) throws DatabaseException {
		if (timesheetIds == null || timesheetIds.isEmpty())
			return;
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");
		if (approved && approver == null)
			throw new IllegalArgumentException("Invalid null approver");

		String sql = "UPDATE timesheets SET approved = ?, approver_id = ? "
				+ "WHERE id = ? AND company_id = ?";

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			for (Integer timesheetId : timesheetIds) {
				int index = 1;
				ps.setBoolean(index++, approved);
				if (approved)
					ps.setInt(index++, approver.getId());
				else
					ps.setNull(index++, Types.INTEGER);
				ps.setInt(index++, timesheetId);
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
	public void verify(Integer companyId, boolean verified, User verifier,
			Integer... timesheetIds) throws DatabaseException {
		this.verify(companyId, verified, verifier, timesheetIds == null ? null
				: Arrays.asList(timesheetIds));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void verify(Integer companyId, boolean verified, User verifier,
			Collection<Integer> timesheetIds) throws DatabaseException {
		if (timesheetIds == null || timesheetIds.isEmpty())
			return;
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");
		if (verified && verifier == null)
			throw new IllegalArgumentException("Invalid null verifier");

		String sql = "UPDATE timesheets SET verified = ?, verifier_id = ? "
				+ "WHERE id = ? AND company_id = ?";

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			for (Integer timesheetId : timesheetIds) {
				int index = 1;
				ps.setBoolean(index++, verified);
				if (verified)
					ps.setInt(index++, verifier.getId());
				else
					ps.setNull(index++, Types.INTEGER);
				ps.setInt(index++, timesheetId);
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
	public void export(Integer companyId, boolean exported, User exporter,
			Integer... timesheetIds) throws DatabaseException {
		this.export(companyId, exported, exporter, timesheetIds == null ? null
				: Arrays.asList(timesheetIds));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void export(Integer companyId, boolean exported, User exporter,
			Collection<Integer> timesheetIds) throws DatabaseException {
		if (timesheetIds == null || timesheetIds.isEmpty())
			return;
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");
		if (exported && exporter == null)
			throw new IllegalArgumentException("Invalid null exporter");

		String sql = "UPDATE timesheets SET exported = ?, exporter_id = ? "
				+ "WHERE id = ? AND company_id = ?";

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			for (Integer timesheetId : timesheetIds) {
				int index = 1;
				ps.setBoolean(index++, exported);
				if (exported)
					ps.setInt(index++, exporter.getId());
				else
					ps.setNull(index++, Types.INTEGER);
				ps.setInt(index++, timesheetId);
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
				"DELETE FROM timesheets WHERE company_id = %d AND id IN (%s)",
				companyId, StringUtils.join(timesheetIds, ","));

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.executeUpdate();
		} catch (SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	protected void enrich(Connection conn, Integer companyId,
			Set<Timesheet> timesheets, Set<Enrichment> enrichments)
			throws DatabaseException, HolidayConfigurationException {
		if (timesheets == null || timesheets.isEmpty())
			return;
		if (enrichments == null || enrichments.isEmpty())
			return;

		if (conn == null)
			throw new IllegalArgumentException("Invalid null connection");

		for (Enrichment enrichment : enrichments) {
			if (enrichment == Enrichment.USERS)
				enrichWithUsers(conn, companyId, timesheets);
			else if (enrichment == Enrichment.PAY_PERIODS)
				enrichWithPayPeriods(conn, companyId, timesheets);
			else if (enrichment == Enrichment.HOLIDAYS)
				enrichWithHolidays(conn, companyId, timesheets);
			else if (enrichment == Enrichment.AUDIT_LOGS)
				enrichWithAuditLogs(conn, companyId, timesheets);
			else if (enrichment == Enrichment.TASKS)
				enrichWithTasks(conn, companyId, timesheets);
			else
				throw new DatabaseException("Invalid enrichment specified: "
						+ enrichment);
		}
	}

	protected void enrichWithUsers(Connection conn, Integer companyId,
			Set<Timesheet> timesheets) throws DatabaseException {
		final Map<Integer, User> userMap = new JdbcUserDao().getForTimesheets(
				conn, companyId, timesheets);

		for (Timesheet timesheet : timesheets) {
			if (timesheet.getUserId() != null)
				timesheet.setUser(userMap.get(timesheet.getUserId()));
			if (timesheet.getApproverId() != null)
				timesheet.setApprover(userMap.get(timesheet.getApproverId()));
			if (timesheet.getVerifierId() != null)
				timesheet.setVerifier(userMap.get(timesheet.getVerifierId()));
			if (timesheet.getExporterId() != null)
				timesheet.setExporter(userMap.get(timesheet.getExporterId()));
		}

		userMap.clear();
	}

	protected void enrichWithPayPeriods(Connection conn, Integer companyId,
			Set<Timesheet> timesheets) throws DatabaseException {
		Set<Integer> ids = getTimesheetIds(timesheets);
		if (ids.isEmpty())
			return;

		Map<Integer, Timesheet> timesheetMap = getTimesheetMap(timesheets);
		Map<Integer, PayPeriod> payPeriodMap = new JdbcPayPeriodDao()
				.getForTimesheets(conn, companyId, ids);

		for (Entry<Integer, PayPeriod> entry : payPeriodMap.entrySet()) {
			Timesheet timesheet = timesheetMap.get(entry.getKey());
			if (timesheet != null)
				timesheet.setPayPeriod(entry.getValue());
		}

		ids.clear();
		timesheetMap.clear();
		payPeriodMap.clear();
	}

	protected void enrichWithHolidays(Connection conn, Integer companyId,
			Set<Timesheet> timesheets) throws DatabaseException,
			HolidayConfigurationException {
		Set<Holiday> holidays = new JdbcHolidayDao().getAll(companyId);

		for (Timesheet timesheet : timesheets) {
			PayPeriod pp = timesheet.getPayPeriod();
			if (pp == null)
				continue;

			for (Holiday holiday : holidays)
				if (pp.contains(holiday))
					timesheet.addHolidays(holiday);
		}

		holidays.clear();
	}

	protected void enrichWithAuditLogs(Connection conn, Integer companyId,
			Set<Timesheet> timesheets) throws DatabaseException {
		Set<Integer> ids = getTimesheetIds(timesheets);
		if (ids.isEmpty())
			return;

		Map<Integer, Timesheet> timesheetMap = getTimesheetMap(timesheets);
		Map<Integer, Set<AuditLog>> auditLogMap = new JdbcAuditLogDao()
				.getForTimesheets(conn, companyId, ids);

		for (Entry<Integer, Set<AuditLog>> entry : auditLogMap.entrySet()) {
			Timesheet timesheet = timesheetMap.get(entry.getKey());
			if (timesheet != null)
				timesheet.setAuditLogs(entry.getValue());
		}

		ids.clear();
		timesheetMap.clear();
		auditLogMap.clear();
	}

	protected void enrichWithTasks(Connection conn, Integer companyId,
			Set<Timesheet> timesheets) throws DatabaseException {
		if (timesheets.isEmpty())
			return;

		Map<PayPeriod, Map<Integer, Timesheet>> userGroups = new HashMap<>();
		for (Timesheet timesheet : timesheets) {
			PayPeriod payPeriod = timesheet.getPayPeriod();
			if (payPeriod == null)
				continue;

			Map<Integer, Timesheet> userIds = userGroups.get(payPeriod);
			if (userIds == null) {
				userIds = new TreeMap<>();
				userGroups.put(payPeriod, userIds);
			}
			userIds.put(timesheet.getUserId(), timesheet);
		}

		JdbcTaskDao taskDao = new JdbcTaskDao();
		for (Entry<PayPeriod, Map<Integer, Timesheet>> entry : userGroups
				.entrySet()) {
			Map<Integer, Set<Task>> userTasks = taskDao.getForPayPeriod(conn,
					companyId, entry.getKey(), entry.getValue().keySet());

			for (Entry<Integer, Set<Task>> taskEntry : userTasks.entrySet()) {
				if (taskEntry.getKey() == 0) {
					for (Timesheet timesheet : timesheets)
						timesheet.addTasks(taskEntry.getValue());
				} else {
					Timesheet timesheet = entry.getValue().get(
							taskEntry.getKey());
					timesheet.addTasks(taskEntry.getValue());
				}
			}
		}
	}

	protected SortedSet<Integer> getTimesheetIds(Set<Timesheet> timesheets) {
		SortedSet<Integer> ids = new TreeSet<>();
		for (Timesheet timesheet : timesheets)
			if (timesheet.getId() != null)
				ids.add(timesheet.getId());
		return ids;
	}

	protected Map<Integer, Timesheet> getTimesheetMap(Set<Timesheet> timesheets) {
		Map<Integer, Timesheet> map = new HashMap<>();
		for (Timesheet timesheet : timesheets)
			if (timesheet.getId() != null)
				map.put(timesheet.getId(), timesheet);
		return map;
	}
}
