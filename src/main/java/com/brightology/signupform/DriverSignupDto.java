package com.brightology.signupform;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public class DriverSignupDto {

    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 21, message = "Name must be between 3 and 21 characters")
    @Pattern(regexp = "^[A-Za-z\\s\\-']+$", message = "Name can only contain letters, spaces, hyphens, and apostrophes")
    private String name;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Password confirmation is required")
    private String confirmPassword;

    private MultipartFile driversLicense;

    // Constructors
    public DriverSignupDto() {}

    public DriverSignupDto(String name, String password, String confirmPassword, MultipartFile driversLicense) {
        this.name = name;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.driversLicense = driversLicense;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public MultipartFile getDriversLicense() {
        return driversLicense;
    }

    public void setDriversLicense(MultipartFile driversLicense) {
        this.driversLicense = driversLicense;
    }

    // Custom validation method for password confirmation
    public boolean isPasswordMatching() {
        return password != null && password.equals(confirmPassword);
    }
}

