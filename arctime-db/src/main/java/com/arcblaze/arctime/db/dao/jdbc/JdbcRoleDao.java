package com.arcblaze.arctime.db.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import com.arcblaze.arctime.db.dao.RoleDao;
import com.arcblaze.arctime.model.Role;

/**
 * Manages user roles within the back-end database.
 */
public class JdbcRoleDao implements RoleDao {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<Role> get(Integer userId) throws DatabaseException {
		if (userId == null)
			throw new IllegalArgumentException("Invalid null user id");

		String sql = "SELECT * FROM roles WHERE user_id = ?";

		Set<Role> roles = new TreeSet<>();
		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, userId);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next())
					roles.add(Role.parse(rs.getString("name")));
			}
		} catch (SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
		return roles;
	}

	/**
	 * Provides enrichment support for the user DAO.
	 */
	protected Map<Integer, Set<Role>> get(Connection conn, Set<Integer> userIds)
			throws DatabaseException {
		if (userIds.isEmpty())
			return Collections.emptyMap();
		if (conn == null)
			throw new IllegalArgumentException("Invalid null connection");

		String sql = String.format("SELECT * FROM roles WHERE user_id IN (%s)",
				StringUtils.join(userIds, ","));

		Map<Integer, Set<Role>> roleMap = new TreeMap<>();
		try (PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				int userId = rs.getInt("user_id");
				Role role = Role.parse(rs.getString("name"));

				Set<Role> roles = roleMap.get(userId);
				if (roles == null) {
					roles = new TreeSet<>();
					roleMap.put(userId, roles);
				}
				roles.add(role);
			}
		} catch (SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
		return roleMap;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(Integer userId, Role... roles) throws DatabaseException {
		this.add(userId, roles == null ? null : Arrays.asList(roles));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(Integer userId, Collection<Role> roles)
			throws DatabaseException {
		if (roles == null || roles.isEmpty())
			return;
		if (userId == null)
			throw new IllegalArgumentException("Invalid null user id");

		String sql = "INSERT INTO roles (name, user_id) VALUES (?, ?)";

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			for (Role role : roles) {
				ps.setString(1, role.name());
				ps.setInt(2, userId);
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
	public void delete(Integer userId, Role... roles) throws DatabaseException {
		this.delete(userId, roles == null ? null : Arrays.asList(roles));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete(Integer userId, Collection<Role> roles)
			throws DatabaseException {
		if (roles == null || roles.isEmpty())
			return;
		if (userId == null)
			throw new IllegalArgumentException("Invalid null user id");

		Set<Role> roleSet = new TreeSet<>(roles);

		String sql = String
				.format("DELETE FROM roles WHERE name IN ('%s') "
						+ "AND user_id = %d",
						StringUtils.join(roleSet, "', '"), userId);

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.executeUpdate();
		} catch (SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}
}
