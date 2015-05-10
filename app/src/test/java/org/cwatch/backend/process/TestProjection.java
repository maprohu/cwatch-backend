package org.cwatch.backend.process;

import org.cwatch.backend.process.LatestPositionView.Viewport;
import org.cwatch.backend.store.VesselId;
import org.geotools.geometry.DirectPosition2D;
import org.junit.Test;
import org.mockito.Mockito;

public class TestProjection {
	
	
	@SuppressWarnings("unchecked")
	@Test
	public void test() throws Exception {
		DefaultLatestPositionView<VesselId> lpv = new DefaultLatestPositionView<VesselId>(
				0, 
				null, 
				(LatestPositionProfile<VesselId>)Mockito.mock(LatestPositionProfile.class)
		);
		
		Viewport vp = new Viewport() {
			
			@Override
			public int getWidth() {
				return 1200;
			}
			
			@Override
			public String getProjection() {
				return "EPSG:3857";
			}
			
			@Override
			public double[] getMatrix() {
				return new double[] {4.088332670837288E-4, 0.0, 0.0, 0.0, 0.0, -4.088332670837288E-4, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1056.092933688889, 2362.687768879527, 0.0, 1.0};
			}
			
			@Override
			public int getMargin() {
				return 10;
			}
			
			@Override
			public int getHeight() {
				return 800;
			}
		};
		DefaultLatestPositionView<VesselId>.ClientViewport cv = lpv.new ClientViewport(vp);
		
		DirectPosition2D pos = new DirectPosition2D(
				38.662999353355815,
				-8.966885750000001 
		);
		DirectPosition2D dst = new DirectPosition2D();
		
		cv.crsTransform.transform(pos, dst);
		System.out.println(dst);
		
//		System.out.println(cv.verify(
//				45.9005257652197, 
//				4.744051750000001,
//				null
//		));
		
		
	}

}
