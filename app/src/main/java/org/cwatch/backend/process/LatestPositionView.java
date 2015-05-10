package org.cwatch.backend.process;

public interface LatestPositionView<V> extends EnrichedPositionProcessor<V> {
	
	int getViewId();
	
	void setVieport(Viewport viewport);

	public interface Viewport {

		public abstract double[] getMatrix();

		public abstract int getMargin();

		public abstract int getHeight();

		public abstract int getWidth();

		public abstract String getProjection();

		
	}
}
