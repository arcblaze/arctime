package com.arcblaze.arctime.rest.login;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arcblaze.arctime.rest.BaseResource;
import com.codahale.metrics.Timer;

/**
 * The REST end-point for creating user sessions (to login).
 */
@Path("/login")
public class LoginResource extends BaseResource {
	private final static Logger log = LoggerFactory
			.getLogger(LoginResource.class);

	@Context
	private ServletContext servletContext;

	/**
	 * @param request
	 *            the request sent from the client
	 * @param uriInfo
	 *            the URI information associated with this web request
	 * @param login
	 *            the user login name to use when logging in
	 * @param password
	 *            the password to use when logging in
	 * @param redirectUri
	 *            the location to which we should redirect after a successful
	 *            login
	 * 
	 * @return a redirection to the user timesheet page
	 * 
	 * @throws URISyntaxException
	 *             if there is a problem with the created URI
	 */
	@POST
	public Response login(@Context HttpServletRequest request,
			@Context UriInfo uriInfo, @FormParam("login") String login,
			@FormParam("password") String password,
			@FormParam("redirectUri") String redirectUri)
			throws URISyntaxException {
		log.debug("User login request");
		try (Timer.Context timer = getTimer(this.servletContext, "/login")) {
			String remoteUser = request.getRemoteUser();
			if (StringUtils.isNotBlank(remoteUser)) {
				if (!remoteUser.equals(login)) {
					request.logout();
					request.login(login, password);
				} else
					log.debug("Already logged in.");
			} else
				request.login(login, password);

			String baseUri = uriInfo.getBaseUri().toString();
			baseUri = baseUri.substring(0, baseUri.indexOf("/rest"));
			return Response.seeOther(new URI(baseUri + redirectUri)).build();
		} catch (ServletException loginFailed) {
			log.error("User login failed.", loginFailed);
			throw new NotAuthorizedException("Login failed.", loginFailed);
		}
	}
}
