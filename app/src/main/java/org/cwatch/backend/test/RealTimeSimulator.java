package org.cwatch.backend.test;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.cwatch.backend.message.AisPosition;
import org.cwatch.backend.message.LritPosition;
import org.cwatch.backend.message.VmsPosition;
import org.cwatch.backend.store.DefaultIdentityStore;
import org.cwatch.backend.store.MemoryAisPosition;
import org.cwatch.backend.store.MemoryLritPosition;
import org.cwatch.backend.store.MemoryVmsPosition;
import org.cwatch.sim.StraightTrackSimulator;
import org.springframework.beans.factory.annotation.Autowired;

public class RealTimeSimulator<V> {

	double reportingPeriodSeconds = 10;
	double reportingPeriodVariationSeconds = 5;
	
	@Autowired
	DefaultIdentityStore<V, Integer> mmsiIdentityStore;
	
	@Autowired
	DefaultIdentityStore<V, Integer> imoIdentityStore;
	
	@Autowired
	DefaultIdentityStore<V, String> irIdentityStore;
	
	public void simulate(
			ScheduledExecutorService executorService,
			Stream<V> vessels,
			Consumer<AisPosition> aisPosition,
			Consumer<LritPosition> lritPosition,
			Consumer<VmsPosition> vmsPosition
	) {
		vessels.forEach(v -> {
			StraightTrackSimulator sim = new StraightTrackSimulator(executorService);
			sim.randomize();
			sim.setReportingPeriodSeconds(ThreadLocalRandom.current().nextDouble(reportingPeriodSeconds-reportingPeriodVariationSeconds, reportingPeriodSeconds+reportingPeriodVariationSeconds));
			sim.register(pos -> {
				switch(ThreadLocalRandom.current().nextInt(3)) {
				case 0:
					aisPosition.accept(new MemoryAisPosition(
							mmsiIdentityStore.getIdentifier(v, pos.getTimeStamp()), 
							pos.getTimeStamp(), 
							pos.getCooordinates().getLatitude().getValue(), 
							pos.getCooordinates().getLongitude().getValue(),
							pos.getDirection() 
					));
					break;
				case 1:
					lritPosition.accept(new MemoryLritPosition(
							imoIdentityStore.getIdentifier(v, pos.getTimeStamp()), 
							pos.getTimeStamp(), 
							pos.getCooordinates().getLatitude().getValue(), 
							pos.getCooordinates().getLongitude().getValue(),
							pos.getDirection() 
					));
					break;
				case 2:
					vmsPosition.accept(new MemoryVmsPosition(
							irIdentityStore.getIdentifier(v, pos.getTimeStamp()), 
							pos.getTimeStamp(), 
							pos.getCooordinates().getLatitude().getValue(), 
							pos.getCooordinates().getLongitude().getValue(),
							pos.getDirection() 
					));
					break;
				}
				
			});
			sim.start();
		});
	}

	public double getReportingPeriodSeconds() {
		return reportingPeriodSeconds;
	}

	public void setReportingPeriodSeconds(double reportingPeriodSeconds) {
		this.reportingPeriodSeconds = reportingPeriodSeconds;
	}

	public double getReportingPeriodVariationSeconds() {
		return reportingPeriodVariationSeconds;
	}

	public void setReportingPeriodVariationSeconds(
			double reportingPeriodVariationSeconds) {
		this.reportingPeriodVariationSeconds = reportingPeriodVariationSeconds;
	}
}
