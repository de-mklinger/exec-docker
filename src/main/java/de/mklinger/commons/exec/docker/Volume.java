package de.mklinger.commons.exec.docker;

import java.nio.file.Paths;
import java.util.Locale;

public class Volume {
	private static final String WINDOWS_PREFIX = "c:\\users\\";

	private final String hostPathOrVolume;
	private final String containerPath;
	private final String options;

	public Volume(final String hostPathOrVolume, final String containerPath) {
		this(hostPathOrVolume, containerPath, null);
	}

	public Volume(final String hostPathOrVolume, final String containerPath, final String options) {
		this.hostPathOrVolume = hostPathOrVolume;
		this.containerPath = containerPath;
		this.options = options;
	}

	public String toArgString(final boolean forDockerMachine) {
		String actualHostPathOrVolume;

		if (forDockerMachine && Paths.get(hostPathOrVolume).isAbsolute()) {
			if (!hostPathOrVolume.toLowerCase(Locale.US).startsWith(WINDOWS_PREFIX)) {
				throw new IllegalArgumentException("For docker-machine, only host paths under C:\\Users\\ can be mounted");
			}
			actualHostPathOrVolume = "/c/Users/"
					+ hostPathOrVolume.substring(WINDOWS_PREFIX.length()).replace('\\', '/');
		} else {
			actualHostPathOrVolume = hostPathOrVolume;
		}

		if (options != null) {
			return actualHostPathOrVolume + ":" + containerPath + ":" + options;
		} else {
			return actualHostPathOrVolume + ":" + containerPath;
		}
	}
}