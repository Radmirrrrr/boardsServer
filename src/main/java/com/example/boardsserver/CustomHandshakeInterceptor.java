package com.example.boardsserver;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

public class CustomHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        // Извлечение заголовка 'boardId' и преобразование его в int
        String boardIdStr = request.getHeaders().getFirst("boardId");
        if (boardIdStr != null) {
            try {
                int boardId = Integer.parseInt(boardIdStr);
                BoardId.setBoardId(boardId);
            } catch (NumberFormatException e) {
                System.err.println("Invalid format for boardId, must be an integer: " + boardIdStr);
                return false; // Возвращаем false, если не удалось преобразовать boardId в int
            }
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}