package com.brightology.signupform;



import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class PdfValidationService {

    public boolean isPdfFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        String contentType = file.getContentType();
        String filename = file.getOriginalFilename();

        // Check content type
        if (!"application/pdf".equals(contentType)) {
            return false;
        }

        // Check file extension
        if (filename == null || !filename.toLowerCase().endsWith(".pdf")) {
            return false;
        }

        // Validate PDF structure by trying to open it
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            return document.getNumberOfPages() > 0;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean hasDigitalSignature(MultipartFile file) {
        if (!isPdfFile(file)) {
            return false;
        }

        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            List<PDSignature> signatures = document.getSignatureDictionaries();
            return signatures != null && !signatures.isEmpty();
        } catch (IOException e) {
            return false;
        }
    }

    public String validateDriversLicense(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return "Driver's license file is required";
        }

        if (!isPdfFile(file)) {
            return "File must be a valid PDF";
        }

        if (!hasDigitalSignature(file)) {
            return "PDF must contain a digital signature";
        }

        return null; // No validation errors
    }
}
