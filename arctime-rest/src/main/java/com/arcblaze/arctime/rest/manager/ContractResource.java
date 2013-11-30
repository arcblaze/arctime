package com.arcblaze.arctime.rest.manager;

import java.util.Set;

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
import com.arcblaze.arctime.db.dao.ContractDao;
import com.arcblaze.arctime.model.Contract;
import com.arcblaze.arctime.model.Employee;
import com.arcblaze.arctime.model.Enrichment;

/**
 * The REST end-point for managing contracts.
 */
@Path("/manager/contract")
public class ContractResource {
	/**
	 * @param security
	 *            the security information associated with the request
	 * @param contractId
	 *            the unique id of the contract to retrieve
	 * @param enrichments
	 *            indicates the additional data to be included in the returned
	 *            contract
	 * 
	 * @return the requested contract (if in the same company as the current
	 *         user)
	 * 
	 * @throws DatabaseException
	 *             if there is an error communicating with the back-end
	 */
	@GET
	@Path("{contractId}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Contract one(@Context SecurityContext security,
			@PathParam("contractId") Integer contractId,
			@QueryParam("enrichments") Set<Enrichment> enrichments)
			throws DatabaseException {
		Employee currentUser = (Employee) security.getUserPrincipal();
		ContractDao dao = DaoFactory.getContractDao();
		return dao.get(currentUser.getCompanyId(), contractId, enrichments);
	}

	/**
	 * @param security
	 *            the security information associated with the request
	 * @param enrichments
	 *            indicates the additional data to be included in the returned
	 *            contracts
	 * 
	 * @return all of the available contracts in the same company as the current
	 *         user
	 * 
	 * @throws DatabaseException
	 *             if there is an error communicating with the back-end
	 */
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Set<Contract> all(@Context SecurityContext security,
			@QueryParam("enrichments") Set<Enrichment> enrichments)
			throws DatabaseException {
		Employee currentUser = (Employee) security.getUserPrincipal();
		ContractDao dao = DaoFactory.getContractDao();
		return dao.getAll(currentUser.getCompanyId(), enrichments);
	}
}
