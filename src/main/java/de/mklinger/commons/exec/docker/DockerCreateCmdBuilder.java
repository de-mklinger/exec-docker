package de.mklinger.commons.exec.docker;

/**
 * @author Marc Klinger - mklinger[at]mklinger[dot]de
 */
public class DockerCreateCmdBuilder extends DockerCreateCmdBuilderBase<DockerCreateCmdBuilder> {
	public DockerCreateCmdBuilder(final String repository, final String tag) {
		super(repository, tag, "create");
	}

	public DockerCreateCmdBuilder(final String image) {
		super(image, "create");
	}
}
