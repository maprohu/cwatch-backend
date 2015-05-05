package org.cwatch.backend.store;

import java.util.Date;

public interface IdentityPeriod<I> {

	I getId();

	Date getFrom();
	
	Date getTo();
	
}
