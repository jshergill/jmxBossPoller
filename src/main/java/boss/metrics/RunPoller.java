package boss.metrics;

import java.io.IOException;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.ws.rs.client.WebTarget;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import boss.metrics.constants.CmdConstants;
import boss.metrics.utils.CmdUtils;

/**
 * Retrieves key WildFly Metrics.
 * Output is to a log file.
 * 
 * @author jaskarn.shergill
 *
 */

public class RunPoller {
	
	private static Logger logger = LogManager.getLogger(RunPoller.class);
	
	public static void main(String[] args) throws IOException, InterruptedException {
		
		try {
			CmdUtils cmdUtils = new CmdUtils();

			long seconds = Long.parseLong(cmdUtils.getCmd(args).getOptionValue(CmdConstants.SECONDS));
			int iterations = Integer.parseInt(cmdUtils.getCmd(args).getOptionValue(CmdConstants.ITERATIONS));
			String host = cmdUtils.getCmd(args).getOptionValue(CmdConstants.HOST);
			String user = cmdUtils.getCmd(args).getOptionValue(CmdConstants.WILDFLYSER);
			String pass = cmdUtils.getCmd(args).getOptionValue(CmdConstants.WILDFLYPASS);
			int port = Integer.parseInt(cmdUtils.getCmd(args).getOptionValue(CmdConstants.PORT));
			
			JmxSingleton instance = JmxSingleton.getInstance();	
			JMXConnector jmxConnection = instance.getJmxConnection(host, user, pass, port);
			logger.info("Getting MBean server connection");
			MBeanServerConnection mBeanConnection = jmxConnection.getMBeanServerConnection();
			WebTarget webConnection = instance.getWebConnection(host, user, pass, port);
			
			JmxBossPoll.pollWildFlyMetrics(mBeanConnection,webConnection,seconds, iterations);
			
			logger.info("Polling cycle completed. Closing connection...");
			
			jmxConnection.close();
			
			logger.info("JMX connection closed");

			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
