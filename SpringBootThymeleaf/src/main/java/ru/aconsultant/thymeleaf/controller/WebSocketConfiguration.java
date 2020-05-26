package ru.aconsultant.thymeleaf.controller;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;



/*@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/portfolio").withSockJS();
	}

	@Override
       public void configureMessageBroker(MessageBrokerRegistry config) {
           config.setApplicationDestinationPrefixes("/app");
           config.enableSimpleBroker("/topic", "/queue");
       }

}*/


@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer  {
	
	public void configureMessageBroker(MessageBrokerRegistry confi) {
		confi.enableSimpleBroker("/chat", "/topic", "/queue", "/user");
		confi.setApplicationDestinationPrefixes("/app");
		confi.setUserDestinationPrefix("/user"); 
	}

	public void registerStompEndpoints(StompEndpointRegistry registry) {
		
		registry
		.addEndpoint("/chat-messaging")
		.setHandshakeHandler(new DefaultHandshakeHandler() {
		
			public boolean beforeHandshake(
				ServerHttpRequest request, 
				ServerHttpResponse response, 
				WebSocketHandler wsHandler,
				Map attributes) throws Exception {
		
					if (request instanceof ServletServerHttpRequest) {
							ServletServerHttpRequest servletRequest	= (ServletServerHttpRequest) request;
							HttpSession session = servletRequest.getServletRequest().getSession();
							attributes.put("sessionId", session.getId());
					}
					return true;
			}})
		
		.withSockJS();
		
	}

}