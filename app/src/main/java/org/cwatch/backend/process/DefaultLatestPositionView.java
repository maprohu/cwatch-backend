package org.cwatch.backend.process;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.cwatch.backend.message.AisPosition;
import org.cwatch.backend.message.DefaultPosition;
import org.cwatch.backend.message.LritPosition;
import org.cwatch.backend.message.VmsPosition;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableMap;

public final class DefaultLatestPositionView<V> implements LatestPositionView<V> {
	
	final private int viewId;
	
	final private SimpMessagingTemplate messagingTemplate;
	
	private final String messageDestination;
	
	private final LatestPositionProfile<V> profile;
	
	private final Map<V, ClientPosition> clientState = new HashMap<>();
	
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
			EnrichedPosition<V, ? extends DefaultPosition> enrichedPosition) {
		sendToClient(enrichedPosition);
	}


	private void sendToClient(
			EnrichedPosition<V, ? extends DefaultPosition> enrichedPosition) {
		V identity = enrichedPosition.getIdentity();
		DefaultPosition position = enrichedPosition.getPosition();
		ClientPosition cp = new ClientPosition(
				identity,
				position.getLatitude(),
				position.getLongitude(),
				position.getHeading()
		);
		messagingTemplate.convertAndSend(
				messageDestination, 
				cp
		);
		clientState.put(identity, cp);
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

	@Override
	public void setVieport(
			org.cwatch.backend.process.LatestPositionView.Viewport viewport) {
		// TODO Auto-generated method stub
		
	}
	
	class ClientPosition {
		
		@JsonProperty("id")
		final V identity;
		
		@JsonProperty("lat")
		final double latitude;
		
		@JsonProperty("lon")
		final double longitude;
		
		@JsonProperty("hdg")
		final double heading;

		public ClientPosition(V identity, double latitude, double longitude,
				double heading) {
			super();
			this.identity = identity;
			this.latitude = latitude;
			this.longitude = longitude;
			this.heading = heading;
		}
		
	}
	
}