package com.electrician.spark_e.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {
    
    @Value("${app.upload.dir:uploads}")
    private String uploadDir;
    
    @Value("${app.upload.max-file-size:10485760}") // 10MB default
    private long maxFileSize;
    
    private Path rootLocation;
    
    public FileStorageService() {
        this.rootLocation = Paths.get("uploads").toAbsolutePath().normalize();
    }
    
    public void init() throws IOException {
        if (!Files.exists(rootLocation)) {
            Files.createDirectories(rootLocation);
        }
    }
    
    public String storeFile(MultipartFile file, String subDirectory) throws IOException {
        // Validate file
        if (file.isEmpty()) {
            throw new RuntimeException("Failed to store empty file.");
        }
        
        if (file.getSize() > maxFileSize) {
            throw new RuntimeException("File size exceeds maximum allowed size.");
        }
        
        // Validate file type (only images)
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("Only image files are allowed.");
        }
        
        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
        
        // Create subdirectory if it doesn't exist
        Path targetLocation = rootLocation.resolve(subDirectory);
        if (!Files.exists(targetLocation)) {
            Files.createDirectories(targetLocation);
        }
        
        // Store file
        Path targetPath = targetLocation.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        
        return subDirectory + "/" + uniqueFilename;
    }
    
    public Path loadFile(String filePath) {
        return rootLocation.resolve(filePath);
    }
    
    public boolean deleteFile(String filePath) {
        try {
            Path targetPath = rootLocation.resolve(filePath);
            return Files.deleteIfExists(targetPath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file: " + filePath, e);
        }
    }
    
    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf('.') == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.'));
    }
    
    public boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }
    
    public long getMaxFileSize() {
        return maxFileSize;
    }
}
