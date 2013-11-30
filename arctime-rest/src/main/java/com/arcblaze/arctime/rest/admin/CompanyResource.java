package com.arcblaze.arctime.rest.admin;

import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.arcblaze.arctime.db.DaoFactory;
import com.arcblaze.arctime.db.DatabaseException;
import com.arcblaze.arctime.model.Company;

/**
 * The REST end-point for performing admin actions on companies.
 */
@Path("/admin/company")
public class CompanyResource {
	/**
	 * @param companyId
	 *            the unique id of the company to retrieve
	 * 
	 * @return the requested company
	 * 
	 * @throws DatabaseException
	 *             if there is an error communicating with the back-end
	 */
	@GET
	@Path("{companyId}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Company one(@PathParam("companyId") Integer companyId)
			throws DatabaseException {
		return DaoFactory.getCompanyDao().get(companyId);
	}

	/**
	 * @return all of the available companies
	 * 
	 * @throws DatabaseException
	 *             if there is an error communicating with the back-end
	 */
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Set<Company> all() throws DatabaseException {
		return DaoFactory.getCompanyDao().getAll();
	}
}
