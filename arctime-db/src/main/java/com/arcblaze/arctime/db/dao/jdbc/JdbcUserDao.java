package com.arcblaze.arctime.db.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
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
import com.arcblaze.arctime.db.DatabaseUniqueConstraintException;
import com.arcblaze.arctime.db.dao.UserDao;
import com.arcblaze.arctime.model.Enrichment;
import com.arcblaze.arctime.model.Role;
import com.arcblaze.arctime.model.Timesheet;
import com.arcblaze.arctime.model.User;

/**
 * Manages users within the back-end database.
 */
public class JdbcUserDao implements UserDao {
	protected User fromResultSet(ResultSet rs, boolean includePass)
			throws SQLException {
		User user = new User();
		user.setId(rs.getInt("id"));
		user.setCompanyId(rs.getInt("company_id"));
		user.setLogin(rs.getString("login"));
		if (includePass) {
			user.setHashedPass(rs.getString("hashed_pass"));
			user.setSalt(rs.getString("salt"));
		}
		user.setEmail(rs.getString("email"));
		user.setFirstName(rs.getString("first_name"));
		user.setLastName(rs.getString("last_name"));
		user.setActive(rs.getBoolean("active"));
		return user;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int count(boolean includeInactive) throws DatabaseException {
		String sql = "SELECT COUNT(*) FROM users"
				+ (includeInactive ? "" : " WHERE active = TRUE");

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			if (rs.next())
				return rs.getInt(1);
			return 0;
		} catch (SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public User getLogin(String login) throws DatabaseException {
		if (StringUtils.isBlank(login))
			throw new IllegalArgumentException("Invalid blank login");

		String sql = "SELECT * FROM users WHERE active = true AND "
				+ "(login = ? OR LOWER(email) = LOWER(?))";

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, login);
			ps.setString(2, login);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return fromResultSet(rs, true);
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
	public User get(Integer companyId, Integer userId,
			Enrichment... enrichments) throws DatabaseException {
		Set<Enrichment> enrichmentSet = enrichments == null ? null
				: new LinkedHashSet<>(Arrays.asList(enrichments));
		return this.get(companyId, userId, enrichmentSet);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public User get(Integer companyId, Integer userId,
			Set<Enrichment> enrichments) throws DatabaseException {
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");
		if (userId == null)
			throw new IllegalArgumentException("Invalid null user id");

		String sql = "SELECT * FROM users WHERE company_id = ? AND id = ?";

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, companyId);
			ps.setInt(2, userId);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					User user = fromResultSet(rs, false);

					enrich(conn, companyId, Collections.singleton(user),
							enrichments);

					return user;
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
	public Set<User> getAll(Integer companyId, Enrichment... enrichments)
			throws DatabaseException {
		Set<Enrichment> enrichmentSet = enrichments == null ? null
				: new LinkedHashSet<>(Arrays.asList(enrichments));
		return this.getAll(companyId, enrichmentSet);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<User> getAll(Integer companyId, Set<Enrichment> enrichments)
			throws DatabaseException {
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");

		String sql = "SELECT * FROM users WHERE company_id = ?";

		Set<User> users = new TreeSet<>();
		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, companyId);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next())
					users.add(fromResultSet(rs, false));
			}

			enrich(conn, companyId, users, enrichments);

			return users;
		} catch (SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	protected Map<Integer, User> getForTimesheets(Connection conn,
			Integer companyId, Set<Timesheet> timesheets)
			throws DatabaseException {
		if (timesheets == null || timesheets.isEmpty())
			return Collections.emptyMap();
		if (conn == null)
			throw new IllegalArgumentException("Invalid null connection");
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");

		Set<Integer> userIds = new TreeSet<Integer>();
		for (Timesheet timesheet : timesheets)
			userIds.addAll(timesheet.getUserIds());

		if (userIds.isEmpty())
			return Collections.emptyMap();

		String sql = String.format("SELECT * FROM users WHERE id IN (%s)",
				StringUtils.join(userIds, ","));

		Map<Integer, User> userMap = new TreeMap<>();
		try (PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				User user = fromResultSet(rs, false);
				userMap.put(user.getId(), user);
			}
		} catch (SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}

		return userMap;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(Integer companyId, User... users)
			throws DatabaseUniqueConstraintException, DatabaseException {
		this.add(companyId, users == null ? null : Arrays.asList(users));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(Integer companyId, Collection<User> users)
			throws DatabaseUniqueConstraintException, DatabaseException {
		if (users == null || users.isEmpty())
			return;
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");

		String sql = "INSERT INTO users (company_id, login, hashed_pass, "
				+ "salt, email, first_name, last_name, active) VALUES "
				+ "(?, ?, ?, ?, ?, ?, ?, ?)";

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql,
						Statement.RETURN_GENERATED_KEYS)) {
			for (User user : users) {
				int index = 1;
				user.setCompanyId(companyId);
				ps.setInt(index++, user.getCompanyId());
				ps.setString(index++, user.getLogin());
				ps.setString(index++, user.getHashedPass());
				ps.setString(index++, user.getSalt());
				ps.setString(index++, user.getEmail());
				ps.setString(index++, user.getFirstName());
				ps.setString(index++, user.getLastName());
				ps.setBoolean(index++, user.isActive());
				ps.executeUpdate();

				try (ResultSet rs = ps.getGeneratedKeys()) {
					if (rs.next())
						user.setId(rs.getInt(1));
				}
			}
		} catch (SQLIntegrityConstraintViolationException notUnique) {
			throw new DatabaseUniqueConstraintException(notUnique);
		} catch (SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update(Integer companyId, User... users)
			throws DatabaseUniqueConstraintException, DatabaseException {
		this.update(companyId, users == null ? null : Arrays.asList(users));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update(Integer companyId, Collection<User> users)
			throws DatabaseUniqueConstraintException, DatabaseException {
		if (users == null || users.isEmpty())
			return;
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");

		// NOTE: the hashed_pass and salt values are not updated.

		String sql = "UPDATE users SET login = ?, email = ?, "
				+ "first_name = ?, last_name = ?, active = ? "
				+ "WHERE id = ? AND company_id = ?";

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			for (User user : users) {
				int index = 1;
				ps.setString(index++, user.getLogin());
				ps.setString(index++, user.getEmail());
				ps.setString(index++, user.getFirstName());
				ps.setString(index++, user.getLastName());
				ps.setBoolean(index++, user.isActive());
				ps.setInt(index++, user.getId());
				ps.setInt(index++, companyId);
				ps.executeUpdate();
			}
		} catch (SQLIntegrityConstraintViolationException notUnique) {
			throw new DatabaseUniqueConstraintException(notUnique);
		} catch (SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPassword(Integer userId, String hashedPass, String salt)
			throws DatabaseException {
		if (userId == null)
			throw new IllegalArgumentException("Invalid null user id");
		if (StringUtils.isBlank(hashedPass))
			throw new IllegalArgumentException("Invalid blank password");
		if (StringUtils.isBlank(salt))
			throw new IllegalArgumentException("Invalid blank salt");

		String sql = "UPDATE users SET hashed_pass = ?, salt = ? WHERE id = ?";

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, hashedPass);
			ps.setString(2, salt);
			ps.setInt(3, userId);
			ps.executeUpdate();
		} catch (SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete(Integer companyId, Integer... ids)
			throws DatabaseException {
		this.delete(companyId, ids == null ? null : Arrays.asList(ids));
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
				"DELETE FROM users WHERE company_id = %d AND id IN (%s)",
				companyId, StringUtils.join(ids, ","));

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.executeUpdate();
		} catch (SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<User> getSupervisors(Integer companyId, Integer userId,
			Set<Enrichment> enrichments) throws DatabaseException {
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");
		if (userId == null)
			throw new IllegalArgumentException("Invalid null user id");

		String sql = "SELECT * FROM supervisors s JOIN users e ON "
				+ "(s.supervisor_id = e.id AND s.company_id = e.company_id) "
				+ "WHERE s.company_id = ? AND s.user_id = ?";

		Set<User> supervisors = new TreeSet<>();
		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, companyId);
			ps.setInt(2, userId);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					User user = fromResultSet(rs, false);
					user.setPrimary(rs.getBoolean("is_primary"));
					supervisors.add(user);
				}
			}
		} catch (SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
		return supervisors;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addSupervisors(Integer companyId, Integer userId,
			boolean primary, Integer... supervisorIds) throws DatabaseException {
		this.addSupervisors(companyId, userId, primary,
				supervisorIds == null ? null : Arrays.asList(supervisorIds));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addSupervisors(Integer companyId, Integer userId,
			boolean primary, Collection<Integer> supervisorIds)
			throws DatabaseException {
		if (supervisorIds == null || supervisorIds.isEmpty())
			return;
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");
		if (userId == null)
			throw new IllegalArgumentException("Invalid null user id");

		String sql = "INSERT INTO supervisors (company_id, user_id, "
				+ "supervisor_id, is_primary) VALUES (?, ?, ?, ?)";

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			for (Integer supervisorId : supervisorIds) {
				ps.setInt(1, companyId);
				ps.setInt(2, userId);
				ps.setInt(3, supervisorId);
				ps.setBoolean(4, primary);
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
	public void deleteSupervisors(Integer companyId, Integer userId,
			Integer... supervisorIds) throws DatabaseException {
		this.deleteSupervisors(companyId, userId, supervisorIds == null ? null
				: Arrays.asList(supervisorIds));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteSupervisors(Integer companyId, Integer userId,
			Collection<Integer> supervisorIds) throws DatabaseException {
		if (supervisorIds == null || supervisorIds.isEmpty())
			return;
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");
		if (userId == null)
			throw new IllegalArgumentException("Invalid null user id");

		String sql = "DELETE FROM supervisors WHERE company_id = ? AND "
				+ "user_id = ? AND supervisor_id = ?";

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			for (Integer supervisorId : supervisorIds) {
				ps.setInt(1, companyId);
				ps.setInt(2, userId);
				ps.setInt(3, supervisorId);
				ps.executeUpdate();
			}
		} catch (SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	protected void enrich(Connection conn, Integer companyId, Set<User> users,
			Set<Enrichment> enrichments) throws DatabaseException {
		if (users == null || users.isEmpty())
			return;
		if (enrichments == null || enrichments.isEmpty())
			return;

		if (conn == null)
			throw new IllegalArgumentException("Invalid null connection");

		for (Enrichment enrichment : enrichments) {
			if (enrichment == Enrichment.ROLES)
				enrichWithRoles(conn, users);
			else if (enrichment == Enrichment.SUPERVISED)
				enrichWithSupervised(conn, companyId, users);
			else if (enrichment == Enrichment.SUPERVISERS)
				enrichWithSupervisors(conn, companyId, users);
			else
				throw new DatabaseException("Invalid enrichment specified: "
						+ enrichment);
		}
	}

	protected void enrichWithRoles(Connection conn, Set<User> users)
			throws DatabaseException {
		Set<Integer> ids = getUserIds(users);
		if (ids.isEmpty())
			return;

		Map<Integer, User> userMap = getUserMap(users);
		Map<Integer, Set<Role>> roleMap = new JdbcRoleDao().get(conn, ids);

		for (Entry<Integer, Set<Role>> entry : roleMap.entrySet()) {
			User user = userMap.get(entry.getKey());
			if (user != null)
				user.setRoles(entry.getValue());
		}
	}

	protected void enrichWithSupervised(Connection conn, Integer companyId,
			Set<User> users) throws DatabaseException {
		Set<Integer> ids = getUserIds(users);
		if (ids.isEmpty())
			return;

		Map<Integer, User> userMap = getUserMap(users);

		String sql = String.format("SELECT * FROM users e "
				+ "JOIN supervisors s ON (e.id = s.user_id AND "
				+ "e.company_id = s.company_id) WHERE e.company_id = %d AND "
				+ "s.supervisor_id IN (%s)", companyId,
				StringUtils.join(ids, ","));

		try (PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				User user = fromResultSet(rs, false);
				user.setPrimary(rs.getBoolean("is_primary"));

				int supervisorId = rs.getInt("supervisor_id");

				User supervisor = userMap.get(supervisorId);
				if (supervisor != null)
					supervisor.addSupervised(user);
			}
		} catch (SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	protected void enrichWithSupervisors(Connection conn, Integer companyId,
			Set<User> users) throws DatabaseException {
		Set<Integer> ids = getUserIds(users);
		if (ids.isEmpty())
			return;

		Map<Integer, User> userMap = getUserMap(users);

		String sql = String.format("SELECT * FROM users e "
				+ "JOIN supervisors s ON (e.id = s.supervisor_id AND "
				+ "e.company_id = s.company_id) WHERE e.company_id = %d AND "
				+ "s.user_id IN (%s)", companyId, StringUtils.join(ids, ","));

		try (PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				User user = fromResultSet(rs, false);
				user.setPrimary(rs.getBoolean("is_primary"));

				int userId = rs.getInt("user_id");

				User supervised = userMap.get(userId);
				if (supervised != null)
					supervised.addSupervisors(user);
			}
		} catch (SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	protected SortedSet<Integer> getUserIds(Set<User> users) {
		SortedSet<Integer> ids = new TreeSet<>();
		for (User user : users)
			if (user.getId() != null)
				ids.add(user.getId());
		return ids;
	}

	protected Map<Integer, User> getUserMap(Set<User> users) {
		Map<Integer, User> map = new HashMap<>();
		for (User user : users)
			if (user.getId() != null)
				map.put(user.getId(), user);
		return map;
	}
}
