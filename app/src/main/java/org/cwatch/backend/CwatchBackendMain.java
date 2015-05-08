package org.cwatch.backend;

import hu.mapro.mfw.web.MfwWebConfiguration;
import hu.mapro.mfw.web.MfwWebConfigurer;
import hu.mapro.mfw.web.MfwWebSettings;

import java.util.concurrent.ThreadLocalRandom;

import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.cwatch.backend.store.DefaultIdentityStore;
import org.cwatch.backend.store.VesselId;
import org.cwatch.backend.test.IdentityGenerator;
import org.cwatch.backend.test.RealTimeSimulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Configuration
@EnableAutoConfiguration
@Import({
	MfwWebConfiguration.class,
	MemoryStoreConfiguration.class,
	LatestPositionConfiguration.class,
	RouteConfiguration.class
})
public class CwatchBackendMain implements CommandLineRunner {
	
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

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	RealTimeSimulator<VesselId> realTimeSimulator() {
		return new RealTimeSimulator<VesselId>();
	}
	
	@Autowired
	DefaultIdentityStore<VesselId, Integer> mmsiIdentityStore;
	
	@Autowired
	DefaultIdentityStore<VesselId, Integer> imoIdentityStore;
	
	@Autowired
	DefaultIdentityStore<VesselId, String> irIdentityStore;

	@Produce(uri="direct:AIS_POSITION")
	ProducerTemplate aisPosition;

	@Produce(uri="direct:LRIT_POSITION")
	ProducerTemplate lritPosition;

	@Produce(uri="direct:VMS_POSITION")
	ProducerTemplate vmsPosition;

	@Override
	public void run(String... arg0) throws Exception {
		IdentityGenerator<VesselId> idg = IdentityGenerator.newInstance();
		idg.setVesselCount(100);
		
		idg.generate(mmsiIdentityStore, (v, d) -> v.getId() * 1000 + ThreadLocalRandom.current().nextInt(100, 200));
		idg.generate(imoIdentityStore, (v, d) -> v.getId() * 1000 + ThreadLocalRandom.current().nextInt(200, 300));
		idg.generate(irIdentityStore, (v, d) -> Integer.toString(v.getId() * 1000 + ThreadLocalRandom.current().nextInt(300, 400)));
		
		RealTimeSimulator<VesselId> realTimeSimulator = realTimeSimulator();
		realTimeSimulator.setReportingPeriodSeconds(1);
		realTimeSimulator.setReportingPeriodVariationSeconds(0.1);
		realTimeSimulator.simulate( 
			idg.getVessels(), 
			aisPosition::sendBody,
			lritPosition::sendBody,
			vmsPosition::sendBody
		);
			

	}

}
