package com.brightology.signupform;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
@RequestMapping("/driver")
public class DriverSignupController {

    @Autowired
    private PdfValidationService pdfValidationService;

    private final String UPLOAD_DIR = "uploads/drivers-licenses/";

    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("driverSignup", new DriverSignupDto());
        return "driver-signup";
    }

    @PostMapping("/signup")
    public String processSignup(@Valid @ModelAttribute("driverSignup") DriverSignupDto driverSignup,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {

        // Check password confirmation
        if (!driverSignup.isPasswordMatching()) {
            bindingResult.rejectValue("confirmPassword", "password.mismatch",
                    "Passwords do not match");
        }

        // Validate PDF file
        String pdfValidationError = pdfValidationService.validateDriversLicense(driverSignup.getDriversLicense());
        if (pdfValidationError != null) {
            bindingResult.rejectValue("driversLicense", "file.invalid", pdfValidationError);
        }

        if (bindingResult.hasErrors()) {
            return "driver-signup";
        }

        try {
            // Save the uploaded file
            saveDriversLicense(driverSignup);

            // Here you would typically save the driver information to a database
            // For this example, we'll just show a success message

            redirectAttributes.addFlashAttribute("successMessage",
                    "Driver registration successful! Welcome, " + driverSignup.getName());
            return "redirect:/driver/success";

        } catch (IOException e) {
            model.addAttribute("errorMessage", "Failed to upload driver's license. Please try again.");
            return "driver-signup";
        }
    }

    @GetMapping("/success")
    public String showSuccessPage() {
        return "signup-success";
    }

    private void saveDriversLicense(DriverSignupDto driverSignup) throws IOException {
        if (driverSignup.getDriversLicense() != null && !driverSignup.getDriversLicense().isEmpty()) {
            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate unique filename
            String filename = System.currentTimeMillis() + "_" +
                    driverSignup.getName().replaceAll("[^a-zA-Z0-9]", "_") +
                    "_license.pdf";

            Path filePath = uploadPath.resolve(filename);

            // Save file
            Files.copy(driverSignup.getDriversLicense().getInputStream(),
                    filePath, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}