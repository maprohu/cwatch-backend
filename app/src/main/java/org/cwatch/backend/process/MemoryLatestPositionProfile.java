package org.cwatch.backend.process;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.stream.Stream;

import org.cwatch.backend.message.AisPosition;
import org.cwatch.backend.message.DefaultPosition;
import org.cwatch.backend.message.LritPosition;
import org.cwatch.backend.message.VmsPosition;
import org.cwatch.backend.store.PositionStores;
import org.joda.time.DateTime;

import com.google.common.collect.Maps;

public class MemoryLatestPositionProfile<V> implements LatestPositionProfile<V> {

	final Collection<LatestPositionView<V>> views = Collections.newSetFromMap(new WeakHashMap<>());
	
	final Map<V, EnrichedPosition<V, ? extends DefaultPosition>> positionMap = Maps.newHashMap();
	
	final private EnrichedPositionFilter<V> positionFilter;
	
	final private long timeoutMilliseconds;

	public MemoryLatestPositionProfile(
			EnrichedPositionFilter<V> positionFilter,
			long timeoutMilliseconds,
			PositionStores positionStores,
			EnrichmentService<V> enrichmentService
	) {
		this.positionFilter = positionFilter;
		this.timeoutMilliseconds = timeoutMilliseconds;
		
		Date from = DateTime.now().minus(timeoutMilliseconds).toDate();
		
		positionStores.mmsi().queryLatest(from).forEach(p -> {});
		
	}
	

	@Override
	public void register(LatestPositionView<V> view) {
		views.add(view);
	}

	@Override
	public void processDefault(EnrichedPosition<V, ? extends DefaultPosition> position) {
		// TODO Auto-generated method stub
	}

	public Stream<EnrichedPosition<V, ? extends DefaultPosition>> queryArea(
			double latitudeFrom, 
			double latitudeTo, 
			double longitudeFrom, 
			double longitudeTo
	) {
		return positionMap.values().stream().filter(p -> 
				latitudeFrom <= p.getPosition().getLatitude() &&
				p.getPosition().getLatitude() <= latitudeTo &&
				longitudeFrom <= p.getPosition().getLongitude() &&
				p.getPosition().getLongitude() <= longitudeTo 
		);
	}
	
	private boolean testTimeout(EnrichedPosition<V, ? extends DefaultPosition> position) {
		return position.getPosition().getTimestamp().getTime() > System.currentTimeMillis() - timeoutMilliseconds;
	}
	
	private synchronized boolean testNewer(EnrichedPosition<V, ? extends DefaultPosition> position) {
		EnrichedPosition<V, ? extends DefaultPosition> currentLatest = positionMap.get(position.getIdentity());
		
		if (currentLatest==null || position.getPosition().getTimestamp().after(currentLatest.getPosition().getTimestamp())) {
			positionMap.put(position.getIdentity(), position);
			return true;
		} else {
			return false;
		}
				
	}
	
	@Override
	public void processAis(EnrichedPosition<V, AisPosition> position) {
		if (testTimeout(position) && positionFilter.testAis(position) && testNewer(position)) {
			views.stream().forEach(v -> v.processAis(position));
		}
	}
	
	@Override
	public void processLrit(EnrichedPosition<V, LritPosition> position) {
		if (testTimeout(position) && positionFilter.testLrit(position) && testNewer(position)) {
			views.stream().forEach(v -> v.processLrit(position));
		}
	}

	@Override
	public void processVms(EnrichedPosition<V, VmsPosition> position) {
		if (testTimeout(position) && positionFilter.testVms(position) && testNewer(position)) {
			views.stream().forEach(v -> v.processVms(position));
		}
	}
	

}
