package org.cwatch.backend.message;

public interface PositionVisitable {

	void accept(PositionVisitor visitor);
	
}
