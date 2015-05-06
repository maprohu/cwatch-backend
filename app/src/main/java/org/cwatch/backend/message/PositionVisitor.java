package org.cwatch.backend.message;

public interface PositionVisitor {
	
	void visit(AisPosition position);
	void visit(LritPosition position);
	void visit(VmsPosition position);

}
