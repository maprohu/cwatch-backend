package org.cwatch.backend.test;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiFunction;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.cwatch.backend.store.DefaultIdentityStore;
import org.cwatch.backend.store.VesselId;
import org.joda.time.DateTime;

import com.google.common.collect.Range;

public class IdentityGenerator<V> {
	
	Date from = DateTime.now().minusYears(1).toDate();
	Date to = DateTime.now().toDate();
	int vesselCount = 1000;
	int changeCount = 3;
	
	final IntFunction<V> vesselIdFunction;
	
	public IdentityGenerator(IntFunction<V> vesselIdFunction) {
		super();
		this.vesselIdFunction = vesselIdFunction;
	}

	public <I> void generate(DefaultIdentityStore<V, I> store, BiFunction<V, Optional<Date>, I> identityFunction) {
		getVessels().forEach(
				vessel -> {
					store.set(vessel, Range.<Date>all(), identityFunction.apply(vessel, Optional.<Date>empty()));
					
					IntStream.range(0, changeCount)
						.mapToObj(c -> new Date(ThreadLocalRandom.current().nextLong(from.getTime(), to.getTime())))
						.sorted()
						.forEach(date -> store.set(vessel, Range.atLeast(date), identityFunction.apply(vessel, Optional.of(date))));
				}
		);
	}

	public Stream<V> getVessels() {
		return IntStream.rangeClosed(1, vesselCount).mapToObj(vesselIdFunction);
	}
	
	public static IdentityGenerator<VesselId> newInstance() {
		return new IdentityGenerator<VesselId>(VesselId::new);
	}

	public int getVesselCount() {
		return vesselCount;
	}

	public void setVesselCount(int vesselCount) {
		this.vesselCount = vesselCount;
	}

	public IntFunction<V> getVesselIdFunction() {
		return vesselIdFunction;
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

	public int getChangeCount() {
		return changeCount;
	}

	public void setChangeCount(int changeCount) {
		this.changeCount = changeCount;
	}

}
