package de.mklinger.commons.exec.docker;

/**
 * @author Marc Klinger - mklinger[at]mklinger[dot]de
 */
public class DockerRunCmdBuilder extends DockerCreateCmdBuilderBase<DockerRunCmdBuilder> {
	public DockerRunCmdBuilder(final String repository, final String tag) {
		super(repository, tag, "run");
	}

	public DockerRunCmdBuilder(final String image) {
		super(image, "run");
	}

	public DockerRunCmdBuilder removeOnExit() {
		return arg("--rm");
	}
}
