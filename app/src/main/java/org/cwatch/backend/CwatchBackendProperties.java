package org.cwatch.backend;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="cwatch-backend")
public class CwatchBackendProperties {
	
	private File dataDir = new File("target/data");
	
	private long latestPositionTimeoutMilliseconds = TimeUnit.MILLISECONDS.convert(24, TimeUnit.HOURS);

	public File getDataDir() {
		return dataDir;
	}

	public void setDataDir(File dataDir) {
		this.dataDir = dataDir;
	}

	public long getLatestPositionTimeoutMilliseconds() {
		return latestPositionTimeoutMilliseconds;
	}

	public void setLatestPositionTimeoutMilliseconds(
			long latestPositionTimeoutMilliseconds) {
		this.latestPositionTimeoutMilliseconds = latestPositionTimeoutMilliseconds;
	}
	
	

}
