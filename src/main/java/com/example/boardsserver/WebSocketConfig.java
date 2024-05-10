package com.example.boardsserver;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/websocket").setAllowedOrigins("*")
                .addInterceptors(new CustomHandshakeInterceptor());
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Bean
    public SimpMessagingTemplate messagingTemplate(@Qualifier("clientOutboundChannel") MessageChannel messageChannel) {
        return new SimpMessagingTemplate(messageChannel);
    }

    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        // Добавляем кастомные конвертеры
        messageConverters.add(new StringMessageConverter());
        messageConverters.add(new MappingJackson2MessageConverter());

        // Возвращаем true для добавления стандартных конвертеров после кастомных
        return true;
    }
}
