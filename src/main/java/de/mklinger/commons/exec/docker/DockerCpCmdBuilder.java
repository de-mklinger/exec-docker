package de.mklinger.commons.exec.docker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import de.mklinger.commons.exec.CmdSettings;
import de.mklinger.commons.exec.io.IOUtils;

/**
 * @author Marc Klinger - mklinger[at]mklinger[dot]de
 */
public class DockerCpCmdBuilder extends DockerCmdBuilderBase<DockerCpCmdBuilder> {
	private String srcContainer;
	private String srcPath;
	private InputStream src;
	private String destContainer;
	private String destPath;
	private OutputStream dest;

	public DockerCpCmdBuilder() {
		super("cp");
	}

	public DockerCpCmdBuilder from(final String srcContainer, final String srcPath) {
		this.destContainer = null;
		this.srcContainer = Objects.requireNonNull(srcContainer);
		this.srcPath = Objects.requireNonNull(srcPath);
		this.src = null;
		return this;
	}

	public DockerCpCmdBuilder from(final String srcPath) {
		this.srcContainer = null;
		this.srcPath = Objects.requireNonNull(srcPath);
		this.src = null;
		return this;
	}

	public DockerCpCmdBuilder from(final Path srcPath) {
		return from(srcPath.toString());
	}

	public DockerCpCmdBuilder from(final InputStream src) {
		this.srcContainer = null;
		this.srcPath = "-";
		this.src = src;
		return this;
	}

	public DockerCpCmdBuilder to(final String destContainer, final String destPath) {
		this.srcContainer = null;
		this.destContainer = Objects.requireNonNull(destContainer);
		this.destPath = Objects.requireNonNull(destPath);
		this.dest = null;
		return this;
	}

	public DockerCpCmdBuilder to(final String destPath) {
		this.destContainer = null;
		this.destPath = Objects.requireNonNull(destPath);
		this.dest = null;
		return this;
	}

	public DockerCpCmdBuilder to(final Path destPath) {
		return from(destPath.toString());
	}

	public DockerCpCmdBuilder to(final OutputStream dest) {
		this.destContainer = null;
		this.destPath = "-";
		this.dest = Objects.requireNonNull(dest);
		return this;
	}

	public DockerCpCmdBuilder archive() {
		arg("-a");
		return this;
	}

	@Override
	public CmdSettings toCmdSettings() {
		final CmdSettings cmdSettings = super.toCmdSettings();
		final List<String> command = cmdSettings.getCommand();

		if ((srcContainer != null && destContainer != null) || (srcContainer == null && destContainer == null)) {
			throw new IllegalStateException("Either from or to must be a container");
		}

		if (srcContainer != null) {
			command.add(srcContainer + ":" + srcPath);
		} else {
			command.add(srcPath);
		}

		if (destContainer != null) {
			command.add(destContainer + ":" + destPath);
		} else {
			command.add(destPath);
		}

		if (src != null && dest != null) {
			throw new IllegalStateException();
		}

		if (src != null) {
			// TODO exec should support stdIn InputStream
			final ByteArrayOutputStream bout = new ByteArrayOutputStream();
			try {
				IOUtils.copy(src, bout);
				src.close();
			} catch (final IOException e) {
				throw new UncheckedIOException(e);
			}
			cmdSettings.setStdinBytes(bout.toByteArray());
		}

		if (dest != null) {
			cmdSettings.setStdout(dest);
		}

		return cmdSettings;
	}
}
