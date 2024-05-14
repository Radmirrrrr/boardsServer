package com.example.boardsserver;

import org.common.AddPointMessage;
import org.common.BoardsData;
import org.common.Line;
import org.common.SendingLineStart;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class DrawingController {

    private Map<Integer, Map<String, Line>> allLinesMap = new HashMap<>();

    @MessageMapping("/draw")
    @SendTo("/topic/draw")
    public SendingLineStart handleDrawingMessage(SendingLineStart sendingLineStart) {
        int boardId = sendingLineStart.getBoardId();
        Map<String, Line> linesMap = allLinesMap.computeIfAbsent(boardId, k -> new HashMap<>());

        Line line = new Line(sendingLineStart.getLineId());
        line.addPoint(sendingLineStart.getStartX(), sendingLineStart.getStartY());
        line.setLineWidth(sendingLineStart.getLineWidth());
        setLineColor(line, sendingLineStart);
        linesMap.put(line.getId(), line);

        List<Double> receivedPoint = line.getPoints();
        System.out.println(receivedPoint);

        return sendingLineStart;
    }

    public void setLineColor(Line line, SendingLineStart sendingLineStart) {
        line.setRed(sendingLineStart.getRed());
        line.setGreen(sendingLineStart.getGreen());
        line.setBlue(sendingLineStart.getBlue());
        line.setOpacity(sendingLineStart.getOpacity());
    }

    @MessageMapping("/addPoint")
    @SendTo("/topic/addPoint")
    public AddPointMessage handleAddPointMessage(AddPointMessage addPointMessage) {
        int boardId = addPointMessage.getBoardId();
        Map<String, Line> linesMap = allLinesMap.get(boardId);

        if (linesMap != null) {
            String lineId = addPointMessage.getLineId();
            Double x = addPointMessage.getX();
            Double y = addPointMessage.getY();

            if (linesMap.containsKey(lineId)) {
                linesMap.get(lineId).addPoint(x, y);
                System.out.println(linesMap.get(lineId).getPoints());
            }
        }
        return addPointMessage;
    }

    @MessageMapping("/saveLines")
    public void saveLinesData2File(int boardId) {
        String LINES_FILE_PATH = "./data/board_" + boardId + ".ser";
        File linesDataFile = new File(LINES_FILE_PATH);
        Map<String, Line> linesMap = allLinesMap.get(boardId);

        if (linesMap != null) {
            try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(linesDataFile))) {
                outputStream.writeObject(linesMap);
                System.out.println("Lines data saved to file: " + linesDataFile.getAbsolutePath());
            } catch (IOException e) {
                System.err.println("Failed to save lines data to file: " + e.getMessage());
            }
        }
    }

    @MessageMapping("/loadLines")
    @SendToUser("/queue/loadLines")
    public Map<String, Line> loadLinesFromFile(int boardId) {
        String LINES_FILE_PATH = "./data/board_" + boardId + ".ser";
        File linesDataFile = new File(LINES_FILE_PATH);

        if (linesDataFile.exists()) {
            try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(linesDataFile))) {
                Map<String, Line> linesMap = (Map<String, Line>) inputStream.readObject();
                allLinesMap.put(boardId, linesMap);
                System.out.println("Request received, sending lines: " + linesMap);
                return linesMap;
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Failed to load lines data from file: " + e.getMessage());
            }
        }
        return null;
    }
}
