package io.licitat.hiberplay.plays;

import io.licitat.hiberplay.dto.FractionGroupDTO;
import io.licitat.hiberplay.dto.PlanDTO;
import io.licitat.hiberplay.mappers.PlanMapper;
import io.licitat.hiberplay.model.RtBeam;
import io.licitat.hiberplay.model.RtFractionGroup;
import io.licitat.hiberplay.model.RtPlan;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;

import javax.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static io.licitat.hiberplay.plays.RtPlanAdventures.buildTestPlan;
import static org.hibernate.testing.transaction.TransactionUtil.doInJPA;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ToDtoAndBack implements Runnable {

    private final Supplier<EntityManagerFactory> factorySupplier;

    public ToDtoAndBack(Supplier<EntityManagerFactory> factorySupplier) {
        this.factorySupplier = factorySupplier;
    }

    @Override
    public void run() {

        RtPlan insertedPlan = doInJPA(factorySupplier, em -> {
            RtPlan aPlan = buildTestPlan();
            em.persist(aPlan);
            return aPlan;
        });

        PlanMapper theMapper = new PlanMapper();
        PlanDTO dto = theMapper.toApi(insertedPlan);

        dto.setName(dto.getName() + " modified");
        dto.setPatientId(dto.getPatientId() + " modified");
        dto.getFractionGroups().get(1).setName(dto.getFractionGroups().get(1).getName() + " modified");
        dto.getFractionGroups().get(0).getBeams().get(1).setName(dto.getFractionGroups().get(0).getBeams().get(1).getName() + " modified");

        RtPlan updatedPlan = doInJPA(factorySupplier, em -> {
            RtPlan dbPlan = em.find(RtPlan.class, insertedPlan.getId());

            theMapper.toEntity(dto, dbPlan);

            return em.merge(dbPlan);
        });


        RtPlan secondPlan = doInJPA(factorySupplier, em -> {
            RtPlan aPlan = buildTestPlan();
            em.persist(aPlan);
            return aPlan;
        });

        ModelMapper modelMapper = new ModelMapper();
        PlanDTO cool = modelMapper.map(secondPlan, PlanDTO.class);
        assertNotNull(cool.getId());
        assertNotNull(cool.getFractionGroups());
        assertFalse(cool.getFractionGroups().isEmpty());

        cool.setName(cool.getName() + " modified and automapped");
        cool.setPatientId(cool.getPatientId() + " modified and automapped");

        Optional.of(cool)
            .flatMap(p -> get(p.getFractionGroups(), 1))
            .ifPresent(fg -> {
                fg.setName(fg.getName() + " modified and automapped");
                fg.setNumber(42); // This shall not be mapped
            });
        Optional.of(cool)
            .flatMap(p -> get(p.getFractionGroups(), 0))
            .flatMap(fg -> get(fg.getBeams(), 1))
            .ifPresent(b -> b.setName(b.getName() + " modified and automapped"));


        RtPlan secondUpdatedPlan = doInJPA(factorySupplier, em -> {
            RtPlan dbPlan = em.find(RtPlan.class, cool.getId());

            ModelMapper toDbMapper = new ModelMapper();
            toDbMapper.addConverter(dtoToModel);
            toDbMapper.map(cool, dbPlan);

            return em.merge(dbPlan);
        });


        updatedPlan.toString();
    }

    private Converter<PlanDTO, RtPlan> dtoToModel = new Converter<PlanDTO, RtPlan>() {

        @Override
        public RtPlan convert(MappingContext<PlanDTO, RtPlan> ctx) {
            RtPlan dstPlan = ctx.getDestination();
            PlanDTO planDto = ctx.getSource();

            dstPlan.setPatientId(planDto.getPatientId());
            dstPlan.setName(planDto.getName());

            planDto.getFractionGroups().forEach(fgDto -> {
                RtFractionGroup dstGroup = dstPlan
                    .findGroupById(fgDto.getId())
                    .orElseGet(() -> dstPlan.addFractionGroup(new RtFractionGroup()));

                new ModelMapper()
                    .typeMap(FractionGroupDTO.class, RtFractionGroup.class)
                        .addMappings(m -> m.skip(RtFractionGroup::setNumber))
                    .map(fgDto, dstGroup);

                fgDto.getBeams().forEach(beamDto -> {
                    RtBeam dstBeam = dstGroup
                        .findBeamById(beamDto.getId())
                        .orElseGet(() -> dstGroup.addBeam(new RtBeam()));

                    new ModelMapper().map(beamDto, dstBeam);
                });
            });

            // TODO handle removals

            return dstPlan;
        }
    };

    private static <T> Optional<T> get(List<T> source, int i) {
        if (source == null) { return Optional.empty(); }

        return Optional.ofNullable(source.get(i));
    }
}
