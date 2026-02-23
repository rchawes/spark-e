package com.electrician.spark_e.controller;

import com.electrician.spark_e.model.JobType;
import com.electrician.spark_e.repository.JobTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/job-types")
public class JobTypeController {

    @Autowired
    private JobTypeRepository jobTypeRepository;

    @GetMapping
    public List<JobType> getAllJobTypes() {
        return jobTypeRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobType> getJobTypeById(@PathVariable Long id) {
        return jobTypeRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public JobType createJobType(@RequestBody JobType jobType) {
        return jobTypeRepository.save(jobType);
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobType> updateJobType(@PathVariable Long id, @RequestBody JobType jobTypeDetails) {
        return jobTypeRepository.findById(id)
                .map(jobType -> {
                    jobType.setName(jobTypeDetails.getName());
                    jobType.setDescription(jobTypeDetails.getDescription());
                    return jobTypeRepository.save(jobType);
                })
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJobType(@PathVariable Long id) {
        if (jobTypeRepository.existsById(id)) {
            jobTypeRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}