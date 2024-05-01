package com.example.boardsserver;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.common.BoardsData;
import org.common.Board;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.Base64;

@RestController
@RequestMapping("/user/boards")
public class UserBoardsDataController {
    private static final String BOARDS_FILE_PATH = "./data/boards.ser"; // Путь к файлу с данными досок
    private File boardsDataFile = new File(BOARDS_FILE_PATH);

    @PostMapping("/receive-boards-data")
    public ResponseEntity<String> receiveBoardsData(@RequestBody BoardsData boardsData) {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(BOARDS_FILE_PATH))) {
            // Записываем объект boardsData в файл
            outputStream.writeObject(boardsData);
            System.out.println("Received boards data saved to file: " + BOARDS_FILE_PATH);
            return new ResponseEntity<>("Boards data received and saved successfully.", HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error occurred while saving boards data.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-boards-data")
    public ResponseEntity<String> getBoardsDataFromFile() {
        if (!boardsDataFile.exists()) {
            System.out.println("Boards file does not exist.");
            return new ResponseEntity<>("Boards file does not exist.", HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(BOARDS_FILE_PATH))) {
                BoardsData boardsData = (BoardsData) inputStream.readObject();
                String serializedData = serializeBoardsDataToString(boardsData);
                System.out.println("boardsData is sent");
                return ResponseEntity.ok(serializedData);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                return new ResponseEntity<>("Error occurred while reading boards data from file.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    private String serializeBoardsDataToString(BoardsData boardsData) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(boardsData);
            objectOutputStream.flush();
        }
        return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
    }


}
