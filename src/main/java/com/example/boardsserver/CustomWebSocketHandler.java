package com.example.boardsserver;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.util.ArrayList;
import java.util.List;

@Component
public class CustomWebSocketHandler implements WebSocketHandler {

    private final List<WebSocketSession> sessions = new ArrayList<>();


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        System.out.println("Client is connected");
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        String receivedMessage = (String) message.getPayload();
        System.out.println("Received message from client: " + receivedMessage);
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
