package io.licitat.hiberplay.plays;

import io.licitat.hiberplay.dto.PlanDTO;
import io.licitat.hiberplay.mappers.PlanMapper;
import io.licitat.hiberplay.model.RtPlan;

import javax.persistence.EntityManagerFactory;
import java.util.function.Supplier;

import static io.licitat.hiberplay.plays.RtPlanAdventures.buildTestPlan;
import static org.hibernate.testing.transaction.TransactionUtil.doInJPA;

public class ToDtoAndBack implements Runnable {

    private Supplier<EntityManagerFactory> factorySupplier;

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
        dto.getFractionGroups().get(0).getBeams().get(1).setName(dto.getFractionGroups().get(0).getBeams().get(1).getName() + "modified");

        RtPlan updatedPlan = doInJPA(factorySupplier, em -> {
            RtPlan dbPlan = em.find(RtPlan.class, insertedPlan.getId());

            theMapper.toEntity(dto, dbPlan);

            return em.merge(dbPlan);
        });

        updatedPlan.toString();
    }
}
