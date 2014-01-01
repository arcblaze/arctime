package com.arcblaze.arctime.db.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;

import com.arcblaze.arctime.db.ConnectionManager;
import com.arcblaze.arctime.db.DatabaseException;
import com.arcblaze.arctime.db.dao.TransactionDao;
import com.arcblaze.arctime.model.Transaction;
import com.arcblaze.arctime.model.TransactionType;

/**
 * Manages transactions within the back-end database.
 */
public class JdbcTransactionDao implements TransactionDao {
	protected Transaction fromResultSet(ResultSet rs) throws SQLException {
		Transaction transaction = new Transaction();
		transaction.setId(rs.getInt("id"));
		transaction.setCompanyId(rs.getInt("company_id"));
		transaction.setUserId(rs.getInt("user_id"));
		transaction.setTimestamp(rs.getTimestamp("timestamp"));
		transaction.setTransactionType(TransactionType.parse(rs
				.getString("type")));
		transaction.setDescription(rs.getString("description"));
		transaction.setAmount(rs.getString("amount"));

		String notes = rs.getString("notes");
		if (StringUtils.isNotBlank(notes))
			transaction.setNotes(notes);
		return transaction;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Transaction get(Integer id) throws DatabaseException {
		if (id == null)
			throw new IllegalArgumentException("Invalid null id");

		String sql = "SELECT * FROM transactions WHERE id = ?";

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, id);

			try (ResultSet rs = ps.executeQuery();) {
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
	public Set<Transaction> getForCompany(Integer companyId)
			throws DatabaseException {
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");

		String sql = "SELECT * FROM transactions WHERE company_id = ?";

		Set<Transaction> transactions = new TreeSet<>();
		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, companyId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next())
					transactions.add(fromResultSet(rs));
			}

			return transactions;
		} catch (SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<Transaction> getForCompany(Integer companyId, Date begin,
			Date end) throws DatabaseException {
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");
		if (begin == null)
			throw new IllegalArgumentException("Invalid null begin");
		if (end == null)
			throw new IllegalArgumentException("Invalid null end");

		String sql = "SELECT * FROM transactions WHERE company_id = ? AND "
				+ "timestamp >= ? AND timestamp < ?";

		Set<Transaction> transactions = new TreeSet<>();
		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, companyId);
			ps.setTimestamp(2, new Timestamp(begin.getTime()));
			ps.setTimestamp(3, new Timestamp(end.getTime()));
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next())
					transactions.add(fromResultSet(rs));
			}

			return transactions;
		} catch (SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(Transaction... transactions) throws DatabaseException {
		this.add(transactions == null ? null : Arrays.asList(transactions));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(Collection<Transaction> transactions)
			throws DatabaseException {
		if (transactions == null || transactions.isEmpty())
			return;

		String sql = "INSERT INTO transactions (company_id, user_id, "
				+ "timestamp, type, description, amount, notes) VALUES "
				+ "(?, ?, ?, ?, ?, ?, ?)";

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql,
						Statement.RETURN_GENERATED_KEYS)) {
			for (Transaction transaction : transactions) {
				int index = 1;
				ps.setInt(index++, transaction.getCompanyId());
				ps.setInt(index++, transaction.getUserId());
				ps.setTimestamp(index++, new Timestamp(transaction
						.getTimestamp().getTime()));
				ps.setString(index++, transaction.getTransactionType().name());
				ps.setString(index++, transaction.getDescription());
				ps.setString(index++, transaction.getAmount().toPlainString());
				ps.setString(index++, transaction.getNotes());
				ps.executeUpdate();

				try (ResultSet rs = ps.getGeneratedKeys()) {
					if (rs.next())
						transaction.setId(rs.getInt(1));
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
	public void update(Transaction... transactions) throws DatabaseException {
		this.update(transactions == null ? null : Arrays.asList(transactions));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update(Collection<Transaction> transactions)
			throws DatabaseException {
		if (transactions == null || transactions.isEmpty())
			return;

		String sql = "UPDATE transactions SET company_id = ?, user_id = ?, "
				+ "timestamp = ?, type = ?, description = ?, amount = ?, "
				+ "notes = ? WHERE id = ?";

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			for (Transaction transaction : transactions) {
				int index = 1;
				ps.setInt(index++, transaction.getCompanyId());
				ps.setInt(index++, transaction.getUserId());
				ps.setTimestamp(index++, new Timestamp(transaction
						.getTimestamp().getTime()));
				ps.setString(index++, transaction.getTransactionType().name());
				ps.setString(index++, transaction.getDescription());
				ps.setString(index++, transaction.getAmount().toPlainString());
				ps.setString(index++, transaction.getNotes());
				ps.setInt(index++, transaction.getId());
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

		String sql = String.format("DELETE FROM transactions WHERE id IN (%s)",
				StringUtils.join(ids, ","));

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.executeUpdate();
		} catch (SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}
}
