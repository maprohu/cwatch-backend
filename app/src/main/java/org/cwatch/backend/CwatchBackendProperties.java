package org.cwatch.backend;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="cwatch-backend")
public class CwatchBackendProperties {
	
	private String dataDir = "target/data";

	public String getDataDir() {
		return dataDir;
	}

	public void setDataDir(String dataDir) {
		this.dataDir = dataDir;
	}
	
	

}
