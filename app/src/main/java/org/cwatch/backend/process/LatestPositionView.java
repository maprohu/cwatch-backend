package org.cwatch.backend.process;

public interface LatestPositionView<V> extends EnrichedPositionProcessor<V> {
	
	int getViewId();
	
	void setVieport(Viewport viewport);

	public interface Viewport {

		public abstract int getHeight();

		public abstract int getWidth();

		public abstract String getProjection();

		public abstract double getRotation();

		public abstract double getZoom();

		public abstract double getCenterLongitude();

		public abstract double getCenterLatitude();
		
	}
}
