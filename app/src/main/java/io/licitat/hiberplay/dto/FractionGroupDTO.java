package io.licitat.hiberplay.dto;

import java.util.List;

public class FractionGroupDTO {

    private Long id;
    private String name;
    private Integer number;
    private List<BeamDTO> beams;

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

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public List<BeamDTO> getBeams() {
        return beams;
    }

    public void setBeams(List<BeamDTO> beams) {
        this.beams = beams;
    }
}
