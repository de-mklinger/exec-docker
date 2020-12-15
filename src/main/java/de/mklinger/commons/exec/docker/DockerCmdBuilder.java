package de.mklinger.commons.exec.docker;

/**
 * @author Marc Klinger - mklinger[at]mklinger[dot]de
 */
public class DockerCmdBuilder extends DockerCmdBuilderBase<DockerCmdBuilder> {
	public DockerCmdBuilder(final String... dockerCommands) {
		super(dockerCommands);
	}
}
