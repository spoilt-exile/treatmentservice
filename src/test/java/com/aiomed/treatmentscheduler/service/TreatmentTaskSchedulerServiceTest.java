package com.aiomed.treatmentscheduler.service;

import com.aiomed.treatmentscheduler.entity.TreatmentPlan;
import com.aiomed.treatmentscheduler.entity.TreatmentTask;
import com.aiomed.treatmentscheduler.entity.enums.TreatmentActionTypes;
import com.aiomed.treatmentscheduler.entity.enums.TreatmentPlanStatusTypes;
import com.aiomed.treatmentscheduler.entity.enums.TreatmentTaskStatusTypes;
import com.aiomed.treatmentscheduler.repository.TreatmentPlanRepository;
import com.aiomed.treatmentscheduler.repository.TreatmentTaskRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import static java.util.Objects.nonNull;
import java.util.stream.Collectors;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TreatmentTaskSchedulerServiceTest extends BaseTest {
    
    private final static String RECUR_WEEKLY_MO_WE_FR = "FREQ=WEEKLY;INTERVAL=1;BYDAY=MO,WE,FR";
    
    @Autowired
    private TreatmentTaskSchedulerService schedulingService;
    
    @Autowired
    private TreatmentPlanRepository planRepository;
    
    @Autowired
    private TreatmentTaskRepository taskRepository;
    
    @Test
    public void shouldGetPlansForTaskGeneration() {
        //Visible in range for generation
        TreatmentPlan plan1 = createAndSavePlan(LocalDate.now(), 
                LocalDate.now().plusDays(14), 
                TreatmentPlanStatusTypes.ACTIVE, 
                "", 
                List.of(LocalTime.now()), 
                LocalDateTime.now().plusDays(2));
        
        //Visible in range for generation (newly created)
        TreatmentPlan plan2 = createAndSavePlan(LocalDate.now(), 
                LocalDate.now().plusDays(14), 
                TreatmentPlanStatusTypes.ACTIVE, 
                "", 
                List.of(LocalTime.now()), 
                null);
        
        //Not visible - disabled
        TreatmentPlan plan3 = createAndSavePlan(LocalDate.now(), 
                LocalDate.now().plusDays(14), 
                TreatmentPlanStatusTypes.DISABLED, 
                "", 
                List.of(LocalTime.now()), 
                LocalDateTime.now().plusDays(2));
        
        //Not visible - too distant in the future
        TreatmentPlan plan4 = createAndSavePlan(LocalDate.now().plusDays(14), 
                LocalDate.now().plusDays(28), 
                TreatmentPlanStatusTypes.ACTIVE, 
                "", 
                List.of(LocalTime.now()), 
                LocalDateTime.now().plusDays(14));
        
        List<TreatmentPlan> plans = schedulingService
                .getPlansForTaskGeneration(LocalDateTime.now(), LocalDateTime.now().plusDays(7));
        Assertions.assertThat(plans).hasSize(2);
        Assertions.assertThat(plans).containsOnly(plan1, plan2);
        Assertions.assertThat(plans).doesNotContain(plan3, plan4);
    }
    
    @Test
    public void shouldGenerateTreatmentTasksForPlanNew() {
        
        //Newly created plan, end date within horizon
        TreatmentPlan plan = createAndSavePlan(LocalDate.of(2024, 11, 4), 
                LocalDate.of(2024, 11, 11), 
                TreatmentPlanStatusTypes.ACTIVE, 
                RECUR_WEEKLY_MO_WE_FR, 
                List.of(LocalTime.of(12, 0), LocalTime.of(18, 0)), 
                null);
        
        schedulingService.generateTreatmentTasksForPlan(plan, LocalDate.of(2024, 11, 14));
        
        List<TreatmentTask> tasks = taskRepository.findTasksByPlan(plan.getId());
        Assertions.assertThat(tasks).hasSize(8);
        
        //Monday, Wednesday, Friday and next Monday
        List<LocalDateTime> expectedDateTimes = List.of(
                LocalDateTime.of(2024, 11, 4, 12, 0),
                LocalDateTime.of(2024, 11, 4, 18, 0),
                LocalDateTime.of(2024, 11, 6, 12, 0),
                LocalDateTime.of(2024, 11, 6, 18, 0),
                LocalDateTime.of(2024, 11, 8, 12, 0),
                LocalDateTime.of(2024, 11, 8, 18, 0),
                LocalDateTime.of(2024, 11, 11, 12, 0),
                LocalDateTime.of(2024, 11, 11, 18, 0)
        );
        
        tasks.forEach(task -> assertTreatmentTaskByPlan(task, plan, expectedDateTimes));
        
        TreatmentPlan updatedPlan = planRepository.findById(plan.getId()).orElseThrow();
        Assertions.assertThat(updatedPlan.getGeneratedTill()).isEqualTo(LocalDateTime.of(2024, 11, 11, 18, 0));
    }
    
    @Test
    public void shouldGenerateTreatmentTasksForPlanExisting() {
        
        //Existing plan, end date is null (endless)
        TreatmentPlan plan = createAndSavePlan(LocalDate.of(2024, 11, 4), 
                null, 
                TreatmentPlanStatusTypes.ACTIVE, 
                RECUR_WEEKLY_MO_WE_FR, 
                List.of(LocalTime.of(12, 0), LocalTime.of(18, 0)), 
                LocalDateTime.of(2024, 11, 4, 18, 0));
        
        schedulingService.generateTreatmentTasksForPlan(plan, LocalDate.of(2024, 11, 11));
        
        List<TreatmentTask> tasks = taskRepository.findTasksByPlan(plan.getId());
        Assertions.assertThat(tasks).hasSize(6);
        
        //Wednesday, Friday and next Monday
        List<LocalDateTime> expectedDateTimes = List.of(
                LocalDateTime.of(2024, 11, 6, 12, 0),
                LocalDateTime.of(2024, 11, 6, 18, 0),
                LocalDateTime.of(2024, 11, 8, 12, 0),
                LocalDateTime.of(2024, 11, 8, 18, 0),
                LocalDateTime.of(2024, 11, 11, 12, 0),
                LocalDateTime.of(2024, 11, 11, 18, 0)
        );
        
        tasks.forEach(task -> assertTreatmentTaskByPlan(task, plan, expectedDateTimes));
        
        TreatmentPlan updatedPlan = planRepository.findById(plan.getId()).orElseThrow();
        Assertions.assertThat(updatedPlan.getGeneratedTill()).isEqualTo(LocalDateTime.of(2024, 11, 11, 18, 0));
    }
    
    @Test
    public void shouldGenerateTreatmentTasksForPlanSkip() {
        
        //Newly created plan, out of range
        TreatmentPlan plan = createAndSavePlan(LocalDate.of(2024, 12, 4), 
                LocalDate.of(2024, 12, 24), 
                TreatmentPlanStatusTypes.ACTIVE, 
                RECUR_WEEKLY_MO_WE_FR, 
                List.of(LocalTime.of(12, 0), LocalTime.of(18, 0)), 
                LocalDateTime.of(2024, 12, 1, 0, 0));
        
        schedulingService.generateTreatmentTasksForPlan(plan, LocalDate.of(2024, 11, 11));
        
        List<TreatmentTask> tasks = taskRepository.findTasksByPlan(plan.getId());
        Assertions.assertThat(tasks).hasSize(0);
        
        TreatmentPlan updatedPlan = planRepository.findById(plan.getId()).orElseThrow();
        Assertions.assertThat(updatedPlan.getGeneratedTill()).isEqualTo(LocalDateTime.of(2024, 11, 11, 23, 59, 59));
    }
    
    private TreatmentPlan createAndSavePlan(LocalDate startDate, LocalDate endDate, 
            TreatmentPlanStatusTypes status, String recurrencePattern, 
            List<LocalTime> tTimes, LocalDateTime generatedTill) {
        final LocalDateTime generatedTillEx = nonNull(generatedTill) ? generatedTill.withNano(0) : null;
        final List<LocalTime> tTimesEx = tTimes.stream().map(tt -> tt.withNano(0)).collect(Collectors.toList());
        TreatmentPlan newPlan = new TreatmentPlan()
                .setTreatmentAction(TreatmentActionTypes.ACTION_A)
                .setSubjectPatient("John Smith")
                .setStartDate(startDate)
                .setEndDate(endDate)
                .setStatus(status)
                .setRecurrencePattern(recurrencePattern)
                .setStartTimes(tTimesEx)
                .setGeneratedTill(generatedTillEx);
        return planRepository.save(newPlan);
    }
    
    private void assertTreatmentTaskByPlan(TreatmentTask task, TreatmentPlan plan, List<LocalDateTime> taskTimes) {
        Assertions.assertThat(task.getTreatmentAction()).isEqualTo(plan.getTreatmentAction());
        Assertions.assertThat(task.getSubjectPatient()).isEqualTo(plan.getSubjectPatient());
        Assertions.assertThat(taskTimes).contains(task.getTaskDateTime());
        Assertions.assertThat(task.getStatus()).isEqualTo(TreatmentTaskStatusTypes.ACTIVE);
    }
}
