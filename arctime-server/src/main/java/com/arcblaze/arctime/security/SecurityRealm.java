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
import com.arcblaze.arctime.db.dao.RoleDao;
import com.arcblaze.arctime.db.dao.UserDao;
import com.arcblaze.arctime.model.Password;
import com.arcblaze.arctime.model.Role;
import com.arcblaze.arctime.model.User;

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
	private final Map<String, User> userMap = new ConcurrentHashMap<>();

	/**
	 * @param realmName
	 *            the name of the realm
	 */
	public SecurityRealm(String realmName) {
		this.realmName = realmName;
		setDigest(Password.HASH_ALGORITHM);
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
		UserDao dao = DaoFactory.getUserDao();
		try {
			User user = dao.getLogin(username);
			if (user == null)
				return null;

			this.userMap.put(username, user);
			return user.getHashedPass();
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
		User user = this.userMap.get(username);
		if (user != null) {
			RoleDao dao = DaoFactory.getRoleDao();
			try {
				Set<Role> roles = dao.get(user.getId());
				List<String> roleNames = new ArrayList<>(roles.size() + 1);
				roleNames.add(Role.USER.name());
				for (Role role : roles)
					roleNames.add(role.name());
				user.setRoles(roles);

				return new GenericPrincipal(user.getLogin(),
						user.getHashedPass(), roleNames, user);
			} catch (DatabaseException databaseException) {
				databaseException.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String digest(String credentials) {
		return new Password().hash(credentials);
	}
}
