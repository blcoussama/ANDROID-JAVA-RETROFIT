package com.cabinet.cabinetmedical.model.response;

import java.util.List;

public class ErrorResponse {

    private String timestamp;
    private int status;
    private String error;
    private String message;      // ← MESSAGE DEPUIS LE BACKEND
    private String path;
    private List<String> errors;  // Pour validation multiple

    // Constructeur vide (requis par Gson)
    public ErrorResponse() {}

    // Getters
    public String getTimestamp() { return timestamp; }
    public int getStatus() { return status; }
    public String getError() { return error; }
    public String getMessage() { return message; }
    public String getPath() { return path; }
    public List<String> getErrors() { return errors; }

    // Setters
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public void setStatus(int status) { this.status = status; }
    public void setError(String error) { this.error = error; }
    public void setMessage(String message) { this.message = message; }
    public void setPath(String path) { this.path = path; }
    public void setErrors(List<String> errors) { this.errors = errors; }
}
