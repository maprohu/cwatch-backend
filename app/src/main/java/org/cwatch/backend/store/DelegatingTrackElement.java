package org.cwatch.backend.store;

import java.util.Date;

import org.cwatch.backend.message.DefaultPosition;

public class DelegatingTrackElement<P extends DefaultPosition> implements TrackElement {
	
	public DelegatingTrackElement(P delegate) {
		super();
		this.delegate = delegate;
	}

	final P delegate;

	@Override
	public Date getTimestamp() {
		return delegate.getTimestamp();
	}

	@Override
	public double getLatitude() {
		return delegate.getLatitude();
	}

	@Override
	public double getLongitude() {
		return delegate.getLongitude();
	}

	@Override
	public double getHeading() {
		return delegate.getHeading();
	}

}
