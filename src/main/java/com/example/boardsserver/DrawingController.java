package com.example.boardsserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.common.AddPointMessage;
import org.common.Line;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class DrawingController {

    private Map<String, Line> linesMap = new HashMap<>();

    private ObjectMapper objectMapper = new ObjectMapper();

    @MessageMapping("/draw")
    @SendTo("/topic/draw")
    public void handleDrawingMessage(byte[] messageBytes) {
        try {
            // Десериализация массива байтов в объект Line
            Line receivedLine = objectMapper.readValue(messageBytes, Line.class);
            linesMap.put(receivedLine.getId(), receivedLine);
        } catch (IOException e) {
            // Обработка ошибок десериализации
            e.printStackTrace();
        }
    }

    @MessageMapping("/addPoint")
    @SendTo("/topic/line")
    public void handleAddPointMessage(byte[] messageBytes) throws IOException {
        try {
            AddPointMessage addPointMessage = objectMapper.readValue(messageBytes, AddPointMessage.class);
            String lineId = addPointMessage.getLineId();
            Double x = addPointMessage.getX();
            Double y = addPointMessage.getY();
            if (linesMap.containsKey(lineId)) {
                linesMap.get(lineId).addPoint(x, y);
                System.out.println(linesMap.get(lineId).getPoints());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}