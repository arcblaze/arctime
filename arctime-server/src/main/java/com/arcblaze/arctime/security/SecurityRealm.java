package com.arcblaze.arctime.security;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.catalina.realm.GenericPrincipal;
import org.apache.catalina.realm.RealmBase;

import com.arcblaze.arctime.db.DaoFactory;
import com.arcblaze.arctime.db.DatabaseException;
import com.arcblaze.arctime.db.dao.EmployeeDao;
import com.arcblaze.arctime.db.dao.RoleDao;
import com.arcblaze.arctime.model.Employee;
import com.arcblaze.arctime.model.Role;

/**
 * Provides an implementation of the security realm used to authenticate users
 * in the system.
 */
public class SecurityRealm extends RealmBase {
	/** The configured name of this realm. */
	private final String realmName;

	/**
	 * Cache looked-up users for better performance.
	 */
	private final Map<String, Employee> userMap = new ConcurrentHashMap<>();

	/**
	 * @param realmName
	 *            the name of the realm
	 */
	public SecurityRealm(String realmName) {
		this.realmName = realmName;
		setDigest("SHA-512");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getName() {
		return this.realmName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getPassword(String username) {
		EmployeeDao dao = DaoFactory.getEmployeeDao();
		try {
			Employee employee = dao.getLogin(1, username);
			if (employee == null)
				return null;

			this.userMap.put(username, employee);
			return employee.getHashedPass();
		} catch (DatabaseException databaseException) {
			databaseException.printStackTrace();
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Principal getPrincipal(String username) {
		Employee employee = this.userMap.get(username);
		if (employee != null) {
			RoleDao dao = DaoFactory.getRoleDao();
			try {
				Set<Role> roles = dao.get(employee.getId());
				List<String> roleNames = new ArrayList<>(roles.size() + 1);
				roleNames.add(Role.USER.name());
				for (Role role : roles)
					roleNames.add(role.name());
				employee.setRoles(roles);

				return new GenericPrincipal(employee.getLogin(),
						employee.getHashedPass(), roleNames, employee);
			} catch (DatabaseException databaseException) {
				databaseException.printStackTrace();
			}
		}
		return null;
	}
}
