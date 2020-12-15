package de.mklinger.commons.exec.docker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.mklinger.commons.exec.CmdBuilder;
import de.mklinger.commons.exec.CmdBuilderBase;
import de.mklinger.commons.exec.CmdException;
import de.mklinger.commons.exec.CmdOutputUtil;
import de.mklinger.commons.exec.CmdSettings;

/**
 * @author Marc Klinger - mklinger[at]mklinger[dot]de
 */
public abstract class DockerCmdBuilderBase<B extends CmdBuilderBase<B>> extends CmdBuilderBase<B> {
	private String dockerExecutable = "docker";
	private final String[] dockerCommands;
	private String dockerMachine;
	private String dockerMachineExecutable = "docker-machine";

	public DockerCmdBuilderBase(final String... dockerCommands) {
		this.dockerCommands = dockerCommands;
	}

	public B dockerExecutable(final String dockerExecutable) {
		this.dockerExecutable = dockerExecutable;
		return getBuilder();
	}

	public B dockerMachine(final String dockerMachine) {
		this.dockerMachine = dockerMachine;
		return getBuilder();
	}

	protected String getDockerMachine() {
		return dockerMachine;
	}

	public B dockerMachineExecutable(final String dockerMachineExecutable) {
		this.dockerMachineExecutable = dockerMachineExecutable;
		return getBuilder();
	}

	@Override
	public CmdSettings toCmdSettings() {
		final CmdSettings cmdSettings = super.toCmdSettings();

		List<String> command = cmdSettings.getCommand();
		if (command == null) {
			command = new ArrayList<>();
			cmdSettings.setCommand(command);
		}
		command.add(0, dockerExecutable);
		for (int i = dockerCommands.length -1; i >= 0; i--) {
			command.add(1, dockerCommands[i]);
		}

		if (dockerMachine != null) {
			try {
				final String envOutput = CmdOutputUtil.executeForStdout(new CmdBuilder(dockerMachineExecutable)
						.arg("env")
						.arg("--shell")
						.arg("cmd")
						.arg(dockerMachine));
				final Map<String, String> env = parseDockerMachineEnv(envOutput);
				final Map<String, String> oldEnvironment = cmdSettings.getEnvironment();
				if (oldEnvironment != null) {
					env.putAll(oldEnvironment);
				}
				cmdSettings.setEnvironment(env);
			} catch (final CmdException e) {
				throw new RuntimeException("Error executing docker-machine", e);
			}
		}

		return cmdSettings;
	}

	private static final Pattern ENV_LINE = Pattern.compile("SET\\s+([^=]+)=(.*)");

	private Map<String, String> parseDockerMachineEnv(final String envOutput) {
		final Map<String, String> result = new HashMap<>();
		final StringTokenizer lineTokenizer = new StringTokenizer(envOutput, "\n");
		while (lineTokenizer.hasMoreTokens()) {
			final String line = lineTokenizer.nextToken().trim();
			if (line.isEmpty() || line.startsWith("REM")) {
				continue;
			}
			final Matcher m = ENV_LINE.matcher(line);
			if (m.matches()) {
				final String name = m.group(1);
				final String value = m.group(2)
						.replace("%%", "%");
				result.put(name, value);
			} else {
				throw new RuntimeException("Invalid line: '" + line + "'");
			}
		}
		return result;
	}

	protected static String requireNonEmpty(final String s) {
		if (s == null || s.isEmpty()) {
			throw new IllegalArgumentException();
		}
		return s;
	}
}
