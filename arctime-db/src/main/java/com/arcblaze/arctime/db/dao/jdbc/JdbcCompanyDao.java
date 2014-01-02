package com.arcblaze.arctime.db.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.arcblaze.arctime.db.ConnectionManager;
import com.arcblaze.arctime.db.DatabaseException;
import com.arcblaze.arctime.db.dao.CompanyDao;
import com.arcblaze.arctime.model.Company;

/**
 * Manages companies within the back-end database.
 */
public class JdbcCompanyDao implements CompanyDao {
	protected Company fromResultSet(ResultSet rs) throws SQLException {
		Company company = new Company();
		company.setId(rs.getInt("id"));
		company.setName(rs.getString("name"));
		company.setActive(rs.getBoolean("active"));
		return company;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int count(boolean includeInactive) throws DatabaseException {
		String sql = "SELECT COUNT(*) FROM companies"
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
	public SortedMap<Date, Integer> getActiveByMonth(Date begin, Date end)
			throws DatabaseException {
		if (begin == null)
			throw new IllegalArgumentException("Invalid null begin");
		if (end == null)
			throw new IllegalArgumentException("Invalid null end");

		String sql = "SELECT YEAR(day) AS year, MONTH(day) AS month, "
				+ "MAX(active) AS active FROM active_company_counts "
				+ "WHERE day >= ? AND day < ? GROUP BY year, month";

		SortedMap<Date, Integer> map = new TreeMap<>();
		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setDate(1, new java.sql.Date(begin.getTime()));
			ps.setDate(2, new java.sql.Date(end.getTime()));
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					int year = rs.getInt("year");
					int month = rs.getInt("month");
					Integer active = rs.getInt("active");

					Date date = DateUtils.parseDate(
							String.format("%d-%d-01", year, month),
							new String[] { "yyyy-MM-dd" });
					Integer num = map.get(date);
					map.put(date, num == null ? active : Math.max(num, active));
				}
			} catch (ParseException badDate) {
				throw new DatabaseException("Unexpected date parse problem.",
						badDate);
			}

			// Add any missing months.
			Date date = DateUtils.truncate(begin, Calendar.MONTH);
			while (!date.after(end)) {
				if (!map.containsKey(date))
					map.put(date, 0);
				date = DateUtils.addMonths(date, 1);
			}

			return map;
		} catch (SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setActiveCompanies(Date day, Integer count)
			throws DatabaseException {
		if (day == null)
			throw new IllegalArgumentException("Invalid null day");
		if (count == null || count < 0)
			throw new IllegalArgumentException("Invalid count: " + count);

		String sql = "INSERT INTO active_company_counts "
				+ "(day, active) VALUES (?, ?)";

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			int index = 1;
			ps.setDate(index++, new java.sql.Date(day.getTime()));
			ps.setInt(index++, count);
			ps.executeUpdate();
		} catch (SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Company get(Integer id) throws DatabaseException {
		if (id == null)
			throw new IllegalArgumentException("Invalid null id");

		String sql = "SELECT * FROM companies WHERE id = ?";

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, id);

			try (ResultSet rs = ps.executeQuery()) {
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
	public Set<Company> getAll() throws DatabaseException {
		String sql = "SELECT * FROM companies";

		Set<Company> companies = new TreeSet<>();
		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next())
				companies.add(fromResultSet(rs));

			return companies;
		} catch (SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(Company... companies) throws DatabaseException {
		this.add(companies == null ? null : Arrays.asList(companies));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(Collection<Company> companies) throws DatabaseException {
		if (companies == null || companies.isEmpty())
			return;

		String sql = "INSERT INTO companies (name, active) VALUES (?, ?)";

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql,
						Statement.RETURN_GENERATED_KEYS)) {
			for (Company company : companies) {
				int index = 1;
				ps.setString(index++, company.getName());
				ps.setBoolean(index++, company.isActive());
				ps.executeUpdate();

				try (ResultSet rs = ps.getGeneratedKeys()) {
					if (rs.next())
						company.setId(rs.getInt(1));
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
	public void update(Company... companies) throws DatabaseException {
		this.update(companies == null ? null : Arrays.asList(companies));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update(Collection<Company> companies) throws DatabaseException {
		if (companies == null || companies.isEmpty())
			return;

		String sql = "UPDATE companies SET name = ?, active = ? "
				+ "WHERE id = ?";

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			for (Company company : companies) {
				int index = 1;
				ps.setString(index++, company.getName());
				ps.setBoolean(index++, company.isActive());
				ps.setInt(index++, company.getId());
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
	public void delete(Integer... ids) throws DatabaseException {
		this.delete(ids == null ? null : Arrays.asList(ids));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete(Collection<Integer> ids) throws DatabaseException {
		if (ids == null || ids.isEmpty())
			return;

		String sql = String.format("DELETE FROM companies WHERE id IN (%s)",
				StringUtils.join(ids, ","));

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.executeUpdate();
		} catch (SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}
}
