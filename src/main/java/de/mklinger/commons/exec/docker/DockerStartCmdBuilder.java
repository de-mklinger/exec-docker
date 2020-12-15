package de.mklinger.commons.exec.docker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.mklinger.commons.exec.CmdSettings;

/**
 * @author Marc Klinger - mklinger[at]mklinger[dot]de
 */
public class DockerStartCmdBuilder extends DockerCmdBuilderBase<DockerStartCmdBuilder> {
	private final List<String> containers;

	public DockerStartCmdBuilder(final String... containers) {
		super("start");
		this.containers = new ArrayList<>();
		Collections.addAll(this.containers, containers);
	}

	public DockerStartCmdBuilder container(final String container) {
		this.containers.add(container);
		return this;
	}

	public DockerStartCmdBuilder attach() {
		arg("-a");
		return this;
	}

	@Override
	public CmdSettings toCmdSettings() {
		final CmdSettings cmdSettings = super.toCmdSettings();

		if (containers.isEmpty()) {
			throw new IllegalStateException("No containers given");
		}

		cmdSettings.getCommand().addAll(containers);

		return cmdSettings;
	}
}
