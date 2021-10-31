package io.licitat.hiberplay.model;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Objects;

@Audited
@Entity()
@Table(name = "rtbeam")
@Access(AccessType.FIELD)
public class RtBeam {

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

        public RtBeam createRtBeam() {
            return new RtBeam(name, number);
        }
    }

    public RtBeam() { }

    public RtBeam(String name, Integer number) {
        this.name = name;
        this.number = number;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String name;
    private Integer number;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private RtFractionGroup fractionGroup;

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

    public Long getId() {
        return id;
    }

    public RtFractionGroup getFractionGroup() {
        return fractionGroup;
    }

    public void setFractionGroup(RtFractionGroup fractionGroup) {
        this.fractionGroup = fractionGroup;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RtBeam that = (RtBeam) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
