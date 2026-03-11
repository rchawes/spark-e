package com.electrician.spark_e.controller;

import com.electrician.spark_e.model.ComplianceChecklist;
import com.electrician.spark_e.model.ChecklistItem;
import com.electrician.spark_e.repository.ComplianceChecklistRepository;
import com.electrician.spark_e.repository.JobTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/checklists")
public class ComplianceChecklistController {

    @Autowired
    private ComplianceChecklistRepository checklistRepository;

    @Autowired
    private JobTypeRepository jobTypeRepository;

    @GetMapping
    public List<ComplianceChecklist> getAllChecklists() {
        return checklistRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComplianceChecklist> getChecklistById(@PathVariable Long id) {
        return checklistRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createChecklist(@RequestBody ComplianceChecklist checklist) {
        // Ensure job type exists
        if (checklist.getJobType() != null && checklist.getJobType().getId() != null) {
            jobTypeRepository.findById(checklist.getJobType().getId())
                    .orElseThrow(() -> new RuntimeException("Job type not found"));
        }
        return ResponseEntity.ok(checklistRepository.save(checklist));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ComplianceChecklist> updateChecklist(@PathVariable Long id, @RequestBody ComplianceChecklist checklistDetails) {
        return checklistRepository.findById(id)
                .map(checklist -> {
                    checklist.setName(checklistDetails.getName());
                    checklist.setRegulationReference(checklistDetails.getRegulationReference());
                    if (checklistDetails.getJobType() != null && checklistDetails.getJobType().getId() != null) {
                        jobTypeRepository.findById(checklistDetails.getJobType().getId())
                                .ifPresent(checklist::setJobType);
                    }
                    return checklistRepository.save(checklist);
                })
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChecklist(@PathVariable Long id) {
        if (checklistRepository.existsById(id)) {
            checklistRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Endpoints for managing checklist items
    @PostMapping("/{checklistId}/items")
    public ResponseEntity<?> addItemToChecklist(@PathVariable Long checklistId, @RequestBody ChecklistItem item) {
        return checklistRepository.findById(checklistId)
                .map(checklist -> {
                    item.setChecklist(checklist);
                    checklist.getItems().add(item);
                    checklistRepository.save(checklist);
                    return ResponseEntity.ok(item);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> deleteChecklistItem(@PathVariable Long itemId) {
        return ResponseEntity.status(501).build();
    }
}
