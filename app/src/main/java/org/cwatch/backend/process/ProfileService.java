package org.cwatch.backend.process;

public interface ProfileService<V> {

	String DEFAULT_PROFILE = "default";
	
	Profile<V> getProfile(String name);

}
