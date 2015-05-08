package org.cwatch.backend.process;

import org.cwatch.backend.message.AisPosition;
import org.cwatch.backend.message.DefaultPosition;
import org.cwatch.backend.message.LritPosition;
import org.cwatch.backend.message.VmsPosition;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.google.common.collect.ImmutableMap;

public final class DefaultLatestPositionView<V> implements LatestPositionView<V> {
	
	final private int viewId;
	
	final private SimpMessagingTemplate messagingTemplate;
	
	private final String messageDestination;
	
	private final LatestPositionProfile<V> profile;
	
	public DefaultLatestPositionView(int viewId,
			SimpMessagingTemplate messagingTemplate,
			LatestPositionProfile<V> profile) {
		super();
		this.viewId = viewId;
		this.messagingTemplate = messagingTemplate;
		this.profile = profile;
		this.messageDestination = "/queue/positions/" + viewId;
		this.profile.register(this);
	}

	@Override
	public void processDefault(
			EnrichedPosition<V, ? extends DefaultPosition> position) {
		messagingTemplate.convertAndSend(
				messageDestination, 
				ImmutableMap.<String,Object>builder()
				.put("id", position.getIdentity())
				.put("lat", position.getPosition().getLatitude())
				.put("lon", position.getPosition().getLongitude())
				.put("hdg", position.getPosition().getHeading())
				.build()
		);
	}

	@Override
	public void processAis(EnrichedPosition<V, AisPosition> position) {
		processDefault(position);
	}

	@Override
	public void processLrit(EnrichedPosition<V, LritPosition> position) {
		processDefault(position);
	}

	@Override
	public void processVms(EnrichedPosition<V, VmsPosition> position) {
		processDefault(position);
	}

	@Override
	public int getViewId() {
		return viewId;
	}
}