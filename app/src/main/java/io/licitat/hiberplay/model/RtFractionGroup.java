package io.licitat.hiberplay.model;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Audited
@Entity()
@Table(name = "rtfractiongroup")
@Access(AccessType.FIELD)
public class RtFractionGroup {

    public static class Builder {
        private String name;
        private Integer number;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setNumber(Integer number) {
            this.number = number;
            return this;
        }

        public RtFractionGroup createFractionGroup() {
            return new RtFractionGroup(name, number);
        }
    }

    public RtFractionGroup() { }

    public RtFractionGroup(String name, Integer number) {
        this.name = name;
        this.number = number;
        this.beams = new ArrayList<>();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String name;
    private Integer number;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private RtPlan plan;

    @OneToMany(
        mappedBy = "fractionGroup",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<RtBeam> beams;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public RtPlan getPlan() {
        return plan;
    }

    public void setPlan(RtPlan plan) {
        this.plan = plan;
    }

    public List<RtBeam> getBeams() {
        return beams;
    }

    public RtBeam addBeam(RtBeam beam) {
        this.beams.add(beam);
        beam.setFractionGroup(this);
        return beam;
    }

    public void removeBeam(RtBeam beam) {
        beam.setFractionGroup(null);
        this.beams.remove(beam);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RtFractionGroup that = (RtFractionGroup) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
