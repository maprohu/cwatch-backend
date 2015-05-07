package org.cwatch.backend;

import hu.mapro.mfw.web.MfwWebConfiguration;
import hu.mapro.mfw.web.MfwWebConfigurer;
import hu.mapro.mfw.web.MfwWebSettings;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

@Configuration
@EnableAutoConfiguration
@Import({
	MfwWebConfiguration.class,
	MemoryStoreConfiguration.class,
	LatestPositionConfiguration.class,
})
public class CwatchBackendMain {
	
	public static void main(String[] args) {
		SpringApplication.run(CwatchBackendMain.class, args);
	}
	
	@Component
	public static class Configurer implements MfwWebConfigurer {

		@Override
		public void configure(MfwWebSettings settings) {
			settings.addCss("libs/openlayers/ol.css");
			settings.addCss("js/modules/angular-openlayers-directive.css");
			settings.addCss("css/app.css");

		}
		
	}

}
