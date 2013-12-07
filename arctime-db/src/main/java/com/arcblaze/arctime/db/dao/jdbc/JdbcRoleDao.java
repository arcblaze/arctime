package com.arcblaze.arctime.db.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
 * Manages employee roles within the back-end database.
 */
public class JdbcRoleDao implements RoleDao {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<Role> get(Integer employeeId) throws DatabaseException {
		if (employeeId == null)
			throw new IllegalArgumentException("Invalid null employee id");

		String sql = "SELECT * FROM roles WHERE employee_id = ?";

		Set<Role> roles = new TreeSet<>();
		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, employeeId);

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
	 * Provides enrichment support for the employee DAO.
	 */
	protected Map<Integer, Set<Role>> get(Connection conn,
			Set<Integer> employeeIds) throws DatabaseException {
		if (employeeIds.isEmpty())
			return Collections.emptyMap();
		if (conn == null)
			throw new IllegalArgumentException("Invalid null connection");

		String sql = String.format(
				"SELECT * FROM roles WHERE employee_id IN (%s)",
				StringUtils.join(employeeIds, ","));

		Map<Integer, Set<Role>> roleMap = new TreeMap<>();
		try (PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				int employeeId = rs.getInt("employee_id");
				Role role = Role.parse(rs.getString("name"));

				Set<Role> roles = roleMap.get(employeeId);
				if (roles == null) {
					roles = new TreeSet<>();
					roleMap.put(employeeId, roles);
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
	public void add(Integer employeeId, Collection<Role> roles)
			throws DatabaseException {
		if (roles == null || roles.isEmpty())
			return;
		if (employeeId == null)
			throw new IllegalArgumentException("Invalid null employee id");

		String sql = "INSERT INTO roles (name, employee_id) VALUES (?, ?)";

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			for (Role role : roles) {
				ps.setString(1, role.name());
				ps.setInt(2, employeeId);
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
	public void delete(Integer employeeId, Collection<Role> roles)
			throws DatabaseException {
		if (roles == null || roles.isEmpty())
			return;
		if (employeeId == null)
			throw new IllegalArgumentException("Invalid null employee id");

		Set<Role> roleSet = new TreeSet<>(roles);

		String sql = String.format("DELETE FROM roles WHERE name IN ('%s') "
				+ "AND employee_id = %d", StringUtils.join(roleSet, "', '"),
				employeeId);

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.executeUpdate();
		} catch (SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}
}
