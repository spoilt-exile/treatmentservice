package com.aiomed.treatmentscheduler.repository;

import com.aiomed.treatmentscheduler.entity.TreatmentPlan;
import com.aiomed.treatmentscheduler.entity.enums.TreatmentPlanStatusTypes;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TreatmentPlanRepository extends JpaRepository<TreatmentPlan, Long> {
    
    @Query("select tp from TreatmentPlan tp where tp.status = :status "
            + "AND (tp.generatedTill between :startTime and :endTime OR tp.generatedTill is null)")
    List<TreatmentPlan> findPlansByGeneratedTillBetween(@Param("startTime") LocalDateTime startTime, 
            @Param("endTime") LocalDateTime endTime, @Param("status") TreatmentPlanStatusTypes status);
}
