package com.arcblaze.arctime.rest;

import javax.servlet.ServletContext;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.codahale.metrics.servlets.MetricsServlet;

/**
 * The base class for all resources.
 */
public class BaseResource {
	/**
	 * @param message
	 *            the message to include in the exception
	 * 
	 * @return a {@link NotFoundException} with a suitable status code and error
	 *         message
	 */
	protected NotFoundException notFound(String message) {
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
		return new BadRequestException(Response.status(Status.BAD_REQUEST)
				.entity(message).build());
	}

	/**
	 * @param context
	 *            the servlet context from which the metric registry should be
	 *            retrieved
	 * 
	 * @return an instance of the application metric registry
	 */
	protected MetricRegistry getMetricRegistry(ServletContext context) {
		return (MetricRegistry) context
				.getAttribute(MetricsServlet.METRICS_REGISTRY);
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
		MetricRegistry metricRegistry = getMetricRegistry(context);
		return metricRegistry.timer(name).time();
	}
}
