package com.arcblaze.arctime.rest.admin.stats;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.math.BigDecimal;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.arcblaze.arctime.rest.BaseResource;
import com.codahale.metrics.Timer;

/**
 * The REST end-point for retrieving memory usage statistics.
 */
@Path("/admin/stats/memory")
public class MemoryUsageResource extends BaseResource {
	@Context
	private ServletContext servletContext;

	@XmlRootElement
	static class Memory {
		@XmlElement
		public Integer usage;
	}

	@XmlRootElement
	static class MemoryInfo {
		@XmlElement
		public BigDecimal usedHeap;
		@XmlElement
		public BigDecimal maxHeap;
		@XmlElement
		public BigDecimal heapPercentUsed;
		@XmlElement
		public BigDecimal usedNonHeap;
		@XmlElement
		public BigDecimal maxNonHeap;
		@XmlElement
		public BigDecimal nonHeapPercentUsed;
	}

	/**
	 * @return the heap memory usage for this system
	 */
	@GET
	@Path("heap")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Memory heap() {
		try (Timer.Context timer = getTimerNoLog(this.servletContext,
				"/admin/stats/memory/heap")) {
			MemoryUsage heap = ManagementFactory.getMemoryMXBean()
					.getHeapMemoryUsage();

			double usedHeapMegs = heap.getUsed() / 1024d / 1024d;
			double maxHeapMegs = heap.getMax() / 1024d / 1024d;
			double heapPercentUsed = usedHeapMegs / maxHeapMegs * 100d;

			Memory memory = new Memory();
			memory.usage = (int) heapPercentUsed;
			return memory;
		}
	}

	/**
	 * @return the non-heap memory usage for this system
	 */
	@GET
	@Path("nonheap")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Memory nonheap() {
		try (Timer.Context timer = getTimerNoLog(this.servletContext,
				"/admin/stats/memory/nonheap")) {
			MemoryUsage nonHeap = ManagementFactory.getMemoryMXBean()
					.getNonHeapMemoryUsage();

			double usedNonHeapMegs = nonHeap.getUsed() / 1024d / 1024d;
			double maxNonHeapMegs = nonHeap.getMax() / 1024d / 1024d;
			double nonHeapPercentUsed = usedNonHeapMegs / maxNonHeapMegs * 100d;

			Memory memory = new Memory();
			memory.usage = (int) nonHeapPercentUsed;
			return memory;
		}
	}

	/**
	 * @return an overview of the current system memory statistics
	 */
	@GET
	@Path("info")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public MemoryInfo info() {
		try (Timer.Context timer = getTimerNoLog(this.servletContext,
				"/admin/stats/memory/info")) {
			MemoryUsage heap = ManagementFactory.getMemoryMXBean()
					.getHeapMemoryUsage();
			MemoryUsage nonHeap = ManagementFactory.getMemoryMXBean()
					.getNonHeapMemoryUsage();

			MemoryInfo info = new MemoryInfo();

			double usedHeap = heap.getUsed() / 1024d / 1024d;
			double maxHeap = heap.getMax() / 1024d / 1024d;
			double heapPercentUsed = usedHeap / maxHeap * 100d;

			double usedNonHeap = nonHeap.getUsed() / 1024d / 1024d;
			double maxNonHeap = nonHeap.getMax() / 1024d / 1024d;
			double nonHeapPercentUsed = usedNonHeap / maxNonHeap * 100d;

			info.usedHeap = new BigDecimal(usedHeap).setScale(2,
					BigDecimal.ROUND_HALF_UP);
			info.maxHeap = new BigDecimal(maxHeap).setScale(2,
					BigDecimal.ROUND_HALF_UP);
			info.heapPercentUsed = new BigDecimal(heapPercentUsed).setScale(2,
					BigDecimal.ROUND_HALF_UP);

			info.usedNonHeap = new BigDecimal(usedNonHeap).setScale(2,
					BigDecimal.ROUND_HALF_UP);
			info.maxNonHeap = new BigDecimal(maxNonHeap).setScale(2,
					BigDecimal.ROUND_HALF_UP);
			info.nonHeapPercentUsed = new BigDecimal(nonHeapPercentUsed)
					.setScale(2, BigDecimal.ROUND_HALF_UP);

			return info;
		}
	}
}
