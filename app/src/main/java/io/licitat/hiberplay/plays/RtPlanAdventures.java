package io.licitat.hiberplay.plays;

import io.licitat.hiberplay.model.*;

import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.*;


import java.util.function.Supplier;

import static java.util.Comparator.comparingInt;
import static org.hibernate.testing.transaction.TransactionUtil.doInJPA;


public class RtPlanAdventures implements Runnable {
    private final Supplier<EntityManagerFactory> factorySupplier;

    public RtPlanAdventures(Supplier<EntityManagerFactory> factorySupplier) {
        this.factorySupplier = factorySupplier;
    }

    public static RtPlan buildTestPlan() {
        RtPlan aPlan = new RtPlan.Builder()
            .setName("Full Cranio Spinal")
            .setPatientId("LIC-MAN-0001")
            .createRtPlan();

        RtFractionGroup firstGroup = aPlan.addFractionGroup(
            new RtFractionGroup.Builder().setName("RadRx Head").setNumber(1).createFractionGroup()
        );
        firstGroup.addBeam(new RtBeam.Builder().setName("Head Right").setNumber(1).createRtBeam());
        firstGroup.addBeam(new RtBeam.Builder().setName("Head Left").setNumber(2).createRtBeam());

        RtFractionGroup secondGroup = aPlan.addFractionGroup(
            new RtFractionGroup.Builder().setName("RadRx Neck").setNumber(2).createFractionGroup()
        );
        secondGroup.addBeam(new RtBeam.Builder().setName("Neck Right").setNumber(3).createRtBeam());
        secondGroup.addBeam(new RtBeam.Builder().setName("Neck Left").setNumber(4).createRtBeam());

        return aPlan;
    }

    @Override
    public void run() {

        // Build and save large plan
        Long savedFractionGroupId = doInJPA(factorySupplier, em -> {
            RtPlan aPlan = buildTestPlan();
            em.persist(aPlan);

            return aPlan.getFractionGroups().get(0).getId();
        });

        // Load saved plan to check lazy loading
        Long savedPlanId = doInJPA(factorySupplier, em -> {
            RtFractionGroup loadedFg = em.find(RtFractionGroup.class, savedFractionGroupId);

            // Let's see if this triggers a load
            System.out.println("The plan id of loaded FG is: " + loadedFg.getPlan().getId());

            // This should trigger a load
            System.out.println("The patient id of loaded FG is: " + loadedFg.getPlan().getPatientId());

            return loadedFg.getPlan().getId();
        });

        // Add a beam
        doInJPA(factorySupplier, em -> {
            RtPlan savedPlan = em.find(RtPlan.class, savedPlanId);

            savedPlan.getFractionGroups().get(1).addBeam(
                new RtBeam.Builder().setName("Neck Top").setNumber(5).createRtBeam()
            );

            // Reordering does not trigger massive reloading
            savedPlan.getFractionGroups().get(1).getBeams().sort(comparingInt(RtBeam::getNumber).reversed());
        });

        // Add transient fraction group wit a single beam
        doInJPA(factorySupplier, em -> {
            RtPlan savedPlan = em.find(RtPlan.class, savedPlanId);

            RtFractionGroup thirdGroup = savedPlan.addFractionGroup(
                new RtFractionGroup.Builder().setName("RadRx Torso").setNumber(3).createFractionGroup()
            );
            thirdGroup.addBeam(new RtBeam.Builder().setName("Torso Right").setNumber(6).createRtBeam());
            thirdGroup.addBeam(new RtBeam.Builder().setName("Torso Left").setNumber(7).createRtBeam());

            em.merge(savedPlan);
        });

        // Explore dynamic query building
        doInJPA(factorySupplier, em -> {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<RtFractionGroup> queryTree = builder.createQuery(RtFractionGroup.class);
            Root<RtFractionGroup> root = queryTree.from(RtFractionGroup.class);
            ListJoin<RtFractionGroup, RtBeam> join = root.join(RtFractionGroup_.beams);
            join.on(builder.equal(join.get(RtBeam_.fractionGroup).get(RtFractionGroup_.id), root.get(RtFractionGroup_.id)));

            // https://stackoverflow.com/a/36869943/1010666
            ParameterExpression<Long> fgId = builder.parameter(Long.class);
            queryTree.where(builder.equal(root.get(RtFractionGroup_.id), fgId));

            RtFractionGroup loadedFg = em
                .createQuery(queryTree)
                .setParameter(fgId, savedFractionGroupId)
                .getSingleResult();
            System.out.println("I've just loaded FG: " + loadedFg.getName() + " of plan ID: " + loadedFg.getPlan().getId());
        });


        // Delete a fraction group - beam deletion triggered in cascade
        doInJPA(factorySupplier, em -> {
            RtPlan savedPlan = em.find(RtPlan.class, savedPlanId);
            savedPlan.removeFractionGroup(savedPlan.getFractionGroups().get(1));

            em.merge(savedPlan);
        });
    }
}
