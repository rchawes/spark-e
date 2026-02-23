package com.electrician.spark_e.controller;

import com.electrician.spark_e.model.Part;
import com.electrician.spark_e.repository.PartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parts")
public class PartController {

    @Autowired
    private PartRepository partRepository;

    @GetMapping
    public List<Part> getAllParts() {
        return partRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Part> getPartById(@PathVariable Long id) {
        return partRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Part createPart(@RequestBody Part part) {
        return partRepository.save(part);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Part> updatePart(@PathVariable Long id, @RequestBody Part partDetails) {
        return partRepository.findById(id)
                .map(part -> {
                    part.setName(partDetails.getName());
                    part.setStockQuantity(partDetails.getStockQuantity());
                    part.setReorderThreshold(partDetails.getReorderThreshold());
                    part.setUnitPrice(partDetails.getUnitPrice());
                    return partRepository.save(part);
                })
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePart(@PathVariable Long id) {
        if (partRepository.existsById(id)) {
            partRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Custom endpoint to get low stock parts (stock <= threshold)
    @GetMapping("/low-stock")
    public List<Part> getLowStockParts() {
        return partRepository.findByStockQuantityLessThanEqual(5); // example threshold
    }
}