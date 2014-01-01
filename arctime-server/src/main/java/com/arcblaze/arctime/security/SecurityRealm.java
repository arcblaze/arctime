package com.arcblaze.arctime.security;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

	/** Used to perform password hashing. */
	private final Password password = new Password();

	/** The user object that has been retrieved from the database. */
	private User user;

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
			this.user = dao.getLogin(username);
			if (this.user == null)
				return null;

			return this.user.getHashedPass();
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
		if (this.user != null) {
			RoleDao dao = DaoFactory.getRoleDao();
			try {
				Set<Role> roles = dao.get(this.user.getId());
				List<String> roleNames = new ArrayList<>(roles.size() + 1);
				roleNames.add(Role.USER.name());
				for (Role role : roles)
					roleNames.add(role.name());
				this.user.setRoles(roles);

				return new GenericPrincipal(this.user.getLogin(),
						this.user.getHashedPass(), roleNames, this.user);
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
		return this.password.hash(credentials, this.user.getSalt());
	}
}
