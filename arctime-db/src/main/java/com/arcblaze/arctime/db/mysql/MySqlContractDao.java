package com.arcblaze.arctime.db.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import com.arcblaze.arctime.db.ConnectionManager;
import com.arcblaze.arctime.db.DatabaseException;
import com.arcblaze.arctime.db.dao.ContractDao;
import com.arcblaze.arctime.model.Contract;
import com.arcblaze.arctime.model.Enrichment;

/**
 * Manages contracts within the back-end database.
 */
public class MySqlContractDao implements ContractDao {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Contract get(Integer companyId, Integer contractId,
			Set<Enrichment> enrichments) throws DatabaseException {
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");
		if (contractId == null)
			throw new IllegalArgumentException("Invalid null contract id");

		String sql = "SELECT * FROM contracts WHERE company_id = ? AND id = ?";

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, companyId);
			ps.setInt(2, contractId);

			try (ResultSet rs = ps.executeQuery();) {
				if (rs.next()) {
					Contract contract = new Contract();
					contract.setId(rs.getInt("id"));
					contract.setCompanyId(rs.getInt("company_id"));
					contract.setDescription(rs.getString("description"));
					contract.setContractNum(rs.getString("contract_num"));
					contract.setJobCode(rs.getString("job_code"));
					contract.setAdministrative(rs.getBoolean("admin"));
					contract.setActive(rs.getBoolean("active"));
					return contract;
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
	public Set<Contract> getAll(Integer companyId, Set<Enrichment> enrichments)
			throws DatabaseException {
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");

		String sql = "SELECT * FROM contracts WHERE company_id = ?";

		Set<Contract> contracts = new TreeSet<>();
		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, companyId);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					Contract contract = new Contract();
					contract.setId(rs.getInt("id"));
					contract.setCompanyId(rs.getInt("company_id"));
					contract.setDescription(rs.getString("description"));
					contract.setContractNum(rs.getString("contract_num"));
					contract.setJobCode(rs.getString("job_code"));
					contract.setAdministrative(rs.getBoolean("admin"));
					contract.setActive(rs.getBoolean("active"));
					contracts.add(contract);
				}
			}

			return contracts;
		} catch (SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(Integer companyId, Collection<Contract> contracts)
			throws DatabaseException {
		if (contracts == null || contracts.isEmpty())
			return;
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");

		String sql = "INSERT IGNORE INTO contracts (company_id, description, "
				+ "contract_num, job_code, admin, active) VALUES "
				+ "(?, ?, ?, ?, ?, ?)";

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql,
						Statement.RETURN_GENERATED_KEYS)) {
			for (Contract contract : contracts) {
				int index = 1;
				contract.setCompanyId(companyId);
				ps.setInt(index++, contract.getCompanyId());
				ps.setString(index++, contract.getDescription());
				ps.setString(index++, contract.getContractNum());
				ps.setString(index++, contract.getJobCode());
				ps.setBoolean(index++, contract.isAdministrative());
				ps.setBoolean(index++, contract.isActive());
				ps.executeUpdate();

				try (ResultSet rs = ps.getGeneratedKeys()) {
					if (rs.next())
						contract.setId(rs.getInt(1));
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
	public void update(Integer companyId, Collection<Contract> contracts)
			throws DatabaseException {
		if (contracts == null || contracts.isEmpty())
			return;
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");

		String sql = "UPDATE contracts SET description = ?, contract_num = ?, "
				+ "job_code = ?, admin = ?, active = ? WHERE id = ? "
				+ "AND company_id = ?";

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			for (Contract contract : contracts) {
				int index = 1;
				ps.setString(index++, contract.getDescription());
				ps.setString(index++, contract.getContractNum());
				ps.setString(index++, contract.getJobCode());
				ps.setBoolean(index++, contract.isAdministrative());
				ps.setBoolean(index++, contract.isActive());
				ps.setInt(index++, contract.getId());
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

		String sql = "DELETE FROM contracts WHERE id = ? AND company_id = ?";

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			for (Integer id : ids) {
				ps.setInt(1, id);
				ps.setInt(2, companyId);
				ps.executeUpdate();
			}
		} catch (SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}
}
