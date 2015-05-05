package org.cwatch.backend.store;

public class VesselId {
	
	final int id;

	public VesselId(int id) {
		super();
		this.id = id;
	}

	public int getId() {
		return id;
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VesselId other = (VesselId) obj;
		if (id != other.id)
			return false;
		return true;
	}

}
