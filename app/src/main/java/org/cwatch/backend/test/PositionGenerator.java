package org.cwatch.backend.test;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.cwatch.backend.message.TypedPosition;
import org.cwatch.backend.store.DefaultIdentityStore;
import org.cwatch.backend.store.DefaultPositionStore;
import org.cwatch.sim.PositionCallback;
import org.cwatch.sim.SimulatedPosition;
import org.cwatch.sim.StraightTrackSimulator;
import org.joda.time.DateTime;

public class PositionGenerator {
	
	Date from = DateTime.now().minusYears(1).toDate();
	Date to = DateTime.now().toDate();
	int positionCount = 100000;
	
	final ExecutorService executorService;
	
	public PositionGenerator(ExecutorService executorService) {
		super();
		this.executorService = executorService;
	}

	public <I, P extends TypedPosition<I>> void generate(
			DefaultPositionStore<I, P> store,
			Function<SimulatedPosition, P> positionFactory
	) {
		
		generate(simulatedPosition -> {
			store.save(positionFactory.apply(simulatedPosition));
		});
	}

	public void generate(PositionCallback positionCallback) {
		StraightTrackSimulator track = new StraightTrackSimulator();
		track.register(simulatedPosition -> {
			executorService.execute(() -> {
				positionCallback.onMessage(simulatedPosition);
			});
		});
		track.sendRandom(from, to, positionCount);
	}

	public <V, I, P extends TypedPosition<I>> void generate(
			V vesselId,
			DefaultIdentityStore<V, I> identityStore,
			DefaultPositionStore<I, P> positionStore,
			BiFunction<I, SimulatedPosition, P> positionFactory
	) {
		generate(
				positionStore,
				(simulatedPosition) -> positionFactory.apply(
						identityStore.getIdentifier(vesselId, simulatedPosition.getTimeStamp()),
						simulatedPosition
				)
		);
	}

	public Date getFrom() {
		return from;
	}

	public void setFrom(Date from) {
		this.from = from;
	}

	public Date getTo() {
		return to;
	}

	public void setTo(Date to) {
		this.to = to;
	}

	public int getPositionCount() {
		return positionCount;
	}

	public void setPositionCount(int positionCount) {
		this.positionCount = positionCount;
	}
	
}
