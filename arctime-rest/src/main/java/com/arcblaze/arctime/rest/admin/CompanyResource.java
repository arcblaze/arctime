package com.arcblaze.arctime.rest.admin;

import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.arcblaze.arctime.db.DaoFactory;
import com.arcblaze.arctime.db.DatabaseException;
import com.arcblaze.arctime.db.dao.CompanyDao;
import com.arcblaze.arctime.model.Company;
import com.arcblaze.arctime.model.Enrichment;

/**
 * The REST end-point for performing admin actions on companies.
 */
@Path("/company")
public class CompanyResource {
	/**
	 * @param companyId
	 *            the unique id of the company to retrieve
	 * @param enrichments
	 *            indicates the additional data to be included in the returned
	 *            company
	 * 
	 * @return the requested company
	 * 
	 * @throws DatabaseException
	 *             if there is an error communicating with the back-end
	 */
	@GET
	@Path("{companyId}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Company one(@PathParam("companyId") Integer companyId,
			@QueryParam("enrichments") Set<Enrichment> enrichments)
			throws DatabaseException {
		CompanyDao dao = DaoFactory.getCompanyDao();
		return dao.get(companyId, enrichments);
	}

	/**
	 * @param enrichments
	 *            indicates the additional data to be included in the returned
	 *            company
	 * 
	 * @return all of the available companies
	 * 
	 * @throws DatabaseException
	 *             if there is an error communicating with the back-end
	 */
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Set<Company> all(
			@QueryParam("enrichments") Set<Enrichment> enrichments)
			throws DatabaseException {
		CompanyDao dao = DaoFactory.getCompanyDao();
		return dao.getAll(enrichments);
	}
}
