package com.arcblaze.arctime.db.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;

import com.arcblaze.arctime.db.ConnectionManager;
import com.arcblaze.arctime.db.DatabaseException;
import com.arcblaze.arctime.db.dao.CompanyDao;
import com.arcblaze.arctime.model.Company;

/**
 * Manages companies within the back-end database.
 */
public class JdbcCompanyDao implements CompanyDao {
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

			try (ResultSet rs = ps.executeQuery();) {
				if (rs.next()) {
					Company company = new Company();
					company.setId(rs.getInt("id"));
					company.setName(rs.getString("name"));
					company.setActive(rs.getBoolean("active"));

					return company;
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
	public Set<Company> getAll() throws DatabaseException {
		String sql = "SELECT * FROM companies";

		Set<Company> companies = new TreeSet<>();
		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				Company company = new Company();
				company.setId(rs.getInt("id"));
				company.setName(rs.getString("name"));
				company.setActive(rs.getBoolean("active"));
				companies.add(company);
			}

			return companies;
		} catch (SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
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
