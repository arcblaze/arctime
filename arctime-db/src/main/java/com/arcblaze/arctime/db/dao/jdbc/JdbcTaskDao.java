package com.arcblaze.arctime.db.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;

import com.arcblaze.arctime.db.ConnectionManager;
import com.arcblaze.arctime.db.DatabaseException;
import com.arcblaze.arctime.db.dao.TaskDao;
import com.arcblaze.arctime.model.Assignment;
import com.arcblaze.arctime.model.Task;
import com.arcblaze.arctime.model.PayPeriod;

/**
 * Manages tasks within the back-end database.
 */
public class JdbcTaskDao implements TaskDao {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Task get(Integer companyId, Integer taskId) throws DatabaseException {
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");
		if (taskId == null)
			throw new IllegalArgumentException("Invalid null task id");

		String sql = "SELECT * FROM tasks WHERE company_id = ? AND id = ?";

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, companyId);
			ps.setInt(2, taskId);

			try (ResultSet rs = ps.executeQuery();) {
				if (rs.next()) {
					Task task = new Task();
					task.setId(rs.getInt("id"));
					task.setCompanyId(rs.getInt("company_id"));
					task.setDescription(rs.getString("description"));
					task.setJobCode(rs.getString("job_code"));
					task.setAdministrative(rs.getBoolean("admin"));
					task.setActive(rs.getBoolean("active"));
					return task;
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
	public Set<Task> getAll(Integer companyId) throws DatabaseException {
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");

		String sql = "SELECT * FROM tasks WHERE company_id = ?";

		Set<Task> tasks = new TreeSet<>();
		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, companyId);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					Task task = new Task();
					task.setId(rs.getInt("id"));
					task.setCompanyId(rs.getInt("company_id"));
					task.setDescription(rs.getString("description"));
					task.setJobCode(rs.getString("job_code"));
					task.setAdministrative(rs.getBoolean("admin"));
					task.setActive(rs.getBoolean("active"));
					tasks.add(task);
				}
			}

