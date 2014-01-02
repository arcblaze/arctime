package com.arcblaze.arctime.rest;

import javax.mail.MessagingException;
import javax.servlet.ServletContext;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arcblaze.arctime.db.DatabaseException;
import com.arcblaze.arctime.model.User;
import com.arcblaze.arctime.model.util.HolidayConfigurationException;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.codahale.metrics.health.HealthCheck;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.servlets.HealthCheckServlet;
import com.codahale.metrics.servlets.MetricsServlet;

/**
 * The base class for all resources.
 */
public class BaseResource extends HealthCheck {
	private final static Logger log = LoggerFactory
			.getLogger(BaseResource.class);

	/**
	 * @param message
	 *            the message to include in the exception
	 * 
	 * @return a {@link NotFoundException} with a suitable status code and error
	 *         message
	 */
	protected NotFoundException notFound(String message) {
		log.error(message);
		return new NotFoundException(Response.status(Status.NOT_FOUND)
				.entity(message).build());
	}

	/**
	 * @param message
	 *            the message to include in the exception
	 * 
	 * @return a {@link BadRequestException} with a suitable status code and
	 *         error message
	 */
	protected BadRequestException badRequest(String message) {
		log.error(message);
		return new BadRequestException(Response.status(Status.BAD_REQUEST)
				.entity(message).build());
	}

	/**
	 * @param user
	 *            the user account that performed the unauthorized action
	 * @param message
	 *            the message to include in the exception
	 * 
	 * @return a {@link ForbiddenException} with a suitable status code and
	 *         error message
	 */
	protected ForbiddenException forbidden(User user, String message) {
		// This will cause the health check to fail.
		this.failure = new Exception("User " + user
				+ " attempted to perform an unauthorized action: " + message);

		log.error("User attempted to perform an unauthorized action: " + user);
		log.error(message);
		return new ForbiddenException(Response.status(Status.FORBIDDEN)
				.entity(message).build());
	}

	/**
	 * @param exception
	 *            the database exception
	 * 
	 * @return an {@link InternalServerErrorException} with a suitable status
	 *         code and error message
	 */
	protected InternalServerErrorException dbError(DatabaseException exception) {
		// This will cause the health check to fail.
		this.failure = exception;

		log.error("Database error", exception);
		return new InternalServerErrorException(Response
				.status(Status.INTERNAL_SERVER_ERROR)
				.entity(exception.getMessage()).build());
	}

	/**
	 * @param exception
	 *            the exception that occurred on the server
	 * 
	 * @return an {@link InternalServerErrorException} with a suitable status
	 *         code and error message
	 */
	protected InternalServerErrorException serverError(Exception exception) {
		// This will cause the health check to fail.
		this.failure = exception;

		log.error("Server error", exception);
		return new InternalServerErrorException(Response
				.status(Status.INTERNAL_SERVER_ERROR)
				.entity(exception.getMessage()).build());
	}

	/**
	 * @param exception
	 *            the holiday configuration exception
	 * 
	 * @return a {@link InternalServerErrorException} with a suitable status
	 *         code and error message
	 */
	protected InternalServerErrorException holidayError(
			HolidayConfigurationException exception) {
		// This will cause the health check to fail.
		this.failure = exception;

		log.error("Holiday error", exception);
		return new InternalServerErrorException(Response
				.status(Status.INTERNAL_SERVER_ERROR)
				.entity(exception.getMessage()).build());
	}

	/**
	 * @param exception
	 *            the database exception
	 * 
	 * @return a {@link InternalServerErrorException} with a suitable status
	 *         code and error message
	 */
	protected InternalServerErrorException mailError(
			MessagingException exception) {
		// This will cause the health check to fail.
		this.failure = exception;

		log.error("Mail error", exception);
		String message = exception.getMessage();
		if (message == null)
			message = "Failed to send email.";
		return new InternalServerErrorException(Response
				.status(Status.INTERNAL_SERVER_ERROR)
				.entity(exception.getMessage()).build());
	}

	/**
	 * @param context
	 *            the servlet context from which the metric registry should be
	 *            retrieved
	 * 
	 * @return an instance of the application metric registry
	 */
	protected MetricRegistry getMetricRegistry(ServletContext context) {
		if (context == null)
			return null;

		return (MetricRegistry) context
				.getAttribute(MetricsServlet.METRICS_REGISTRY);
	}

	/**
	 * @param context
	 *            the servlet context from which the health check registry
	 *            should be retrieved
	 * 
	 * @return an instance of the application health check registry
	 */
	protected HealthCheckRegistry getHealthCheckRegistry(ServletContext context) {
		if (context == null)
			return null;

		return (HealthCheckRegistry) context
				.getAttribute(HealthCheckServlet.HEALTH_CHECK_REGISTRY);
	}

	/**
	 * @param context
	 *            the servlet context from which the metric timer should be
	 *            retrieved
	 * @param name
	 *            the name of the timer to retrieve
	 * 
	 * @return an instance of the requested timer
	 */
	protected Timer.Context getTimer(ServletContext context, String name) {
		log.info(name);

		MetricRegistry metricRegistry = getMetricRegistry(context);
		if (metricRegistry == null)
			return null;

		HealthCheckRegistry healthCheckRegistry = getHealthCheckRegistry(context);
		if (healthCheckRegistry != null)
			healthCheckRegistry.register(name, this);

		return metricRegistry.timer(name).time();
	}

	/** A system error resulting in failed health checks. */
	private Throwable failure = null;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Result check() throws Exception {
		if (this.failure != null)
			return Result.unhealthy(this.failure);
		return Result.healthy();
	}
}
