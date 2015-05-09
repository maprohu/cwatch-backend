package org.cwatch.backend;

import java.util.HashMap;
import java.util.Map;

import javax.media.jai.widget.ViewportListener;
import javax.servlet.http.HttpSession;

import org.cwatch.backend.process.DefaultLatestPositionService;
import org.cwatch.backend.process.EnrichmentService;
import org.cwatch.backend.process.LatestPositionRegistry;
import org.cwatch.backend.process.LatestPositionView;
import org.cwatch.backend.process.LatestPositionView.Viewport;
import org.cwatch.backend.process.ProfileService;
import org.cwatch.backend.store.VesselId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocketMessageBroker
public class LatestPositionConfiguration extends AbstractWebSocketMessageBrokerConfigurer  {

	private static final String SESSION_POSITION_VIEWS_MAP = "positionViewsMap";
	
	
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.setApplicationDestinationPrefixes("/app");
        config.enableSimpleBroker("/queue", "/topic");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
        	.addEndpoint("/web")
        	.addInterceptors(
        			new CreateViewsMapInterceptor(),
        			new HttpSessionHandshakeInterceptor()
			)
        	.withSockJS();
    }

    public class CreateViewsMapInterceptor implements HandshakeInterceptor {


    	@Override
    	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
    			WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

    		HttpSession session = getSession(request);
    		
    		if (session != null) {
        		@SuppressWarnings("unchecked")
    			Map<Integer, LatestPositionView<VesselId>> viewsMap = (Map<Integer, LatestPositionView<VesselId>>) session.getAttribute(SESSION_POSITION_VIEWS_MAP);
        		
        		if (viewsMap==null) {
        			viewsMap = new HashMap<>();
        			session.setAttribute(SESSION_POSITION_VIEWS_MAP, viewsMap);
        		}
    		}
    		return true;
    	}

    	private HttpSession getSession(ServerHttpRequest request) {
    		if (request instanceof ServletServerHttpRequest) {
    			ServletServerHttpRequest serverRequest = (ServletServerHttpRequest) request;
    			return serverRequest.getServletRequest().getSession(true);
    		}
    		return null;
    	}

    	@Override
    	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
    			WebSocketHandler wsHandler, Exception ex) {
    	}

    }
    
    @Bean
    LatestPositionRegistry<VesselId> latestPositionRegistry(
    		EnrichmentService<VesselId> enrichmentService, 
    		ProfileService<VesselId> profileService, 
    		SimpMessagingTemplate messagingTemplate
	) {
    	return new DefaultLatestPositionService<VesselId>(enrichmentService, profileService, messagingTemplate);
    }
    
    @RestController
    @RequestMapping("/service/position")
    public static class LatestPositionController {
    	
    	@Autowired
    	LatestPositionRegistry<VesselId> latestPositionRegistry;
    	
    	/**
    	 * 
    	 * @param profile this is here only for the sake of the prototype. normally
    	 * 	the profile will be the attribute of the logged in user
    	 * @param request
    	 * @return
    	 */
    	@RequestMapping("/start")
    	public synchronized int start(
    			@RequestParam(value="profile", required=false, defaultValue=ProfileService.DEFAULT_PROFILE) String profile,
    			WebRequest request
		) {
    		@SuppressWarnings("unchecked")
			Map<Integer, LatestPositionView<VesselId>> viewsMap = (Map<Integer, LatestPositionView<VesselId>>) request.getAttribute(SESSION_POSITION_VIEWS_MAP, WebRequest.SCOPE_SESSION);
    		
    		if (viewsMap==null) {
    			viewsMap = new HashMap<>();
    			request.setAttribute(SESSION_POSITION_VIEWS_MAP, viewsMap, WebRequest.SCOPE_SESSION);
    		}
    		
    		LatestPositionView<VesselId> view = latestPositionRegistry.register(profile);
    		
    		viewsMap.put(view.getViewId(), view);
    		
    		return view.getViewId();
    	}
    	
    	@MessageMapping("/positions/view")
    	public void positionsView(PositionsView viewData, SimpMessageHeaderAccessor headers) {
    		System.out.println("boo");
    		
			@SuppressWarnings("unchecked")
			Map<Integer, LatestPositionView<VesselId>> viewsMap = (Map<Integer, LatestPositionView<VesselId>>) headers.getSessionAttributes().get(SESSION_POSITION_VIEWS_MAP);
    		
    		LatestPositionView<VesselId> view = viewsMap.get(viewData.viewId);
    		
    		view.setVieport(viewData);
    	}
    	
    	public static class PositionsView implements Viewport {
    		public int viewId;
    		public double centerLatitude;
    		public double centerLongitude;
    		public double zoom;
    		public double rotation;
    		public String projection;
    		public int width;
    		public int height;
			@Override
			public double getCenterLatitude() {
				return centerLatitude;
			}
			@Override
			public double getCenterLongitude() {
				return centerLongitude;
			}
			@Override
			public double getZoom() {
				return zoom;
			}
			@Override
			public double getRotation() {
				return rotation;
			}
			@Override
			public String getProjection() {
				return projection;
			}
			@Override
			public int getWidth() {
				return width;
			}
			@Override
			public int getHeight() {
				return height;
			}
    	}

    	
    }
    
}
