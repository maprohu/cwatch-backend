package org.cwatch.backend.store;

import java.util.Date;

import com.google.common.collect.Range;

public interface IdentityPeriod<I> {

	I getId();

	Range<Date> getPeriod();
	
}
