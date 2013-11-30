package com.arcblaze.arctime.rest.manager;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import com.arcblaze.arctime.db.DaoFactory;
import com.arcblaze.arctime.db.DatabaseException;
import com.arcblaze.arctime.db.dao.CompanyDao;
import com.arcblaze.arctime.model.Company;
import com.arcblaze.arctime.model.Employee;

/**
 * The REST end-point for performing management actions on companies.
 */
@Path("/manager/company")
public class CompanyResource {
	/** Security information associated with the web request. */
	@Context
	private SecurityContext security = null;

	/**
	 * @return the company in which the current user account resides
	 * 
	 * @throws DatabaseException
	 *             if there is an error communicating with the back-end
	 */
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Company mine() throws DatabaseException {
		Employee currentUser = (Employee) this.security.getUserPrincipal();
		CompanyDao dao = DaoFactory.getCompanyDao();
		return dao.get(currentUser.getCompanyId());
	}
}
