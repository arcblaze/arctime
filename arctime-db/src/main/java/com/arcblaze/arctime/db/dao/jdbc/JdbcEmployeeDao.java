package com.arcblaze.arctime.db.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
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
import com.arcblaze.arctime.db.dao.EmployeeDao;
import com.arcblaze.arctime.model.Employee;
import com.arcblaze.arctime.model.Enrichment;
import com.arcblaze.arctime.model.PersonnelType;
import com.arcblaze.arctime.model.Role;

/**
 * Manages employees within the back-end database.
 */
public class JdbcEmployeeDao implements EmployeeDao {
	protected Employee fromResultSet(ResultSet rs, boolean includePass)
			throws SQLException {
		Employee employee = new Employee();
		employee.setId(rs.getInt("id"));
		employee.setCompanyId(rs.getInt("company_id"));
		employee.setLogin(rs.getString("login"));
		if (includePass)
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Employee getLogin(String login) throws DatabaseException {
		if (StringUtils.isBlank(login))
			throw new IllegalArgumentException("Invalid blank login");

		String sql = "SELECT * FROM employees WHERE active = true AND "
				+ "(login = ? OR LOWER(email) = LOWER(?))";

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, login);
			ps.setString(2, login);

			try (ResultSet rs = ps.executeQuery();) {
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
					Employee employee = fromResultSet(rs, false);

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
				while (rs.next())
					employees.add(fromResultSet(rs, false));
			}

			enrich(conn, companyId, employees, enrichments);

			return employees;
		} catch (SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * Used to perform timesheet enrichment.
	 */
	protected Map<Integer, Employee> getForTimesheets(Connection conn,
			Integer companyId, Set<Integer> timesheetIds)
			throws DatabaseException {
		if (timesheetIds == null || timesheetIds.isEmpty())
			return Collections.emptyMap();
		if (conn == null)
			throw new IllegalArgumentException("Invalid null connection");
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");

		String sql = String.format("SELECT t.id AS timesheet_id, e.* FROM "
				+ "timesheets t JOIN employees e ON (e.id = t.employee_id AND "
				+ "e.company_id = t.company_id) WHERE e.company_id = %d AND "
				+ "t.id IN (%s)", companyId,
				StringUtils.join(timesheetIds, ","));

		Map<Integer, Employee> employeeMap = new TreeMap<>();
		try (PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				Employee employee = fromResultSet(rs, false);
				int timesheetId = rs.getInt("timesheet_id");

				employeeMap.put(timesheetId, employee);
			}
		} catch (SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}

		return employeeMap;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(Integer companyId, Collection<Employee> employees)
			throws DatabaseUniqueConstraintException, DatabaseException {
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
	public void update(Integer companyId, Collection<Employee> employees)
			throws DatabaseUniqueConstraintException, DatabaseException {
		if (employees == null || employees.isEmpty())
			return;
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");

		// NOTE: the hashed_pass value is not updated.

		String sql = "UPDATE employees SET login = ?, email = ?, "
				+ "first_name = ?, last_name = ?, suffix = ?, "
				+ "division = ?, personnel_type = ?, active = ? "
				+ "WHERE id = ? AND company_id = ?";

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			for (Employee employee : employees) {
				int index = 1;
				ps.setString(index++, employee.getLogin());
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
	public void setPassword(Integer employeeId, String hashedPass)
			throws DatabaseException {
		if (employeeId == null)
			throw new IllegalArgumentException("Invalid null employee id");
		if (StringUtils.isBlank(hashedPass))
			throw new IllegalArgumentException("Invalid blank password");

		String sql = "UPDATE employees SET hashed_pass = ? WHERE id = ?";

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, hashedPass);
			ps.setInt(2, employeeId);
			ps.executeUpdate();
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
	public Set<Employee> getSupervisors(Integer companyId, Integer employeeId,
			Set<Enrichment> enrichments) throws DatabaseException {
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");
		if (employeeId == null)
			throw new IllegalArgumentException("Invalid null employee id");

		String sql = "SELECT * FROM supervisors s JOIN employees e ON "
				+ "(s.supervisor_id = e.id AND s.company_id = e.company_id) "
				+ "WHERE s.company_id = ? AND s.employee_id = ?";

		Set<Employee> supervisors = new TreeSet<>();
		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, companyId);
			ps.setInt(2, employeeId);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					Employee employee = fromResultSet(rs, false);
					employee.setPrimary(rs.getBoolean("is_primary"));
					supervisors.add(employee);
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
	public void addSupervisors(Integer companyId, Integer employeeId,
			Collection<Integer> supervisorIds, boolean primary)
			throws DatabaseException {
		if (supervisorIds == null || supervisorIds.isEmpty())
			return;
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");
		if (employeeId == null)
			throw new IllegalArgumentException("Invalid null employee id");

		String sql = "INSERT INTO supervisors (company_id, employee_id, "
				+ "supervisor_id, is_primary) VALUES (?, ?, ?, ?)";

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			for (Integer supervisorId : supervisorIds) {
				ps.setInt(1, companyId);
				ps.setInt(2, employeeId);
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
	public void deleteSupervisors(Integer companyId, Integer employeeId,
			Collection<Integer> supervisorIds) throws DatabaseException {
		if (supervisorIds == null || supervisorIds.isEmpty())
			return;
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");
		if (employeeId == null)
			throw new IllegalArgumentException("Invalid null employee id");

		String sql = "DELETE FROM supervisors WHERE company_id = ? AND "
				+ "employee_id = ? AND supervisor_id = ?";

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			for (Integer supervisorId : supervisorIds) {
				ps.setInt(1, companyId);
				ps.setInt(2, employeeId);
				ps.setInt(3, supervisorId);
				ps.executeUpdate();
			}
		} catch (SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
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
			if (enrichment == Enrichment.ROLES)
				enrichWithRoles(conn, employees);
			else if (enrichment == Enrichment.SUPERVISED)
				enrichWithSupervised(conn, companyId, employees);
			else if (enrichment == Enrichment.SUPERVISERS)
				enrichWithSupervisors(conn, companyId, employees);
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
		Map<Integer, Set<Role>> roleMap = new JdbcRoleDao().get(conn, ids);

		for (Entry<Integer, Set<Role>> entry : roleMap.entrySet()) {
			Employee employee = employeeMap.get(entry.getKey());
			if (employee != null)
				employee.setRoles(entry.getValue());
		}
	}

	protected void enrichWithSupervised(Connection conn, Integer companyId,
			Set<Employee> employees) throws DatabaseException {
		Set<Integer> ids = getEmployeeIds(employees);
		if (ids.isEmpty())
			return;

		Map<Integer, Employee> employeeMap = getEmployeeMap(employees);

		String sql = String.format("SELECT * FROM employees e "
				+ "JOIN supervisors s ON (e.id = s.employee_id AND "
				+ "e.company_id = s.company_id) WHERE e.company_id = %d AND "
				+ "s.supervisor_id IN (%s)", companyId,
				StringUtils.join(ids, ","));

		try (PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				Employee employee = fromResultSet(rs, false);
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

	protected void enrichWithSupervisors(Connection conn, Integer companyId,
			Set<Employee> employees) throws DatabaseException {
		Set<Integer> ids = getEmployeeIds(employees);
		if (ids.isEmpty())
			return;

		Map<Integer, Employee> employeeMap = getEmployeeMap(employees);

		String sql = String.format("SELECT * FROM employees e "
				+ "JOIN supervisors s ON (e.id = s.supervisor_id AND "
				+ "e.company_id = s.company_id) WHERE e.company_id = %d AND "
				+ "s.employee_id IN (%s)", companyId,
				StringUtils.join(ids, ","));

		try (PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				Employee employee = fromResultSet(rs, false);
				employee.setPrimary(rs.getBoolean("is_primary"));

				int employeeId = rs.getInt("employee_id");

				Employee supervised = employeeMap.get(employeeId);
				if (supervised != null)
					supervised.addSupervisors(employee);
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
