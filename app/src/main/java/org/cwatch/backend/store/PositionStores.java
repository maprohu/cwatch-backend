package org.cwatch.backend.store;

import org.cwatch.backend.message.AisPosition;
import org.cwatch.backend.message.LritPosition;
import org.cwatch.backend.message.VmsPosition;

public interface PositionStores {
	
	DefaultPositionStore<Integer, AisPosition> mmsi();
	DefaultPositionStore<Integer, LritPosition> lrit();
	DefaultPositionStore<String, VmsPosition> vms();
	

}
