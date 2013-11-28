package com.arcblaze.arctime.db.mysql;

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

import com.arcblaze.arctime.db.ConnectionManager;
import com.arcblaze.arctime.db.DatabaseException;
import com.arcblaze.arctime.db.dao.CompanyDao;
import com.arcblaze.arctime.model.Company;
import com.arcblaze.arctime.model.Enrichment;

/**
 * Manages companies within the back-end database.
 */
public class MySqlCompanyDao implements CompanyDao {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Company get(Integer id, Set<Enrichment> enrichments)
			throws DatabaseException {
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

					enrich(conn, Collections.singleton(company), enrichments);

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
	public Set<Company> getAll(Set<Enrichment> enrichments)
			throws DatabaseException {
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

			enrich(conn, companies, enrichments);

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

		String sql = "INSERT IGNORE INTO companies (name, active) VALUES "
				+ "(?, ?)";

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

		String sql = "DELETE FROM companies WHERE id = ?";

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

	protected void enrich(Connection conn, Set<Company> companies,
			Set<Enrichment> enrichments) throws DatabaseException {
		if (companies == null || companies.isEmpty())
			return;
		if (enrichments == null || enrichments.isEmpty())
			return;

		if (conn == null)
			throw new IllegalArgumentException("Invalid null connection");

		for (Enrichment enrichment : enrichments) {
			throw new DatabaseException("Invalid enrichment specified: "
					+ enrichment);
		}
	}

	protected SortedSet<Integer> getCompanyIds(Set<Company> companies) {
		SortedSet<Integer> ids = new TreeSet<>();
		for (Company company : companies)
			if (company.getId() != null)
				ids.add(company.getId());
		return ids;
	}

	protected Map<Integer, Company> getCompanyMap(Set<Company> companies) {
		Map<Integer, Company> map = new HashMap<>();
		for (Company company : companies)
			if (company.getId() != null)
				map.put(company.getId(), company);
		return map;
	}
}
