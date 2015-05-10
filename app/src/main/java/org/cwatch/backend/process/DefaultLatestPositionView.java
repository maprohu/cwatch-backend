package org.cwatch.backend.process;

import java.util.HashMap;
import java.util.Map;

import org.cwatch.backend.message.AisPosition;
import org.cwatch.backend.message.DefaultPosition;
import org.cwatch.backend.message.LritPosition;
import org.cwatch.backend.message.VmsPosition;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.referencing.CRS;
import org.geotools.referencing.operation.DefaultMathTransformFactory;
import org.geotools.referencing.operation.matrix.GeneralMatrix;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransformFactory;
import org.opengis.referencing.operation.Matrix;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Throwables;
import com.google.common.math.DoubleMath;

public final class DefaultLatestPositionView<V> implements LatestPositionView<V> {
	
	private static final double HEADING_TOLERANCE = 5;
	private static final int PIXEL_TOLERANCE = 1;
	
	final private int viewId;
	
	final private SimpMessagingTemplate messagingTemplate;
	
	private final String messageDestination;
	
	private final LatestPositionProfile<V> profile;
	
	private final Map<V, ClientPosition> clientState = new HashMap<>();

	private final CoordinateReferenceSystem positionCRS;
	
	private final MathTransformFactory mtf = new DefaultMathTransformFactory();
	
	private ClientViewport clientViewport;
	
	public DefaultLatestPositionView(int viewId,
			SimpMessagingTemplate messagingTemplate,
			LatestPositionProfile<V> profile) {
		super();
		this.viewId = viewId;
		this.messagingTemplate = messagingTemplate;
		this.profile = profile;
		this.messageDestination = "/queue/positions/" + viewId;
		this.profile.register(this);
		
		try {
			this.positionCRS = CRS.decode("EPSG:4326");
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}
		
		
	}


	@Override
	public void processDefault(
			EnrichedPosition<V, ? extends DefaultPosition> enrichedPosition) {
		if (clientViewport==null) {
			return;
		}
		
		ClientPosition currentClientPosition = clientState.get(enrichedPosition.getIdentity());
		
		DefaultLatestPositionView<V>.ClientPixel newPixel = clientViewport.locatePixel(enrichedPosition.getPosition().getLatitude(), enrichedPosition.getPosition().getLongitude());
	
		if (newPixel.visible) {
			
			if (currentClientPosition==null) {
				sendToClient(enrichedPosition);
			} else if (!DoubleMath.fuzzyEquals(enrichedPosition.getPosition().getHeading(), currentClientPosition.heading, HEADING_TOLERANCE)) {
				sendToClient(enrichedPosition);
			} else if (clientViewport.locatePixel(currentClientPosition).isDifferentThan(newPixel)) {
				sendToClient(enrichedPosition);
			}
			
		} else {
			
			if (currentClientPosition!=null && clientViewport.locatePixel(currentClientPosition).visible) {
				sendToClient(enrichedPosition);
			}
			
		}
		
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
		
		this.clientViewport = new ClientViewport(viewport);
		
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
		
		@JsonIgnore
		ClientViewport pixelViewport;
		
		@JsonIgnore
		ClientPixel pixel;
		
	}
	
	class ClientPixel {
		final boolean visible;
		final int x;
		final int y;
		public ClientPixel(int x, int y, boolean visible) {
			super();
			this.x = x;
			this.y = y;
			this.visible = visible;
		}
		
		public boolean isDifferentThan(ClientPixel other) {
			return visible != other.visible
					|| Math.abs(x - other.x) > PIXEL_TOLERANCE
					|| Math.abs(y - other.y) > PIXEL_TOLERANCE;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ClientPixel other = (ClientPixel) obj;
			if (visible != other.visible)
				return false;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			return true;
		}
	}
	
	class ClientViewport {

		private MathTransform transform;
		MathTransform crsTransform;
		final int minX, maxX, minY, maxY;

		public ClientViewport(Viewport viewport) {
			try {
				this.minX = -viewport.getMargin();
				this.minY = -viewport.getMargin();
				this.maxX = viewport.getWidth()+viewport.getMargin();
				this.maxY = viewport.getHeight()+viewport.getMargin();
				
				GeneralMatrix matrix4 = new GeneralMatrix(4, 4, viewport.getMatrix());
				
				Matrix matrix3 = new Matrix() {
					
					@Override
					public void setElement(int row, int column, double value) {
						throw new RuntimeException();
					}
					
					@Override
					public boolean isIdentity() {
						return false;
					}
					
					@Override
					public int getNumRow() {
						return 3;
					}
					
					@Override
					public int getNumCol() {
						return 3;
					}
					
					private final int map(int pos) {
						return pos == 2 ? 3 : pos; 
					}
					
					@Override
					public double getElement(int row, int column) {
						return matrix4.getElement(map(column), map(row));
					}
					
					public Matrix clone() {
						return this;
					}
				};
				
				crsTransform = CRS.findMathTransform(positionCRS, CRS.decode(viewport.getProjection()), true);
				this.transform = mtf.createConcatenatedTransform(
						crsTransform,
						mtf.createAffineTransform(matrix3)
//						mtf.createConcatenatedTransform(
//								mtf.createAffineTransform(matrix3),
//								new AffineTransformBuilder(ImmutableList.of(
//										new MappedPosition(
//												new DirectPosition2D(-viewport.getMargin(), -viewport.getMargin()), 
//												new DirectPosition2D(0, 0) 
//										),
//										new MappedPosition(
//												new DirectPosition2D(-viewport.getMargin(), viewport.getHeight()+viewport.getMargin()), 
//												new DirectPosition2D(0, 1) 
//										),
//										new MappedPosition(
//												new DirectPosition2D(viewport.getWidth()+viewport.getMargin(), -viewport.getMargin()), 
//												new DirectPosition2D(1, 0) 
//										)
//								)).getMathTransform()
//						)
				);
			} catch (Exception e) {
				throw Throwables.propagate(e);
			}
			
		} 
		
		public ClientPixel locatePixel(double latitude, double longitude) {
			try {
				DirectPosition2D dst = new DirectPosition2D();
				transform.transform(new DirectPosition2D(latitude, longitude), dst);
				
				return new ClientPixel((int)dst.x, (int)dst.y, !(dst.x < minX || dst.x > maxX || dst.y < minY || dst.y > maxY));
			} catch (Exception e) {
				throw Throwables.propagate(e);
			}
		}
		
		ClientPixel locatePixel(ClientPosition clientPosition) {
			if (clientPosition.pixelViewport != this) {
				clientPosition.pixel = locatePixel(clientPosition.latitude, clientPosition.longitude);
				clientPosition.pixelViewport = this;
			}
			
			return clientPosition.pixel;
		}
		
	}
	
}