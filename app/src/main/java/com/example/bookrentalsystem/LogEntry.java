package com.example.bookrentalsystem;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "log_entries")
public class LogEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String operationType;
    private String timestamp;
    private String message;

    public LogEntry(String operationType, String timestamp, String message) {
        this.operationType = operationType;
        this.timestamp = timestamp;
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }
}