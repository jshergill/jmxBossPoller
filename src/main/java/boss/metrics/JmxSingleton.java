package boss.metrics;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

public class JmxSingleton {

	private static Logger logger = LogManager.getLogger(JmxSingleton.class);

	private static volatile JmxSingleton instance = null;
	private static volatile JMXConnector jmxConn = null;// null for lazy loading
	private static volatile WebTarget webConn = null;// volatile to force threads to read from main memory instead of
														// cpu cache

	private JmxSingleton() {

		if (jmxConn != null) {
			throw new RuntimeException("Use getJmxConnection() method to create"); // for users using reflection to access
																				// private method
		}
		if (webConn != null) {
			throw new RuntimeException("Use getWebConnection() method to create"); 
		}
		if (instance != null) {
			throw new RuntimeException("Use getInstance() method to create");
		}
	}

	public static JmxSingleton getInstance() {
		if (instance == null) {
			synchronized (JmxSingleton.class) { // will run only once.first thread will acquire lock and run code block.
												// subsequent threads
												// will wait due to synchronized block.
				if (instance == null) { // instance will be created and subsequent threads will back out
										// due to instance already being created
					instance = new JmxSingleton(); // only created when asked for instead of at startup
				}
			}
		}
		return instance;
	}

	/**
	 * Creates a JMX connection to the WildFly server only once
	 * 
	 * @param host
	 *            - WIldfly IP or FQDN
	 * @param user
	 *            - Wildfly user
	 * @param pass
	 * @param port
	 *            - Wildfly port
	 * @return A JMXConnector object representing the new connection.
	 * @throws MalformedURLException
	 */

	public JMXConnector getJmxConnection(String host, String user, String pass, int port) throws MalformedURLException {
		if (jmxConn == null) {
			synchronized (JmxSingleton.class) { // ensure class is instantiated first
				if (jmxConn == null) {
					// Get a connection to the WildFly MBean server
					String gethost = host;
					int flyPort = port; // management-web port
					String urlString = System.getProperty("jmx.service.url",
							"service:jmx:remote+http://" + gethost + ":" + flyPort);
					JMXServiceURL serviceURL = new JMXServiceURL(urlString);
					Map<String, String[]> environment = new HashMap<>();
					String[] creds = new String[2];
					creds[0] = user;
					creds[1] = pass;
					environment.put(JMXConnector.CREDENTIALS, creds);
					try {
						jmxConn = JMXConnectorFactory.connect(serviceURL, environment);
					} catch (IOException e) {
						logger.error("Error creating JMX Connection: " + e.getMessage(), e);
					}
				}
			}
		}
		return jmxConn;
	}

	public WebTarget getWebConnection(String host, String user, String pass, int port) {
		if (webConn == null) {
			synchronized (JmxSingleton.class) {
				if(webConn == null) {
					HttpAuthenticationFeature feature = HttpAuthenticationFeature.digest(user, pass);
					Client client = ClientBuilder.newClient();
					client.register(feature);
					WebTarget managementResource = client.target("http://" + host + ":" + port + "/management");
					webConn = managementResource;
				}
			}
		}
		return webConn;
	}

}
