package boss.metrics.utils;
import org.apache.commons.cli.CommandLine;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import boss.metrics.constants.CmdConstants;

public class CmdUtils {

	public CommandLine getCmd(String args[]) throws ParseException {
		// commons cli setup
		Options options = new Options();
		options.addOption(CmdConstants.SECONDS, true, "Desc: time to poll metrics in seconds");
		options.addOption(CmdConstants.ITERATIONS, true, "Desc: Number of poll events");
		options.addOption(CmdConstants.HOST, true, "Desc: Application IP or FQDN");
		options.addOption(CmdConstants.WILDFLYSER, true, "Desc: WildFly user");
		options.addOption(CmdConstants.WILDFLYPASS, true, "Desc: Wildfly pass");
		options.addOption(CmdConstants.PORT, true, "Desc: WildFly port ");

		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = parser.parse(options, args);

		return cmd;

	}

}
