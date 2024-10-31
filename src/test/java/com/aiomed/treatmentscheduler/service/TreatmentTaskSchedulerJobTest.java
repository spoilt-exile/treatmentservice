package com.aiomed.treatmentscheduler.service;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import static java.util.Objects.nonNull;
import java.util.concurrent.ScheduledFuture;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TreatmentTaskSchedulerJobTest extends BaseTest {
    
    @Autowired
    private TreatmentTaskSchedulerJob schedulerJob;
    
    @Test
    public void shouldInitScheduler() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        ScheduledFuture jobFuture = schedulerJob.getJobFuture();
        Assertions.assertThat(nonNull(jobFuture)).isTrue();
        
        Instant expectedScheduledTime = Instant.now()
                .atZone(ZoneOffset.systemDefault())
                .plus(1, ChronoUnit.DAYS)
                .withHour(4)
                .withMinute(0)
                .withSecond(0)
                .withNano(0)
                .toInstant();
        
        Instant scheduleTime = getInstantFromFuture(jobFuture);
        Assertions.assertThat(expectedScheduledTime).isEqualTo(scheduleTime);
    }
    
    private Instant getInstantFromFuture(ScheduledFuture jobFuture) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field scheduleFiled = jobFuture.getClass().getDeclaredField("scheduledExecutionTime");
        scheduleFiled.setAccessible(true);
        return (Instant) scheduleFiled.get(jobFuture);
    }
}
