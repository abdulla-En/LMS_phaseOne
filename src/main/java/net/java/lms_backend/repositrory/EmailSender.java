package net.java.lms_backend.repositrory;

public interface EmailSender {
    void send(String to, String email);
}