package de.mklinger.commons.exec.docker;

import de.mklinger.commons.exec.CmdUtil;

public class DockerMachine {

	public static String getDockerMachine() {
		return CmdUtil.isWindows() ? "default" : null;
	}

}
