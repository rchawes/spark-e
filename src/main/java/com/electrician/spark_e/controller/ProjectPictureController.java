package com.electrician.spark_e.controller;

import com.electrician.spark_e.model.Job;
import com.electrician.spark_e.model.ProjectPicture;
import com.electrician.spark_e.repository.JobRepository;
import com.electrician.spark_e.repository.ProjectPictureRepository;
import com.electrician.spark_e.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/project-pictures")
public class ProjectPictureController {

    @Autowired
    private ProjectPictureRepository projectPictureRepository;
    
    @Autowired
    private JobRepository jobRepository;
    
    @Autowired
    private FileStorageService fileStorageService;

    // GET all pictures for a job
    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<ProjectPicture>> getPicturesByJobId(@NonNull @PathVariable Long jobId) {
        return ResponseEntity.ok(projectPictureRepository.findByJobIdOrderByUploadedAtDesc(jobId));
    }

    // GET pictures for a job by category
    @GetMapping("/job/{jobId}/category/{category}")
    public ResponseEntity<List<ProjectPicture>> getPicturesByJobIdAndCategory(
            @NonNull @PathVariable Long jobId, 
            @NonNull @PathVariable String category) {
        return ResponseEntity.ok(projectPictureRepository.findByJobIdAndCategoryOrderByUploadedAtDesc(jobId, category));
    }

    // GET picture by ID
    @GetMapping("/{id}")
    public ResponseEntity<ProjectPicture> getPictureById(@NonNull @PathVariable Long id) {
        return projectPictureRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET picture file
    @GetMapping("/file/{fileName}")
    public ResponseEntity<Resource> getPictureFile(@NonNull @PathVariable String fileName) {
        try {
            Path filePath = fileStorageService.loadFile(fileName);
            Resource resource = new UrlResource(filePath.toUri());
            
            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }
            
            // Try to determine content type
            String contentType = null;
            try {
                contentType = java.nio.file.Files.probeContentType(filePath);
            } catch (IOException e) {
                // Default to image if cannot determine
                contentType = "image/jpeg";
            }
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // POST upload picture
    @PostMapping("/upload")
    public ResponseEntity<?> uploadPicture(
            @RequestParam("file") MultipartFile file,
            @RequestParam("jobId") Long jobId,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "category", defaultValue = "GENERAL") String category,
            @RequestParam(value = "uploadedBy", required = false) String uploadedBy) {
        
        try {
            // Validate job exists
            Job job = jobRepository.findById(jobId)
                    .orElseThrow(() -> new RuntimeException("Job not found with ID: " + jobId));
            
            // Validate file
            if (!fileStorageService.isImageFile(file)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Only image files are allowed"));
            }
            
            // Store file
            String filePath = fileStorageService.storeFile(file, "job-" + jobId);
            
            // Create and save picture record
            ProjectPicture picture = new ProjectPicture();
            picture.setJob(job);
            picture.setFileName(filePath.substring(filePath.lastIndexOf('/') + 1));
            picture.setOriginalFileName(file.getOriginalFilename());
            picture.setFilePath(filePath);
            picture.setContentType(file.getContentType());
            picture.setFileSize(file.getSize());
            picture.setDescription(description);
            picture.setCategory(category);
            picture.setUploadedBy(uploadedBy);
            
            ProjectPicture savedPicture = projectPictureRepository.save(picture);
            
            return ResponseEntity.ok(savedPicture);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to upload picture: " + e.getMessage()));
        }
    }

    // PUT update picture metadata
    @PutMapping("/{id}")
    public ResponseEntity<ProjectPicture> updatePicture(
            @NonNull @PathVariable Long id, 
            @RequestBody ProjectPicture pictureDetails) {
        return projectPictureRepository.findById(id)
                .map(picture -> {
                    picture.setDescription(pictureDetails.getDescription());
                    picture.setCategory(pictureDetails.getCategory());
                    return ResponseEntity.ok(projectPictureRepository.save(picture));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE picture
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePicture(@NonNull @PathVariable Long id) {
        return projectPictureRepository.findById(id)
                .map(picture -> {
                    try {
                        // Delete file from storage
                        fileStorageService.deleteFile(picture.getFilePath());
                        
                        // Delete record from database
                        projectPictureRepository.deleteById(id);
                        
                        return ResponseEntity.noContent().build();
                    } catch (Exception e) {
                        return ResponseEntity.badRequest().build();
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // GET picture statistics for a job
    @GetMapping("/job/{jobId}/stats")
    public ResponseEntity<Map<String, Object>> getPictureStats(@NonNull @PathVariable Long jobId) {
        Long totalCount = projectPictureRepository.countByJobId(jobId);
        Long beforeCount = projectPictureRepository.countByJobIdAndCategory(jobId, "BEFORE");
        Long afterCount = projectPictureRepository.countByJobIdAndCategory(jobId, "AFTER");
        Long progressCount = projectPictureRepository.countByJobIdAndCategory(jobId, "PROGRESS");
        Long invoiceCount = projectPictureRepository.countByJobIdAndCategory(jobId, "INVOICE");
        
        Map<String, Object> stats = Map.of(
            "totalCount", totalCount,
            "beforeCount", beforeCount,
            "afterCount", afterCount,
            "progressCount", progressCount,
            "invoiceCount", invoiceCount
        );
        
        return ResponseEntity.ok(stats);
    }
}
