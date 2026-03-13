package com.electrician.spark_e.controller;

import com.electrician.spark_e.model.Electrician;
import com.electrician.spark_e.repository.ElectricianRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/electricians")
public class ElectricianController {

    @Autowired
    private ElectricianRepository electricianRepository;

    @GetMapping
    public List<Electrician> getAllElectrician() {
        return electricianRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Electrician> getElectricianById(@NonNull @PathVariable Long id) {
        return electricianRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Electrician createElectrician(@NonNull @RequestBody Electrician electrician) {
        return  electricianRepository.save(electrician);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Electrician> updateElectrician(@NonNull @PathVariable Long id, @NonNull @RequestBody Electrician electricianDetails) {
        return electricianRepository.findById(id)
                .map(electrician -> {
                    electrician.setName(electricianDetails.getName());
                    electrician.setEmail(electricianDetails.getEmail());
                    electrician.setPhone(electricianDetails.getPhone());
                    electrician.setHourlyRate(electricianDetails.getHourlyRate());
                    electrician.setKnowledgeLevel(electricianDetails.getKnowledgeLevel());
                    electrician.setLicenseNumber(electricianDetails.getLicenseNumber());
                    return ResponseEntity.ok(electricianRepository.save(electrician));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteElectrician(@NonNull @PathVariable Long id) {
        if (electricianRepository.existsById(id)) {
            electricianRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
