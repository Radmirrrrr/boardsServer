package com.example.boardsserver;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.ArrayList;
import java.util.List;

@Component
public class CustomWebSocketHandler extends TextWebSocketHandler {
    private final SimpMessagingTemplate messagingTemplate;
    private final List<WebSocketSession> sessions = new ArrayList<>();

    public CustomWebSocketHandler(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        System.out.println("Client is connected");
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String receivedMessage = message.getPayload();
        System.out.println("Received message from client: " + receivedMessage);

        // Отправляем обратно клиенту сообщение о успешном подключении
        messagingTemplate.convertAndSend("/topic/greetings", "привет от сервера");
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.err.println("Transport error on session: " + session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        sessions.remove(session);
        System.out.println("Connection is closed");
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
