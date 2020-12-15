package de.mklinger.commons.exec.docker;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.mklinger.commons.exec.CmdSettings;

/**
 * @author Marc Klinger - mklinger[at]mklinger[dot]de
 */
public class DockerCreateCmdBuilderBase<B extends DockerCreateCmdBuilderBase<B>> extends DockerCmdBuilderBase<B> {
	private final String image;
	private List<Volume> volumes;

	public DockerCreateCmdBuilderBase(final String repository, final String tag, final String dockerCommand) {
		this(toImage(repository, tag), dockerCommand);
	}

	private static String toImage(final String repository, final String tag) {
		requireNonEmpty(repository);
		if (tag != null && !tag.isEmpty()) {
			return repository + ":" + tag;
		} else {
			return repository;
		}
	}

	public DockerCreateCmdBuilderBase(final String image, final String dockerCommand) {
		super(dockerCommand);
		this.image = requireNonEmpty(image);
	}

	public B name(final String name) {
		return arg("--name")
				.arg(requireNonEmpty(name));
	}

	public B cidFile(final Path cidFile) {
		return arg("--cidfile")
				.arg(cidFile.toString());
	}

	public B environment(final String name) {
		return environment(name, null);
	}

	@Override
	public B environment(final String name, final String value) {
		String envArg;
		if (value == null) {
			envArg = name;
		} else {
			envArg = name + "=" + value;
		}

		return arg("-e")
				.arg(envArg);
	}

	public B publish(final int hostPort, final int containerPort) {
		return publish(null, hostPort, containerPort, null);
	}

	public B publish(final int hostPort, final int containerPort, final String protocol) {
		return publish(null, hostPort, containerPort, protocol);
	}

	public B publish(final int containerPort) {
		return publish(null, 0, containerPort, null);
	}

	public B publish(final int containerPort, final String protocol) {
		return publish(null, 0, containerPort, protocol);
	}

	public B publish(final String ip, final int hostPort, final int containerPort) {
		return publish(ip, hostPort, containerPort, null);
	}

	/* -p=[]      : Publish a container᾿s port or a range of ports to the host
	 *                format: ip:hostPort:containerPort | ip::containerPort | hostPort:containerPort | containerPort
	 *                Both hostPort and containerPort can be specified as a
	 *                range of ports. When specifying ranges for both, the
	 *                number of container ports in the range must match the
	 *                number of host ports in the range, for example:
	 *                    -p 1234-1236:1234-1236/tcp
	 *
	 *                When specifying a range for hostPort only, the
	 *                containerPort must not be a range.  In this case the
	 *                container port is published somewhere within the
	 *                specified hostPort range. (e.g., `-p 1234-1236:1234/tcp`)
	 */
	public B publish(final String ip, final int hostPort, final int containerPort, final String protocol) {
		final StringBuilder publishArg = new StringBuilder();
		if (ip != null) {
			publishArg.append(ip);
			publishArg.append(':');
		}
		if (hostPort > 0) {
			publishArg.append(hostPort);
			publishArg.append(':');
		}
		if (containerPort <= 0) {
			throw new IllegalArgumentException("Invalid container port: " + containerPort);
		}
		publishArg.append(containerPort);
		if (protocol != null) {
			publishArg.append('/');
			publishArg.append(protocol);
		}

		return arg("-p")
				.arg(publishArg.toString());
	}

	public B network(final String network) {
		return arg("--network").arg(requireNonEmpty(network));
	}

	public B networkAlias(final String networkAlias) {
		return arg("--network-alias").arg(requireNonEmpty(networkAlias));
	}

	public B volume(final String hostPathOrVolume, final String containerPath) {
		return volume(hostPathOrVolume, containerPath, null);
	}

	public B volume(final String hostPathOrVolume, final String containerPath, final String options) {
		Objects.requireNonNull(hostPathOrVolume);
		Objects.requireNonNull(containerPath);

		if (volumes == null) {
			volumes = new ArrayList<>();
		}

		if (options != null && !options.isEmpty()) {
			volumes.add(new Volume(hostPathOrVolume, containerPath, options));
		} else {
			volumes.add(new Volume(hostPathOrVolume, containerPath));
		}
		return getBuilder();
	}

	@Override
	public CmdSettings toCmdSettings() {
		final CmdSettings cmdSettings = super.toCmdSettings();

		if (volumes != null) {
			final boolean isDockerMachine = getDockerMachine() != null;
			for (final Volume volume : volumes) {
				cmdSettings.getCommand().add("-v");
				cmdSettings.getCommand().add(volume.toArgString(isDockerMachine));
			}
		}

		cmdSettings.getCommand().add(image);
		return cmdSettings;
	}
}
