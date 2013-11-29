package com.arcblaze.arctime.db.mysql;

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
import com.arcblaze.arctime.db.dao.HolidayDao;
import com.arcblaze.arctime.model.Holiday;
import com.arcblaze.arctime.model.PayPeriod;
import com.arcblaze.arctime.model.util.HolidayConfigurationException;

/**
 * Manages holidays within the back-end database.
 */
public class MySqlHolidayDao implements HolidayDao {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Holiday get(Integer companyId, Integer id) throws DatabaseException,
			HolidayConfigurationException {
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");
		if (id == null)
			throw new IllegalArgumentException("Invalid null id");

		String sql = "SELECT * FROM holidays WHERE company_id = ? AND id = ?";

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, companyId);
			ps.setInt(2, id);

			try (ResultSet rs = ps.executeQuery();) {
				if (rs.next()) {
					Holiday holiday = new Holiday();
					holiday.setId(rs.getInt("id"));
					holiday.setCompanyId(rs.getInt("company_id"));
					holiday.setDescription(rs.getString("description"));
					holiday.setConfig(rs.getString("config"));
					return holiday;
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
	public Set<Holiday> getAll(Integer companyId) throws DatabaseException,
			HolidayConfigurationException {
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");

		String sql = "SELECT * FROM holidays WHERE company_id = ?";

		Set<Holiday> holidays = new TreeSet<>();
		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, companyId);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					Holiday holiday = new Holiday();
					holiday.setId(rs.getInt("id"));
					holiday.setCompanyId(rs.getInt("company_id"));
					holiday.setDescription(rs.getString("description"));
					holiday.setConfig(rs.getString("config"));
					holidays.add(holiday);
				}
			}

			return holidays;
		} catch (SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<Holiday> getForPayPeriod(Integer companyId, PayPeriod payPeriod)
			throws DatabaseException, HolidayConfigurationException {
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");
		if (payPeriod == null)
			throw new IllegalArgumentException("Invalid null pay period");

		Set<Holiday> all = getAll(companyId);
		Set<Holiday> inPayPeriod = new TreeSet<>();

		for (Holiday holiday : all) {
			if (payPeriod.contains(holiday))
				inPayPeriod.add(holiday);
		}

		return inPayPeriod;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(Integer companyId, Collection<Holiday> holidays)
			throws DatabaseException {
		if (holidays == null || holidays.isEmpty())
			return;
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");

		String sql = "INSERT IGNORE INTO holidays (company_id, description, "
				+ "config) VALUES (?, ?, ?)";

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql,
						Statement.RETURN_GENERATED_KEYS)) {
			for (Holiday holiday : holidays) {
				int index = 1;
				holiday.setCompanyId(companyId);
				ps.setInt(index++, holiday.getCompanyId());
				ps.setString(index++, holiday.getDescription());
				ps.setString(index++, holiday.getConfig());
				ps.executeUpdate();

				try (ResultSet rs = ps.getGeneratedKeys()) {
					if (rs.next())
						holiday.setId(rs.getInt(1));
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
	public void update(Integer companyId, Collection<Holiday> holidays)
			throws DatabaseException {
		if (holidays == null || holidays.isEmpty())
			return;
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");

		String sql = "UPDATE holidays SET name = ?, active = ? "
				+ "WHERE id = ? AND company_id = ?";

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			for (Holiday holiday : holidays) {
				int index = 1;
				holiday.setCompanyId(companyId);
				ps.setString(index++, holiday.getDescription());
				ps.setString(index++, holiday.getConfig());
				ps.setInt(index++, holiday.getId());
				ps.setInt(index++, holiday.getCompanyId());
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
				"DELETE FROM holidays WHERE company_id = %d AND id IN (%s)",
				companyId, StringUtils.join(ids, ","));

		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.executeUpdate();
		} catch (SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}
}
