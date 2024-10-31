package com.aiomed.treatmentscheduler.service;

import com.aiomed.treatmentscheduler.entity.TreatmentPlan;
import com.aiomed.treatmentscheduler.entity.TreatmentTask;
import com.aiomed.treatmentscheduler.entity.enums.TreatmentPlanStatusTypes;
import com.aiomed.treatmentscheduler.entity.enums.TreatmentTaskStatusTypes;
import com.aiomed.treatmentscheduler.repository.TreatmentPlanRepository;
import com.aiomed.treatmentscheduler.repository.TreatmentTaskRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import static java.util.Objects.nonNull;
import java.util.stream.Collectors;
import net.fortuna.ical4j.model.Recur;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TreatmentTaskSchedulerService {
    
    private final static Logger log = LoggerFactory.getLogger(TreatmentTaskSchedulerService.class);
    
    @Autowired
    private TreatmentPlanRepository planRepository;
    
    @Autowired
    private TreatmentTaskRepository taskRepository;
    
    @Transactional(readOnly = true)
    public List<TreatmentPlan> getPlansForTaskGeneration(
            LocalDateTime startTime, LocalDateTime endTime) {
        return planRepository.findPlansByGeneratedTillBetween(startTime, endTime, 
                TreatmentPlanStatusTypes.ACTIVE);
    }
    
    @Transactional
    public void generateTreatmentTasksForPlan(TreatmentPlan plan, LocalDate generateTill) {
        log.info("Generating treatment tasks for plan {}", plan.getId());
        Recur<LocalDate> recur = new Recur<>(plan.getRecurrencePattern());
        LocalDate startRangeDate = nonNull(plan.getGeneratedTill()) 
                ? plan.getGeneratedTill().toLocalDate().plusDays(1) 
                : plan.getStartDate();
        LocalDate endRangeDate = nonNull(plan.getEndDate()) && plan.getEndDate().isBefore(generateTill) 
                ? plan.getEndDate() 
                : generateTill;
        log.info("For plan {}, calculated start date {} and end date {}", 
                plan.getId(), startRangeDate, endRangeDate);
        
        List<TreatmentTask> createTasks = recur.getDates(startRangeDate, endRangeDate).stream()
                .flatMap(ld -> plan.getStartTimes().stream()
                        .map(lt -> ld.atTime(lt))
                        .collect(Collectors.toList())
                        .stream())
                .map(ldt -> new TreatmentTask()
                        .setSubjectPatient(plan.getSubjectPatient())
                        .setTaskDateTime(ldt)
                        .setTreatmentAction(plan.getTreatmentAction())
                        .setStatus(TreatmentTaskStatusTypes.ACTIVE)
                        .setTreatmentPlan(plan))
                .collect(Collectors.toList());
        if (!createTasks.isEmpty()) {
            LocalDateTime lastTaskTime = createTasks.stream()
                    .map(TreatmentTask::getTaskDateTime)
                    .max(LocalDateTime::compareTo)
                    .get();
            log.info("For plan {}, task generated: {}, generated till: {}", 
                    plan.getId(), createTasks.size(), lastTaskTime);
            taskRepository.saveAll(createTasks);
            plan.setGeneratedTill(lastTaskTime);
            planRepository.save(plan);
        } else {
            LocalDateTime generatedTillTime = generateTill.atTime(23, 59, 59);
            log.info("No tasks generated for plan {}, moving generated date to: {}", 
                    plan.getId(), generatedTillTime);
            plan.setGeneratedTill(generatedTillTime);
            planRepository.save(plan);
        }
    }
    
}
