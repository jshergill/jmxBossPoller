package boss.metrics;

import java.io.IOException;
import java.lang.management.MemoryMXBean;

import com.sun.management.OperatingSystemMXBean;

import boss.dto.SimpleOperationDto;
import org.xnio.management.XnioWorkerMXBean;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.JMX;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.json.JSONObject;

/**
 * Poll for Key JMX metrics from WildFly
 */
public class JmxBossPoll {

	private static Logger logger = LogManager.getLogger(JmxBossPoll.class);

	/**
	 * Retrieve worker queue size, process cpu load, heap memory used and committed,
	 * and active sessions from a WildFly server.
	 * 
	 * @param mBeanConnection
	 *            - mbean server connection
	 * @param webConnection
	 * 			  - wildfly web resource connection      
	 * @param secs
	 *            - polling time in seconds
	 * @param iterations
	 *            - number of times to poll
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@SuppressWarnings("restriction")
	public static void pollWildFlyMetrics(MBeanServerConnection mBeanConnection, WebTarget webConnection, long secs,
			int iterations) throws IOException, InterruptedException {

		XnioWorkerMXBean workerQueueSize;
		Object maxWorkerPoolSize;
		Object ioThreadCount;
		MemoryMXBean memoryMXBeanProxy;
		OperatingSystemMXBean operatingSystemMXbean;
		try {
			workerQueueSize = JMX.newMBeanProxy(mBeanConnection,
					new ObjectName("org.xnio:type=Xnio,provider=\"nio\",worker=\"default\""),
					org.xnio.management.XnioWorkerMXBean.class);
			maxWorkerPoolSize = mBeanConnection.getAttribute(
					new ObjectName("org.xnio:type=Xnio,provider=\"nio\",worker=\"default\""), "MaxWorkerPoolSize");
			ioThreadCount = mBeanConnection.getAttribute(
					new ObjectName("org.xnio:type=Xnio,provider=\"nio\",worker=\"default\""), "IoThreadCount");
			memoryMXBeanProxy = JMX.newMXBeanProxy(mBeanConnection, new ObjectName("java.lang:type=Memory"),
					MemoryMXBean.class);
			operatingSystemMXbean = JMX.newMXBeanProxy(mBeanConnection,
					new ObjectName("java.lang:type=OperatingSystem"), com.sun.management.OperatingSystemMXBean.class);

			logger.info("Max worker pool size: " + maxWorkerPoolSize);
			logger.info("Thread count: " + ioThreadCount);
			logger.info(
					"Heap Memory Max: "
							+ String.format("%.02f",
									(float) (memoryMXBeanProxy.getHeapMemoryUsage().getMax() / (Math.pow(1000, 3))))
							+ " GB");

			// poll the worker queue size
			for (int i = 0; i < iterations; i++) {
				logger.info(" WorkerQueueSize: " + workerQueueSize.getWorkerQueueSize());
				logger.info(" JVM Process CPU: "
						+ String.format("%.02f", operatingSystemMXbean.getProcessCpuLoad() * 100) + " %");
				logger.info(" System CPU: " + String.format("%.02f", operatingSystemMXbean.getSystemCpuLoad() * 100)
						+ " %");
				logger.info(" Heap Memory Used: "
						+ String.format("%.02f",
								(double) memoryMXBeanProxy.getHeapMemoryUsage().getUsed() / (Math.pow(1000, 3)))
						+ " GB");
				logger.info(" Heap Memory Committed: "
						+ String.format("%.02f",
								(double) memoryMXBeanProxy.getHeapMemoryUsage().getCommitted() / (Math.pow(1000, 3)))
						+ " GB");
				logger.info(" Active sessions: " + getActiveSessions(webConnection));
				TimeUnit.SECONDS.sleep(secs);
			}
		} catch (AttributeNotFoundException | InstanceNotFoundException | MalformedObjectNameException | MBeanException
				| ReflectionException e) {
			e.printStackTrace();
		}
	}

	public static String getActiveSessions(WebTarget webConnection) {
		List<String> address = new ArrayList<String>();
		address.add("deployment");
		address.add("synergy.ear");
		address.add("subdeployment");
		address.add("indicee.war");
		address.add("subsystem");
		address.add("undertow");
		Entity<SimpleOperationDto> operation = Entity.entity(
				new SimpleOperationDto("read-attribute", false, address, "active-sessions"),
				MediaType.APPLICATION_JSON_TYPE);
		String response = webConnection.request(MediaType.APPLICATION_JSON_TYPE)
				.header("Content-type", MediaType.APPLICATION_JSON).post(operation, String.class);
		JSONObject jsonObject = new JSONObject(response);
		String result = jsonObject.get("result").toString();
		return result;
	}

}
