package com.arcblaze.arctime.rest.manager;

import java.util.Set;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import com.arcblaze.arctime.db.DaoFactory;
import com.arcblaze.arctime.db.DatabaseException;
import com.arcblaze.arctime.db.dao.UserDao;
import com.arcblaze.arctime.model.Enrichment;
import com.arcblaze.arctime.model.User;
import com.arcblaze.arctime.rest.BaseResource;
import com.codahale.metrics.Timer;

/**
 * The REST end-point for managing users.
 */
@Path("/manager/user")
public class UserResource extends BaseResource {
	@Context
	private ServletContext servletContext;

	/**
	 * @param security
	 *            the security information associated with the request
	 * @param userId
	 *            the unique id of the user to retrieve
	 * @param enrichments
	 *            indicates the additional data to be included in the returned
	 *            user
	 * 
	 * @return the requested user (if in the same company as the current user)
	 * 
	 * @throws DatabaseException
	 *             if there is an error communicating with the back-end
	 */
	@GET
	@Path("{userId}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public User one(@Context SecurityContext security,
			@PathParam("userId") Integer userId,
			@QueryParam("enrichments") Set<Enrichment> enrichments)
			throws DatabaseException {
		try (Timer.Context timer = getTimer(this.servletContext,
				"/manager/user/{userId}")) {
			User currentUser = (User) security.getUserPrincipal();
			UserDao dao = DaoFactory.getUserDao();
			return dao.get(currentUser.getCompanyId(), userId, enrichments);
		}
	}

	/**
	 * @param security
	 *            the security information associated with the request
	 * @param enrichments
	 *            indicates the additional data to be included in the returned
	 *            users
	 * 
	 * @return all of the available users in the same company as the current
	 *         user
	 * 
	 * @throws DatabaseException
	 *             if there is an error communicating with the back-end
	 */
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Set<User> all(@Context SecurityContext security,
			@QueryParam("enrichments") Set<Enrichment> enrichments)
			throws DatabaseException {
		try (Timer.Context timer = getTimer(this.servletContext,
				"/manager/user")) {
			User currentUser = (User) security.getUserPrincipal();
			UserDao dao = DaoFactory.getUserDao();
			return dao.getAll(currentUser.getCompanyId(), enrichments);
		}
	}
}
