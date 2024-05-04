package com.example.boardsserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.common.Line;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

import java.io.IOException;

@Controller
public class DrawingController {

    private ObjectMapper objectMapper = new ObjectMapper();

    @MessageMapping("/draw")
    @SendTo("/topic/draw")
    public void handleDrawingMessage(byte[] messageBytes) {
        try {
            // Десериализация массива байтов в объект Line
            Line line = objectMapper.readValue(messageBytes, Line.class);
            // Обработка объекта Line
            System.out.println(line.getLineWidth());
        } catch (IOException e) {
            // Обработка ошибок десериализации
            e.printStackTrace();
        }
    }
}