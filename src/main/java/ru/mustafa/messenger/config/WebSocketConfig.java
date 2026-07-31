package ru.mustafa.messenger.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Prefix /queue is used for personal messages
        config.enableSimpleBroker("/queue");

        // Prefix for personal addresses (Spring will insert it automatically)
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // The entry point where the frontend will knock for the initial connection
        registry.addEndpoint("/ws-endpoint")
                .setAllowedOriginPatterns("*"); // Allowing access from all domains (CORS)
    }
}