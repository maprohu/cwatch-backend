package org.cwatch.backend.store;

import java.util.Date;

import com.google.common.collect.Range;

public class DefaultIdentityPeriod<I> implements IdentityPeriod<I> {
	
	final I id;
	
	final Range<Date> period;
	
	public DefaultIdentityPeriod(I id, Range<Date> period) {
		super();
		this.id = id;
		this.period = period;
	}

	@Override
	public I getId() {
		return id;
	}

	@Override
	public Range<Date> getPeriod() {
		return period;
	}

}
