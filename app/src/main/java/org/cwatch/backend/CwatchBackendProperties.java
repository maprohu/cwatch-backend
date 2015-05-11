package org.cwatch.backend;

import java.io.File;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="cwatch-backend")
public class CwatchBackendProperties {
	
	private File dataDir = new File("target/data");

	public File getDataDir() {
		return dataDir;
	}

	public void setDataDir(File dataDir) {
		this.dataDir = dataDir;
	}
	
	

}
