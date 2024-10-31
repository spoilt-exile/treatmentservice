package com.aiomed.treatmentscheduler.entity;

import com.aiomed.treatmentscheduler.entity.enums.TreatmentActionTypes;
import com.aiomed.treatmentscheduler.entity.enums.TreatmentTaskStatusTypes;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import static jakarta.persistence.EnumType.STRING;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "treatment_task")
public class TreatmentTask {
    
    @Id
    @GeneratedValue(strategy =  GenerationType.SEQUENCE)
    private Long id;
    
    @Column(name = "treatment_action", nullable = false)
    @Enumerated(value = STRING)
    private TreatmentActionTypes treatmentAction;
    
    @Column(name = "subject_patient", nullable = false)
    private String subjectPatient;
    
    @Column(name = "task_datetime", nullable = false)
    private LocalDateTime taskDateTime;
    
    @Column(name = "status", nullable = false)
    @Enumerated(value = STRING)
    private TreatmentTaskStatusTypes status;
    
    @ManyToOne
    @JoinColumn(name = "plan_id", nullable = false)
    private TreatmentPlan treatmentPlan;

    public Long getId() {
        return id;
    }

    public TreatmentTask setId(Long id) {
        this.id = id;
        return this;
    }

    public TreatmentActionTypes getTreatmentAction() {
        return treatmentAction;
    }

    public TreatmentTask setTreatmentAction(TreatmentActionTypes treatmentAction) {
        this.treatmentAction = treatmentAction;
        return this;
    }

    public String getSubjectPatient() {
        return subjectPatient;
    }

    public TreatmentTask setSubjectPatient(String subjectPatient) {
        this.subjectPatient = subjectPatient;
        return this;
    }

    public LocalDateTime getTaskDateTime() {
        return taskDateTime;
    }

    public TreatmentTask setTaskDateTime(LocalDateTime taskDateTime) {
        this.taskDateTime = taskDateTime;
        return this;
    }

    public TreatmentTaskStatusTypes getStatus() {
        return status;
    }

    public TreatmentTask setStatus(TreatmentTaskStatusTypes status) {
        this.status = status;
        return this;
    }

    public TreatmentPlan getTreatmentPlan() {
        return treatmentPlan;
    }

    public TreatmentTask setTreatmentPlan(TreatmentPlan treatmentPlan) {
        this.treatmentPlan = treatmentPlan;
        return this;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(this.id);
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
        final TreatmentTask other = (TreatmentTask) obj;
        if (!Objects.equals(this.subjectPatient, other.subjectPatient)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (this.treatmentAction != other.treatmentAction) {
            return false;
        }
        if (this.status != other.status) {
            return false;
        }
        return Objects.equals(this.taskDateTime, other.taskDateTime);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TreatmentTask{");
        sb.append("id=").append(id);
        sb.append(", treatmentAction=").append(treatmentAction);
        sb.append(", subjectPatient=").append(subjectPatient);
        sb.append(", taskDateTime=").append(taskDateTime);
        sb.append(", status=").append(status);
        sb.append('}');
        return sb.toString();
    }
}
