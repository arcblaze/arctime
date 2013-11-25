package com.arcblaze.arctime.db.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import com.arcblaze.arctime.db.ConnectionManager;
import com.arcblaze.arctime.db.DatabaseException;
import com.arcblaze.arctime.db.dao.EmployeeDao;
import com.arcblaze.arctime.model.Employee;
import com.arcblaze.arctime.model.Enrichment;
import com.arcblaze.arctime.model.PersonnelType;

/**
 * Manages news items within the back-end database.
 */
public class MySqlEmployeeDao implements EmployeeDao {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean exists(Employee employee) throws DatabaseException {
		if (employee == null)
			throw new IllegalArgumentException("Invalid null employee");

		String sql = "SELECT COUNT(*) FROM employees WHERE id = ? OR login = ?";

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			if (employee.getId() == null)
				ps.setNull(1, Types.INTEGER);
			else
				ps.setInt(1, employee.getId());
			ps.setString(2, employee.getLogin());

			try (ResultSet rs = ps.executeQuery();) {
				if (rs.next())
					return rs.getInt(1) > 0;
			}

			return false;
		} catch (SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Employee get(Integer id, Set<Enrichment> enrichments)
			throws DatabaseException {
		if (id == null)
			throw new IllegalArgumentException("Invalid null id");

		String sql = "SELECT * FROM employees WHERE id = ?";

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, id);

			try (ResultSet rs = ps.executeQuery();) {
				if (rs.next()) {
					Employee employee = new Employee();
					employee.setId(rs.getInt("id"));
					employee.setLogin(rs.getString("login"));
					employee.setHashedPass(rs.getString("hashed_pass"));
					employee.setEmail(rs.getString("email"));
					employee.setFirstName(rs.getString("first_name"));
					employee.setLastName(rs.getString("last_name"));
					employee.setSuffix(rs.getString("suffix"));
					employee.setDivision(rs.getString("division"));
					employee.setPersonnelType(PersonnelType.parse(rs
							.getString("personnel_type")));
					employee.setActive(rs.getBoolean("active"));

					enrich(Collections.singleton(employee), enrichments);

					return employee;
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
	public Set<Employee> getAll(Set<Enrichment> enrichments)
			throws DatabaseException {
		String sql = "SELECT * FROM employees";

		Set<Employee> employees = new TreeSet<>();
		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery();) {
			while (rs.next()) {
				Employee employee = new Employee();
				employee.setId(rs.getInt("id"));
				employee.setLogin(rs.getString("login"));
				employee.setHashedPass(rs.getString("hashed_pass"));
				employee.setEmail(rs.getString("email"));
				employee.setFirstName(rs.getString("first_name"));
				employee.setLastName(rs.getString("last_name"));
				employee.setSuffix(rs.getString("suffix"));
				employee.setDivision(rs.getString("division"));
				employee.setPersonnelType(PersonnelType.parse(rs
						.getString("personnel_type")));
				employee.setActive(rs.getBoolean("active"));
				employees.add(employee);
			}

			enrich(employees, enrichments);

			return employees;
		} catch (SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(Collection<Employee> employees) throws DatabaseException {
		if (employees == null || employees.isEmpty())
			return;

		String sql = "INSERT IGNORE INTO employees (login, hashed_pass, "
				+ "email, first_name, last_name, suffix, division, "
				+ "personnel_type, active) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			for (Employee employee : employees) {
				int index = 1;
				ps.setString(index++, employee.getLogin());
				ps.setString(index++, employee.getHashedPass());
				ps.setString(index++, employee.getEmail());
				ps.setString(index++, employee.getFirstName());
				ps.setString(index++, employee.getLastName());
				ps.setString(index++, employee.getSuffix());
				ps.setString(index++, employee.getDivision());
				ps.setString(index++, employee.getPersonnelType().name());
				ps.setBoolean(index++, employee.isActive());
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
	public void update(Collection<Employee> employees) throws DatabaseException {
		if (employees == null || employees.isEmpty())
			return;

		String sql = "UPDATE employees SET login = ?, hashed_pass = ?, "
				+ "email = ?, first_name = ?, last_name = ?, suffix = ?, "
				+ "division = ?, personnel_type = ?, active = ? WHERE id = ?";

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			for (Employee employee : employees) {
				int index = 1;
				ps.setString(index++, employee.getLogin());
				ps.setString(index++, employee.getHashedPass());
				ps.setString(index++, employee.getEmail());
				ps.setString(index++, employee.getFirstName());
				ps.setString(index++, employee.getLastName());
				ps.setString(index++, employee.getSuffix());
				ps.setString(index++, employee.getDivision());
				ps.setString(index++, employee.getPersonnelType().name());
				ps.setBoolean(index++, employee.isActive());
				ps.setInt(index++, employee.getId());
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
	public void delete(Collection<Integer> ids) throws DatabaseException {
		if (ids == null || ids.isEmpty())
			return;

		String sql = "DELETE FROM employees WHERE id = ?";

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			for (Integer id : ids) {
				ps.setInt(1, id);
				ps.executeUpdate();
			}
		} catch (SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	protected void enrich(Set<Employee> employees, Set<Enrichment> enrichments)
			throws DatabaseException {
		if (employees == null || employees.isEmpty())
			return;
		if (enrichments == null || enrichments.isEmpty())
			return;

		for (Enrichment enrichment : enrichments) {
			switch (enrichment) {
			case ROLE:
				// TODO: Add the employee's roles
				break;
			case SUPERVISED:
				// TODO: Add the employee's supervised
				break;
			default:
				throw new DatabaseException("Invalid enrichment specified: "
						+ enrichment);
			}
		}
	}
}
