package de.mklinger.commons.exec.docker;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.mklinger.commons.exec.CmdException;
import de.mklinger.commons.exec.CmdOutputException;
import de.mklinger.commons.exec.CmdOutputUtil;

/**
 * @author Marc Klinger - mklinger[at]mklinger[dot]de
 */
public class DockerNetworks {
	public static void createAttachable(final String name) {
		try {
			CmdOutputUtil.executeForOutput(new DockerCmdBuilder("network", "create")
					.arg("--attachable")
					.arg(name)
					.dockerMachine(DockerMachine.getDockerMachine()));
		} catch (final CmdException e) {
			throw new DockerException("Error creating docker network", e);
		}
	}

	public static void rm(final String nameOrId) {
		try {
			CmdOutputUtil.executeForOutput(new DockerCmdBuilder("network", "rm")
					.arg(nameOrId)
					.dockerMachine(DockerMachine.getDockerMachine()));
		} catch (final CmdException e) {
			throw new DockerException("Error removing docker network", e);
		}
	}

	public static boolean exists(final String nameOrId) {
		for (final DockerNetwork network : ls()) {
			if (network.getName().equals(nameOrId) || network.getNetworkId().equals(nameOrId)) {
				return true;
			}
		}
		return false;
	}

	public static List<DockerNetwork> ls() {
		String networksOutput;
		try {
			networksOutput = CmdOutputUtil.executeForStdout(new DockerCmdBuilder("network", "ls")
					.dockerMachine(DockerMachine.getDockerMachine()));
		} catch (final CmdException e) {
			throw new DockerException("Error getting docker networks", e);
		}
		final List<DockerNetwork> networks = new ArrayList<>();
		final StringTokenizer st = new StringTokenizer(networksOutput, "\n");
		boolean firstLine = true;
		while (st.hasMoreTokens()) {
			final String line = st.nextToken();
			if (firstLine) {
				firstLine = false;
				continue;
			}
			networks.add(DockerNetwork.parseLsLine(line));
		}
		return networks;
	}

	public static class DockerNetwork {
		private final String networkId;
		private final String name;
		private final String driver;
		private final String scope;

		public DockerNetwork(final String networkId, final String name, final String driver, final String scope) {
			this.networkId = networkId;
			this.name = name;
			this.driver = driver;
			this.scope = scope;
		}

		private static final Pattern LS_LINE_PATTERN = Pattern.compile("([a-f0-9]+)\\s+([^\\s]+)\\s+([^\\s]+)\\s+([^\\s]+)\\s*");

		public static DockerNetwork parseLsLine(final String line) {
			final Matcher matcher = LS_LINE_PATTERN.matcher(line);
			if (!matcher.matches()) {
				throw new IllegalArgumentException();
			}
			return new DockerNetwork(
					matcher.group(1),
					matcher.group(2),
					matcher.group(3),
					matcher.group(4));
		}

		public String getNetworkId() {
			return networkId;
		}

		public String getName() {
			return name;
		}

		public String getDriver() {
			return driver;
		}

		public String getScope() {
			return scope;
		}
	}

	public static class DockerException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		private String output;

		private DockerException(final String message, final Exception cause) {
			super(message, cause);
			if (cause instanceof CmdOutputException) {
				output = ((CmdOutputException)cause).getOutput();
			}
		}

		public boolean containsOutput(final String s) {
			return output != null && output.contains(s);
		}
	}
}
