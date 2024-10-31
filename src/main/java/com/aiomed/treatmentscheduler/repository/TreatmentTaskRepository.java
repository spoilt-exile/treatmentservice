package com.aiomed.treatmentscheduler.repository;

import com.aiomed.treatmentscheduler.entity.TreatmentTask;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TreatmentTaskRepository extends JpaRepository<TreatmentTask, Long> {
    
    @Query("select tt from TreatmentTask tt where tt.treatmentPlan.id = :planId")
    List<TreatmentTask> findTasksByPlan(@Param("planId") Long planId);
    
}
