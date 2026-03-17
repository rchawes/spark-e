package com.electrician.spark_e.repository;

import com.electrician.spark_e.model.ProjectPicture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectPictureRepository extends JpaRepository<ProjectPicture, Long> {
    
    List<ProjectPicture> findByJobId(Long jobId);
    
    List<ProjectPicture> findByJobIdOrderByUploadedAtDesc(Long jobId);
    
    List<ProjectPicture> findByJobIdAndCategory(Long jobId, String category);
    
    @Query("SELECT p FROM ProjectPicture p WHERE p.job.id = :jobId AND p.category = :category ORDER BY p.uploadedAt DESC")
    List<ProjectPicture> findByJobIdAndCategoryOrderByUploadedAtDesc(
            @Param("jobId") Long jobId, 
            @Param("category") String category
    );
    
    Optional<ProjectPicture> findByJobIdAndFileName(Long jobId, String fileName);
    
    @Query("SELECT COUNT(p) FROM ProjectPicture p WHERE p.job.id = :jobId")
    Long countByJobId(@Param("jobId") Long jobId);
    
    @Query("SELECT COUNT(p) FROM ProjectPicture p WHERE p.job.id = :jobId AND p.category = :category")
    Long countByJobIdAndCategory(@Param("jobId") Long jobId, @Param("category") String category);
    
    void deleteByJobId(Long jobId);
}