			return tasks;
		} catch (SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<Task> getForEmployee(Integer companyId, Integer employeeId,
			Date day, boolean includeAdmin) throws DatabaseException {
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");
		if (employeeId == null)
			throw new IllegalArgumentException("Invalid null employee id");

		String sql = "SELECT c.id AS task_id, description, "
				+ "job_code, admin, active, a.id AS assignment_id, "
				+ "employee_id, labor_cat, item_name, begin, end "
				+ "FROM tasks c JOIN assignments a ON "
				+ "(a.task_id = c.id) WHERE c.company_id = ? AND "
				+ "(a.employee_id = ? OR c.admin = true)";

		if (day != null) {
			sql += " AND (begin IS NULL OR begin <= ?)";
			sql += " AND (end IS NULL OR end >= ?)";
		}

		if (!includeAdmin)
			sql += " AND c.admin = false";

		Map<Integer, Task> tasks = new HashMap<>();
		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, companyId);
			ps.setInt(2, employeeId);
			if (day != null) {
				ps.setTimestamp(3, new Timestamp(day.getTime()));
				ps.setTimestamp(4, new Timestamp(day.getTime()));
			}

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					int taskId = rs.getInt("task_id");
					Task task = tasks.get(taskId);
					if (task == null) {
						task = new Task();
						task.setId(taskId);
						task.setCompanyId(rs.getInt("company_id"));
						task.setDescription(rs.getString("description"));
						task.setJobCode(rs.getString("job_code"));
						task.setAdministrative(rs.getBoolean("admin"));
						task.setActive(rs.getBoolean("active"));
						tasks.put(taskId, task);
					}

					Assignment assignment = new Assignment();
					assignment.setId(rs.getInt("assignment_id"));
					assignment.setCompanyId(rs.getInt("company_id"));
					assignment.setTaskId(taskId);
					assignment.setEmployeeId(rs.getInt("employee_id"));
					assignment.setLaborCat(rs.getString("labor_cat"));
					assignment.setItemName(rs.getString("item_name"));

					Date begin = rs.getDate("begin");
					Date end = rs.getDate("end");
					if (begin != null)
						assignment.setBegin(begin);
					if (end != null)
						assignment.setEnd(end);
					task.addAssignments(assignment);
				}
			}

			return new TreeSet<>(tasks.values());
		} catch (SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<Task> getForPayPeriod(Integer companyId, Integer employeeId,
			PayPeriod payPeriod) throws DatabaseException {
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");
		if (employeeId == null)
			throw new IllegalArgumentException("Invalid null employee id");
		if (payPeriod == null)
			throw new IllegalArgumentException("Invalid null pay period");

		String sql = "SELECT c.id AS task_id, description, "
				+ "job_code, admin, active, a.id AS assignment_id, "
				+ "employee_id, labor_cat, item_name, begin, end "
				+ "FROM tasks c JOIN assignments a ON "
				+ "(a.task_id = c.id) WHERE c.company_id = ? AND "
				+ "(a.employee_id = ? OR c.admin = true) AND "
				+ "(begin IS NULL OR begin <= ?) AND "
				+ "(end IS NULL OR end >= ?)";

		Map<Integer, Task> tasks = new HashMap<>();
		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, companyId);
			ps.setInt(2, employeeId);
			ps.setTimestamp(3, new Timestamp(payPeriod.getEnd().getTime()));
			ps.setTimestamp(4, new Timestamp(payPeriod.getBegin().getTime()));

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					int taskId = rs.getInt("task_id");
					Task task = tasks.get(taskId);
					if (task == null) {
						task = new Task();
						task.setId(taskId);
						task.setCompanyId(rs.getInt("company_id"));
						task.setDescription(rs.getString("description"));
						task.setJobCode(rs.getString("job_code"));
						task.setAdministrative(rs.getBoolean("admin"));
						task.setActive(rs.getBoolean("active"));
						tasks.put(taskId, task);
					}

					Assignment assignment = new Assignment();
					assignment.setId(rs.getInt("assignment_id"));
					assignment.setCompanyId(rs.getInt("company_id"));
					assignment.setTaskId(taskId);
					assignment.setEmployeeId(rs.getInt("employee_id"));
					assignment.setLaborCat(rs.getString("labor_cat"));
					assignment.setItemName(rs.getString("item_name"));

					Date begin = rs.getDate("begin");
					Date end = rs.getDate("end");
					if (begin != null)
						assignment.setBegin(begin);
					if (end != null)
						assignment.setEnd(end);
					task.addAssignments(assignment);
				}
			}

			return new TreeSet<>(tasks.values());
		} catch (SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(Integer companyId, Collection<Task> tasks)
			throws DatabaseException {
		if (tasks == null || tasks.isEmpty())
			return;
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");

		String sql = "INSERT INTO tasks (company_id, description, "
				+ "job_code, admin, active) VALUES " + "(?, ?, ?, ?, ?)";

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql,
						Statement.RETURN_GENERATED_KEYS)) {
			for (Task task : tasks) {
				int index = 1;
				task.setCompanyId(companyId);
				ps.setInt(index++, task.getCompanyId());
				ps.setString(index++, task.getDescription());
				ps.setString(index++, task.getJobCode());
				ps.setBoolean(index++, task.isAdministrative());
				ps.setBoolean(index++, task.isActive());
				ps.executeUpdate();

				try (ResultSet rs = ps.getGeneratedKeys()) {
					if (rs.next())
						task.setId(rs.getInt(1));
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
	public void update(Integer companyId, Collection<Task> tasks)
			throws DatabaseException {
		if (tasks == null || tasks.isEmpty())
			return;
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");

		String sql = "UPDATE tasks SET description = ?, "
				+ "job_code = ?, admin = ?, active = ? WHERE id = ? "
				+ "AND company_id = ?";

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			for (Task task : tasks) {
				int index = 1;
				ps.setString(index++, task.getDescription());
				ps.setString(index++, task.getJobCode());
				ps.setBoolean(index++, task.isAdministrative());
				ps.setBoolean(index++, task.isActive());
				ps.setInt(index++, task.getId());
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
				"DELETE FROM tasks WHERE company_id = %d AND id IN (%s)",
				companyId, StringUtils.join(ids, ","));

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.executeUpdate();
		} catch (SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}
}
