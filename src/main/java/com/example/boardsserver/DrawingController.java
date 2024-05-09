package com.example.boardsserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.common.AddPointMessage;
import org.common.BoardsData;
import org.common.Line;
import org.common.SendingLineStart;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.io.*;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class DrawingController {

    private Map<String, Line> linesMap = new HashMap<>();
    private String LINES_FILE_PATH;
    private File linesDataFile;

    @MessageMapping("/draw")
    @SendTo("/topic/draw")
    public void handleDrawingMessage(SendingLineStart sendingLineStart) {
        Line line = new Line(sendingLineStart.getLineId());
        line.addPoint(sendingLineStart.getStartX(), sendingLineStart.getStartY());
        line.setLineWidth(sendingLineStart.getLineWidth());
        setLineColor(line, sendingLineStart);
        linesMap.put(line.getId(), line);
        List<Double> receivedPoint = line.getPoints();
        System.out.println(receivedPoint);
    }

    public void setLineColor(Line line, SendingLineStart sendingLineStart) {
        line.setRed(sendingLineStart.getRed());
        line.setGreen(sendingLineStart.getGreen());
        line.setBlue(sendingLineStart.getBlue());
    }

    @MessageMapping("/addPoint")
    @SendTo("/topic/addPoint")
    public void handleAddPointMessage(AddPointMessage addPointMessage) throws IOException {
        String lineId = addPointMessage.getLineId();
        Double x = addPointMessage.getX();
        Double y = addPointMessage.getY();
        if (linesMap.containsKey(lineId)) {
            linesMap.get(lineId).addPoint(x, y);
            System.out.println(linesMap.get(lineId).getPoints());
        }
    }

    @MessageMapping("/saveLines")
    public void saveLinesData2File() {
        int boardId = BoardId.getBoardId();

        LINES_FILE_PATH = "./data/board_" + boardId + ".ser";
        if (linesDataFile == null) {
            linesDataFile = new File(LINES_FILE_PATH);
        }

        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(linesDataFile))) {
            outputStream.writeObject(linesMap);
            System.out.println("Lines data saved to file: " + linesDataFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Failed to save lines data to file: " + e.getMessage());
        }
    }

    @MessageMapping("/loadLines")
    @SendTo("/topic/loadLines")
    public Map<String, Line> loadLinesFromFile() {
        System.out.println("Request received, sending lines: " + linesMap);
        return linesMap;
    }
}