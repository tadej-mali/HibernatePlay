package io.licitat.hiberplay.model;

import io.licitat.hiberplay.persistence.DasGenerator;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Audited
@Entity()
@Table(name = "rtplan")
@Access(AccessType.FIELD)
public class RtPlan {
    public static class Builder {

        private String name;
        private String patientId;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setPatientId(String patientId) {
            this.patientId = patientId;
            return this;
        }

        public RtPlan createRtPlan() {
            return new RtPlan(name, patientId);
        }
    }

    public RtPlan() { }

    public RtPlan(String name, String patientId) {
        this.name = name;
        this.patientId = patientId;
        this.fractionGroups = new ArrayList<>();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = DasGenerator.NAME)
    private Long id;

    private String name;
    private String patientId;

    @OneToMany(
        mappedBy = "plan",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<RtFractionGroup> fractionGroups;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public List<RtFractionGroup> getFractionGroups() {
        return fractionGroups;
    }

    public RtFractionGroup addFractionGroup(RtFractionGroup group) {
        this.fractionGroups.add(group);
        group.setPlan(this);
        return group;
    }

    public void removeFractionGroup(RtFractionGroup group) {
        group.setPlan(null);
        this.fractionGroups.remove(group);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RtPlan rtPlan = (RtPlan) o;
        return Objects.equals(id, rtPlan.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
