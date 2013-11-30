package com.arcblaze.arctime.db.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;

import com.arcblaze.arctime.db.ConnectionManager;
import com.arcblaze.arctime.db.DatabaseException;
import com.arcblaze.arctime.db.dao.EmployeeDao;
import com.arcblaze.arctime.model.Employee;
import com.arcblaze.arctime.model.Enrichment;
import com.arcblaze.arctime.model.PersonnelType;
import com.arcblaze.arctime.model.Role;

/**
 * Manages employees within the back-end database.
 */
public class JdbcEmployeeDao implements EmployeeDao {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Employee getLogin(Integer companyId, String login)
			throws DatabaseException {
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");
		if (StringUtils.isBlank(login))
			throw new IllegalArgumentException("Invalid blank login");

		String sql = "SELECT * FROM employees WHERE company_id = ? AND "
				+ "(login = ? OR LOWER(email) = LOWER(?)) AND active = true";

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, companyId);
			ps.setString(2, login);
			ps.setString(3, login);

			try (ResultSet rs = ps.executeQuery();) {
				if (rs.next()) {
					Employee employee = new Employee();
					employee.setId(rs.getInt("id"));
					employee.setCompanyId(rs.getInt("company_id"));
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
	public Employee get(Integer companyId, Integer employeeId,
			Set<Enrichment> enrichments) throws DatabaseException {
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");
		if (employeeId == null)
			throw new IllegalArgumentException("Invalid null employee id");

		String sql = "SELECT * FROM employees WHERE company_id = ? AND id = ?";

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, companyId);
			ps.setInt(2, employeeId);

			try (ResultSet rs = ps.executeQuery();) {
				if (rs.next()) {
					Employee employee = new Employee();
					employee.setId(rs.getInt("id"));
					employee.setCompanyId(rs.getInt("company_id"));
					employee.setLogin(rs.getString("login"));
					// hashed_pass is specifically left out
					employee.setEmail(rs.getString("email"));
					employee.setFirstName(rs.getString("first_name"));
					employee.setLastName(rs.getString("last_name"));
					employee.setSuffix(rs.getString("suffix"));
					employee.setDivision(rs.getString("division"));
					employee.setPersonnelType(PersonnelType.parse(rs
							.getString("personnel_type")));
					employee.setActive(rs.getBoolean("active"));

					enrich(conn, companyId, Collections.singleton(employee),
							enrichments);

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
	public Set<Employee> getAll(Integer companyId, Set<Enrichment> enrichments)
			throws DatabaseException {
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");

		String sql = "SELECT * FROM employees WHERE company_id = ?";

		Set<Employee> employees = new TreeSet<>();
		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, companyId);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					Employee employee = new Employee();
					employee.setId(rs.getInt("id"));
					employee.setCompanyId(rs.getInt("company_id"));
					employee.setLogin(rs.getString("login"));
					// hashed_pass is specifically left out
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
			}

			enrich(conn, companyId, employees, enrichments);

			return employees;
		} catch (SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(Integer companyId, Collection<Employee> employees)
			throws DatabaseException {
		if (employees == null || employees.isEmpty())
			return;
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");

		String sql = "INSERT INTO employees (company_id, login, hashed_pass, "
				+ "email, first_name, last_name, suffix, division, "
				+ "personnel_type, active) VALUES "
				+ "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql,
						Statement.RETURN_GENERATED_KEYS)) {
			for (Employee employee : employees) {
				int index = 1;
				employee.setCompanyId(companyId);
				ps.setInt(index++, employee.getCompanyId());
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

				try (ResultSet rs = ps.getGeneratedKeys()) {
					if (rs.next())
						employee.setId(rs.getInt(1));
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
	public void update(Integer companyId, Collection<Employee> employees)
			throws DatabaseException {
		if (employees == null || employees.isEmpty())
			return;
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");

		String sql = "UPDATE employees SET login = ?, hashed_pass = ?, "
				+ "email = ?, first_name = ?, last_name = ?, suffix = ?, "
				+ "division = ?, personnel_type = ?, active = ? "
				+ "WHERE id = ? AND company_id = ?";

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
				"DELETE FROM employees WHERE company_id = %d AND id IN (%s)",
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
	public Set<Role> getRoles(Integer employeeId) throws DatabaseException {
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

	protected void enrich(Connection conn, Integer companyId,
			Set<Employee> employees, Set<Enrichment> enrichments)
			throws DatabaseException {
		if (employees == null || employees.isEmpty())
			return;
		if (enrichments == null || enrichments.isEmpty())
			return;

		if (conn == null)
			throw new IllegalArgumentException("Invalid null connection");

		for (Enrichment enrichment : enrichments) {
			if (enrichment == Enrichment.ROLE)
				enrichWithRoles(conn, employees);
			else if (enrichment == Enrichment.SUPERVISED)
				enrichWithSupervised(conn, companyId, employees);
			else if (enrichment == Enrichment.SUPERVISERS)
				enrichWithSupervisers(conn, companyId, employees);
			else
				throw new DatabaseException("Invalid enrichment specified: "
						+ enrichment);
		}
	}

	protected void enrichWithRoles(Connection conn, Set<Employee> employees)
			throws DatabaseException {
		Set<Integer> ids = getEmployeeIds(employees);
		if (ids.isEmpty())
			return;

		Map<Integer, Employee> employeeMap = getEmployeeMap(employees);

		String sql = String.format(
				"SELECT * FROM roles WHERE employee_id IN (%s)",
				StringUtils.join(ids, ","));

		try (PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				int employeeId = rs.getInt("employee_id");
				Role role = Role.parse(rs.getString("name"));

				Employee employee = employeeMap.get(employeeId);
				if (employee != null)
					employee.addRoles(role);
			}
		} catch (SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	protected void enrichWithSupervised(Connection conn, Integer companyId,
			Set<Employee> employees) throws DatabaseException {
		Set<Integer> ids = getEmployeeIds(employees);
		if (ids.isEmpty())
			return;

		Map<Integer, Employee> employeeMap = getEmployeeMap(employees);

		String sql = String.format("SELECT * FROM employees e "
				+ "JOIN supervisors s on (e.id = s.employee_id) "
				+ "WHERE e.company_id = %d AND s.supervisor_id IN (%s)",
				companyId, StringUtils.join(ids, ","));

		try (PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				Employee employee = new Employee();
				employee.setId(rs.getInt("id"));
				employee.setCompanyId(rs.getInt("company_id"));
				employee.setLogin(rs.getString("login"));
				// hashed_pass is specifically left out
				employee.setEmail(rs.getString("email"));
				employee.setFirstName(rs.getString("first_name"));
				employee.setLastName(rs.getString("last_name"));
				employee.setSuffix(rs.getString("suffix"));
				employee.setDivision(rs.getString("division"));
				employee.setPersonnelType(PersonnelType.parse(rs
						.getString("personnel_type")));
				employee.setActive(rs.getBoolean("active"));
				employee.setPrimary(rs.getBoolean("is_primary"));

				int supervisorId = rs.getInt("supervisor_id");

				Employee supervisor = employeeMap.get(supervisorId);
				if (supervisor != null)
					supervisor.addSupervised(employee);
			}
		} catch (SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	protected void enrichWithSupervisers(Connection conn, Integer companyId,
			Set<Employee> employees) throws DatabaseException {
		Set<Integer> ids = getEmployeeIds(employees);
		if (ids.isEmpty())
			return;

		Map<Integer, Employee> employeeMap = getEmployeeMap(employees);

		String sql = String.format("SELECT * FROM employees e "
				+ "JOIN supervisors s on (e.id = s.supervisor_id) "
				+ "WHERE e.company_id = %d AND s.employee_id IN (%s)",
				companyId, StringUtils.join(ids, ","));

		try (PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				Employee employee = new Employee();
				employee.setId(rs.getInt("id"));
				employee.setCompanyId(rs.getInt("company_id"));
				employee.setLogin(rs.getString("login"));
				// hashed_pass is specifically left out
				employee.setEmail(rs.getString("email"));
				employee.setFirstName(rs.getString("first_name"));
				employee.setLastName(rs.getString("last_name"));
				employee.setSuffix(rs.getString("suffix"));
				employee.setDivision(rs.getString("division"));
				employee.setPersonnelType(PersonnelType.parse(rs
						.getString("personnel_type")));
				employee.setActive(rs.getBoolean("active"));
				employee.setPrimary(rs.getBoolean("is_primary"));

				int employeeId = rs.getInt("employee_id");

				Employee supervised = employeeMap.get(employeeId);
				if (supervised != null)
					supervised.addSupervisers(employee);
			}
		} catch (SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	protected SortedSet<Integer> getEmployeeIds(Set<Employee> employees) {
		SortedSet<Integer> ids = new TreeSet<>();
		for (Employee employee : employees)
			if (employee.getId() != null)
				ids.add(employee.getId());
		return ids;
	}

	protected Map<Integer, Employee> getEmployeeMap(Set<Employee> employees) {
		Map<Integer, Employee> map = new HashMap<>();
		for (Employee employee : employees)
			if (employee.getId() != null)
				map.put(employee.getId(), employee);
		return map;
	}
}
