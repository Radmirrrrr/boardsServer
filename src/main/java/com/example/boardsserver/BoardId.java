package com.example.boardsserver;

public class BoardId {

    private static int boardId;

    public static int getBoardId() {
        return boardId;
    }

    public static void setBoardId(int boardId) {
        BoardId.boardId = boardId;
    }
}
