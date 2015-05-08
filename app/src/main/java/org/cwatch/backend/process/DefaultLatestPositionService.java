package org.cwatch.backend.process;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import org.cwatch.backend.message.AisPosition;
import org.cwatch.backend.message.DefaultPosition;
import org.cwatch.backend.message.LritPosition;
import org.cwatch.backend.message.VmsPosition;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class DefaultLatestPositionService<V> implements LatestPositionProcessor, LatestPositionRegistry<V> {

	private final AtomicInteger viewIdSequence = new AtomicInteger();
	
	private final EnrichmentService<V> enrichmentService;
	
	private final ProfileService<V> profileService;
	
	final private SimpMessagingTemplate messagingTemplate;	
	
	public DefaultLatestPositionService(
			EnrichmentService<V> enrichmentService,
			ProfileService<V> profileService,
			SimpMessagingTemplate messagingTemplate) {
		super();
		this.enrichmentService = enrichmentService;
		this.profileService = profileService;
		this.messagingTemplate = messagingTemplate;
	}

	final LoadingCache<String, LatestPositionProfile<V>> profileCache = CacheBuilder.newBuilder().weakValues().build(CacheLoader.from(
   			name -> new MemoryLatestPositionProfile<V>(profileService.getProfile(name).getPositionFilter())
   	));
   	
	@Override
	public void processDefault(DefaultPosition position) {
		// TODO Auto-generated method stub
	}

	private <E extends EnrichedPosition<V, ? extends DefaultPosition>> void forEachProfile(
			Supplier<E> enrichedSupplier, 
			BiConsumer<E, LatestPositionProfile<V>> action
	) {
		Collection<LatestPositionProfile<V>> profiles = profileCache.asMap().values();
		if (!profiles.isEmpty()) {
			E enriched = enrichedSupplier.get();
			profiles.stream().parallel().forEach(p -> action.accept(enriched, p));
		}
	}

	@Override
	public void processAis(AisPosition position) {
		forEachProfile(
				() -> enrichmentService.enrichAis(position),
				(e, p) -> p.processAis(e)
		);
	}

	@Override
	public void processLrit(LritPosition position) {
		forEachProfile(
				() -> enrichmentService.enrichLrit(position),
				(e, p) -> p.processLrit(e)
		);
	}

	@Override
	public void processVms(VmsPosition position) {
		forEachProfile(
				() -> enrichmentService.enrichVms(position),
				(e, p) -> p.processVms(e)
		);
	}

	@Override
	public LatestPositionView<V> register(String profileName) {
		return new DefaultLatestPositionView<V>(
				viewIdSequence.getAndIncrement(), 
				messagingTemplate, 
				profileCache.getUnchecked(profileName)
		);
	}

}
