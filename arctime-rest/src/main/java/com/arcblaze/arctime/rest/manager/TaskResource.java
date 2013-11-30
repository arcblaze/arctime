package com.arcblaze.arctime.rest.manager;

import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import com.arcblaze.arctime.db.DaoFactory;
import com.arcblaze.arctime.db.DatabaseException;
import com.arcblaze.arctime.db.dao.TaskDao;
import com.arcblaze.arctime.model.Employee;
import com.arcblaze.arctime.model.Task;

/**
 * The REST end-point for managing tasks.
 */
@Path("/manager/task")
public class TaskResource {
	/**
	 * @param security
	 *            the security information associated with the request
	 * @param taskId
	 *            the unique id of the task to retrieve
	 * 
	 * @return the requested task (if in the same company as the current user)
	 * 
	 * @throws DatabaseException
	 *             if there is an error communicating with the back-end
	 */
	@GET
	@Path("{taskId}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Task one(@Context SecurityContext security,
			@PathParam("taskId") Integer taskId) throws DatabaseException {
		Employee currentUser = (Employee) security.getUserPrincipal();
		TaskDao dao = DaoFactory.getTaskDao();
		return dao.get(currentUser.getCompanyId(), taskId);
	}

	/**
	 * @param security
	 *            the security information associated with the request
	 * 
	 * @return all of the available tasks in the same company as the current
	 *         user
	 * 
	 * @throws DatabaseException
	 *             if there is an error communicating with the back-end
	 */
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Set<Task> all(@Context SecurityContext security)
			throws DatabaseException {
		Employee currentUser = (Employee) security.getUserPrincipal();
		TaskDao dao = DaoFactory.getTaskDao();
		return dao.getAll(currentUser.getCompanyId());
	}
}
