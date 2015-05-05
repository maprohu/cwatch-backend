package org.cwatch.backend.route;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;

public enum Direct {
	
	AIS_POSITION,
	LRIT_POSITION,
	VMS_POSITION,
	
	;
	
	public RouteDefinition from(RouteBuilder routeBuilder) {
		return routeBuilder.from("direct:"+name()).id(name());
	}

}
