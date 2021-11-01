package io.licitat.hiberplay.mappers;

import io.licitat.hiberplay.dto.BeamDTO;
import io.licitat.hiberplay.dto.FractionGroupDTO;
import io.licitat.hiberplay.dto.PlanDTO;
import io.licitat.hiberplay.mapping.ClassMapper;
import io.licitat.hiberplay.model.RtBeam;
import io.licitat.hiberplay.model.RtFractionGroup;
import io.licitat.hiberplay.model.RtPlan;

public class PlanMapper {

    public PlanDTO toApi (RtPlan plan) {

        PlanDTO dto = new PlanDTO();

        new ClassMapper<RtPlan, PlanDTO>()
            .map(RtPlan::getId,         PlanDTO::setId)
            .map(RtPlan::getPatientId,  PlanDTO::setPatientId)
            .mapCollection(RtPlan::getFractionGroups, PlanDTO::setFractionGroups, FractionGroupDTO::new, fg -> fg
                .map(RtFractionGroup::getId,                FractionGroupDTO::setId)
                .map(RtFractionGroup::getName,              FractionGroupDTO::setName)
                .map(RtFractionGroup::getNumber,            FractionGroupDTO::setNumber)
                .mapCollection(RtFractionGroup::getBeams,   FractionGroupDTO::setBeams, BeamDTO::new, b -> b
                    .map(RtBeam::getId,     BeamDTO::setId)
                    .map(RtBeam::getName,   BeamDTO::setName)
                    .map(RtBeam::getNumber, BeamDTO::setNumber)
                )
            )
            .map(RtPlan::getName, PlanDTO::setName)
            .map(plan, dto);

        return dto;
    }

    public void toEntity(PlanDTO dto, RtPlan dbPlan) {

        new ClassMapper<PlanDTO, RtPlan>()
            .map(PlanDTO::getPatientId, RtPlan::setPatientId)
            /*
            .mapCollection(RtPlan::getFractionGroups, PlanDTO::setFractionGroups, FractionGroupDTO::new, fg -> fg
                .map(RtFractionGroup::getId, FractionGroupDTO::setId)
                .map(RtFractionGroup::getName, FractionGroupDTO::setName)
                .map(RtFractionGroup::getNumber, FractionGroupDTO::setNumber)
                .mapCollection(RtFractionGroup::getBeams, FractionGroupDTO::setBeams, BeamDTO::new, b -> b
                    .map(RtBeam::getId, BeamDTO::setId)
                    .map(RtBeam::getName, BeamDTO::setName)
                    .map(RtBeam::getNumber, BeamDTO::setNumber)
                )
            )
            */
            .map(PlanDTO::getName, RtPlan::setName)
            .map(dto, dbPlan);

        ClassMapper<FractionGroupDTO, RtFractionGroup> fgMapper = new ClassMapper<FractionGroupDTO, RtFractionGroup>()
            .map(FractionGroupDTO::getName, RtFractionGroup::setName)
            .map(FractionGroupDTO::getNumber, RtFractionGroup::setNumber);

        ClassMapper<BeamDTO, RtBeam> beamMapper = new ClassMapper<BeamDTO, RtBeam>()
            .map(BeamDTO::getName, RtBeam::setName)
            .map(BeamDTO::getNumber, RtBeam::setNumber);

        dto.getFractionGroups().forEach(fg -> {
            dbPlan
                .findGroupById(fg.getId())
                .ifPresent(fgDb -> {
                    fgMapper.map(fg, fgDb);

                    fg.getBeams().forEach(b -> {
                        fgDb.getBeams().stream()
                            .filter(x -> x.getId().equals(b.getId()))
                            .findAny()
                            .ifPresent(bDb -> beamMapper.map(b, bDb));
                    });
                });
        });
    }
}
