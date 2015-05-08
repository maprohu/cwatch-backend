package org.cwatch.backend.process;

import java.util.HashMap;
import java.util.Map;

public class MemoryProfileService<V> implements ProfileService<V> {

	final Map<String, Profile<V>> profiles = new HashMap<>();
	
	public MemoryProfileService() {
		profiles.put(DEFAULT_PROFILE, new Profile<V>() {
			@Override
			public EnrichedPositionFilter<V> getPositionFilter() {
				return EnrichedPositionFilter.all();
			}
		});
	}
	
	@Override
	public Profile<V> getProfile(String name) {
		return profiles.get(name);
	}

}
