package io.licitat.hiberplay.plays;

import com.google.common.base.Supplier;
import io.licitat.hiberplay.model.RtFractionGroup;
import io.licitat.hiberplay.model.RtBeam;
import io.licitat.hiberplay.model.RtFractionGroup_;
import io.licitat.hiberplay.model.RtPlan;

import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;


import static java.util.Comparator.comparingInt;
import static org.hibernate.testing.transaction.TransactionUtil.doInJPA;


public class RtPlanAdventures implements Runnable {
    private final Supplier<EntityManagerFactory> factorySupplier;

    public RtPlanAdventures(Supplier<EntityManagerFactory> factorySupplier) {
        this.factorySupplier = factorySupplier;
    }

    @Override
    public void run() {

        Long savedFractionGroupId = doInJPA(factorySupplier, em -> {
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

            em.persist(aPlan);

            return firstGroup.getId();
        });

        Long savedPlanId = doInJPA(factorySupplier, em -> {
            RtFractionGroup loadedFg = em.find(RtFractionGroup.class, savedFractionGroupId);

            // Let's see if this triggers a load
            System.out.println("The plan id of loaded FG is: " + loadedFg.getPlan().getId());

            // This should trigger a load
            System.out.println("The patient id of loaded FG is: " + loadedFg.getPlan().getPatientId());

            return loadedFg.getPlan().getId();
        });

        doInJPA(factorySupplier, em -> {
            RtPlan savedPlan = em.find(RtPlan.class, savedPlanId);

            savedPlan.getFractionGroups().get(1).addBeam(
                new RtBeam.Builder().setName("Neck Top").setNumber(5).createRtBeam()
            );

            savedPlan.getFractionGroups().get(1).getBeams().sort(comparingInt(RtBeam::getNumber).reversed());
        });

        doInJPA(factorySupplier, em -> {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<RtFractionGroup> queryTree = builder.createQuery(RtFractionGroup.class);
            Root<RtFractionGroup> root = queryTree.from(RtFractionGroup.class);

            queryTree.where(builder.equal(root.get(RtFractionGroup_.ID), savedFractionGroupId));

            RtFractionGroup loadedFg = em.createQuery(queryTree).getSingleResult();
            System.out.println("I've just loaded FG: " + loadedFg.getName() + " of plan ID: " + loadedFg.getPlan().getId());
        });


        doInJPA(factorySupplier, em -> {
            RtPlan savedPlan = em.find(RtPlan.class, savedPlanId);
            savedPlan.removeFractionGroup(savedPlan.getFractionGroups().get(1));

            em.merge(savedPlan);
        });

    }
}
