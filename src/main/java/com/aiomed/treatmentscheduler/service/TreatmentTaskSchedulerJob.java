package com.aiomed.treatmentscheduler.service;

import com.aiomed.treatmentscheduler.entity.TreatmentPlan;
import jakarta.annotation.PostConstruct;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import static java.util.Objects.nonNull;
import java.util.concurrent.ScheduledFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

@Component
public class TreatmentTaskSchedulerJob {
    
    private final static Logger log = LoggerFactory.getLogger(TreatmentTaskSchedulerJob.class);
    
    @Autowired
    private TreatmentTaskSchedulerService treatmentTaskSchedulerService;
    
    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;
    
    private ScheduledFuture jobFuture;
    
    @PostConstruct
    public void init() {
        Trigger trigger = (tgCtx) -> {
            //If scheduled before - execute at 4 AM local timezone next day
            if (nonNull(tgCtx.lastActualExecution())) {
                Instant next = tgCtx.lastActualExecution()
                        .atZone(ZoneOffset.systemDefault())
                        .plus(1, ChronoUnit.DAYS)
                        .withHour(4)
                        .withMinute(0)
                        .withSecond(0)
                        .withNano(0)
                        .toInstant();
                log.info("Job ran at {}, next trigger: {}", 
                        tgCtx.lastActualExecution(), next);
                return next;
            }
            //If not scheduled before - execute now
            Instant now = Instant.now();
            log.info("Job first run - trigger now: {}", now);
            return now;
        };
        jobFuture = threadPoolTaskScheduler.schedule(() -> {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime till = now.plusDays(1);
            LocalDate generateTill = now.toLocalDate().plusDays(2);
            log.info("Now: {}, searching till: {}, generation till: {}", 
                    now, till, generateTill);
            List<TreatmentPlan> plansToGenerate = 
                    treatmentTaskSchedulerService.getPlansForTaskGeneration(now, till);
            log.info("Found {} plans", plansToGenerate.size());
            for (TreatmentPlan planToGenerate: plansToGenerate) {
                try {
                    treatmentTaskSchedulerService
                            .generateTreatmentTasksForPlan(planToGenerate, generateTill);
                } catch (Exception ex) {
                    log.error("Error on task generation", ex);
                    //Just catch and log by now. Any option for mitigation this error can be added later.
                }
            }
        }, trigger);
    }
    
    //For tests only
    public ScheduledFuture getJobFuture() {
        return jobFuture;
    }
}
