package io.licitat.hiberplay.plays;

import com.google.common.base.Supplier;
import io.licitat.hiberplay.model.Post;
import io.licitat.hiberplay.model.Tag;

import javax.persistence.EntityManagerFactory;

import static org.hibernate.testing.transaction.TransactionUtil.doInJPA;

public class SaveLinkAndDelete implements Runnable {

    private Supplier<EntityManagerFactory> factorySupplier;

    public SaveLinkAndDelete(Supplier<EntityManagerFactory> factorySupplier) {
        this.factorySupplier = factorySupplier;
    }

    @Override
    public void run() {
        doInJPA(factorySupplier, em -> {
            for (int i = 0; i < 3; i++) {
                Post aPost = new Post("This is a post with number " + (i + i));
                em.persist(aPost);
            }

            for (long i = 0; i < 3; i++) {
                Tag aTag = new Tag("This is a tag: " + i);
                aTag.tagPost(em.getReference(Post.class, i + 1));
                em.persist(aTag);
            }
        });

        doInJPA(factorySupplier, em -> {
            Post toRemove = em.find(Post.class, 1L);
            em.remove(toRemove);
        });
    }
}
