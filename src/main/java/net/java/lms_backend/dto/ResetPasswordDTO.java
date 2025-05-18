package net.java.lms_backend.dto;

public class ResetPasswordDTO {
    private String email;

    public ResetPasswordDTO() {}

    public ResetPasswordDTO(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;

    }
}