package com.aiomed.treatmentscheduler.entity;

import com.aiomed.treatmentscheduler.entity.enums.TreatmentActionTypes;
import com.aiomed.treatmentscheduler.entity.enums.TreatmentPlanStatusTypes;
import static jakarta.persistence.CascadeType.REMOVE;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import static jakarta.persistence.EnumType.STRING;
import jakarta.persistence.Enumerated;
import static jakarta.persistence.FetchType.LAZY;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "treatment_plan")
public class TreatmentPlan {
    
    @Id
    @GeneratedValue(strategy =  GenerationType.SEQUENCE)
    private Long id;
    
    @Column(name = "treatment_action", nullable = false)
    @Enumerated(value = STRING)
    private TreatmentActionTypes treatmentAction;
    
    @Column(name = "subject_patient", nullable = false)
    private String subjectPatient;
    
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    
    @Column(name = "end_date")
    private LocalDate endDate;
    
    //Start times of tasks for each recurrence in expression
    @Column(name = "start_times", nullable = false)
    private List<LocalTime> startTimes;
    
    //Uses RRULE syntax from iCal standard
    @Column(name = "recurrence_pattern", nullable = false)
    private String recurrencePattern;
    
    @Column(nullable = false)
    private TreatmentPlanStatusTypes status;
    
    @Column(name = "generated_till")
    private LocalDateTime generatedTill;

    @OneToMany(cascade = REMOVE, fetch = LAZY)
    private List<TreatmentTask> tasks;

    public Long getId() {
        return id;
    }

    public TreatmentPlan setId(Long id) {
        this.id = id;
        return this;
    }

    public TreatmentActionTypes getTreatmentAction() {
        return treatmentAction;
    }

    public TreatmentPlan setTreatmentAction(TreatmentActionTypes treatmentAction) {
        this.treatmentAction = treatmentAction;
        return this;
    }

    public String getSubjectPatient() {
        return subjectPatient;
    }

    public TreatmentPlan setSubjectPatient(String subjectPatient) {
        this.subjectPatient = subjectPatient;
        return this;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public TreatmentPlan setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public TreatmentPlan setEndDate(LocalDate endDate) {
        this.endDate = endDate;
        return this;
    }

    public List<LocalTime> getStartTimes() {
        return startTimes;
    }

    public TreatmentPlan setStartTimes(List<LocalTime> taskTimes) {
        this.startTimes = taskTimes;
        return this;
    }

    public String getRecurrencePattern() {
        return recurrencePattern;
    }

    public TreatmentPlan setRecurrencePattern(String recurrencePattern) {
        this.recurrencePattern = recurrencePattern;
        return this;
    }

    public TreatmentPlanStatusTypes getStatus() {
        return status;
    }

    public TreatmentPlan setStatus(TreatmentPlanStatusTypes status) {
        this.status = status;
        return this;
    }

    public LocalDateTime getGeneratedTill() {
        return generatedTill;
    }

    public TreatmentPlan setGeneratedTill(LocalDateTime generatedTill) {
        this.generatedTill = generatedTill;
        return this;
    }

    public List<TreatmentTask> getTasks() {
        return tasks;
    }

    public TreatmentPlan setTasks(List<TreatmentTask> tasks) {
        this.tasks = tasks;
        return this;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TreatmentPlan other = (TreatmentPlan) obj;
        if (!Objects.equals(this.subjectPatient, other.subjectPatient)) {
            return false;
        }
        if (!Objects.equals(this.recurrencePattern, other.recurrencePattern)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (this.treatmentAction != other.treatmentAction) {
            return false;
        }
        if (!Objects.equals(this.startDate, other.startDate)) {
            return false;
        }
        if (!Objects.equals(this.endDate, other.endDate)) {
            return false;
        }
        if (!Objects.equals(this.startTimes, other.startTimes)) {
            return false;
        }
        if (this.status != other.status) {
            return false;
        }
        return Objects.equals(this.generatedTill, other.generatedTill);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TreatmentPlan{");
        sb.append("id=").append(id);
        sb.append(", treatmentAction=").append(treatmentAction);
        sb.append(", subjectPatient=").append(subjectPatient);
        sb.append(", startDate=").append(startDate);
        sb.append(", endDate=").append(endDate);
        sb.append(", taskTimes=").append(startTimes);
        sb.append(", recurrencePattern=").append(recurrencePattern);
        sb.append(", status=").append(status);
        sb.append(", generatedTill=").append(generatedTill);
        sb.append('}');
        return sb.toString();
    }
}
