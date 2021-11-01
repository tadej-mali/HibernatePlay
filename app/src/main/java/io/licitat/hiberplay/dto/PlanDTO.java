package io.licitat.hiberplay.dto;

import java.util.ArrayList;
import java.util.List;

public class PlanDTO {

    private Long id;
    private String name;
    private String patientId;
    private List<FractionGroupDTO> fractionGroups = new ArrayList<>();


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<FractionGroupDTO> getFractionGroups() {
        return fractionGroups;
    }

    public void setFractionGroups(List<FractionGroupDTO> fractionGroups) {
        this.fractionGroups.addAll(fractionGroups);
    }
}
