package io.licitat.hiberplay.plays;

import com.google.common.base.Supplier;
import io.licitat.hiberplay.model.Post;
import io.licitat.hiberplay.model.Tag;

import javax.persistence.EntityManagerFactory;

import java.util.ArrayList;

import static org.hibernate.testing.transaction.TransactionUtil.doInJPA;

public class SaveLinkAndDelete implements Runnable {

    private Supplier<EntityManagerFactory> factorySupplier;

    public SaveLinkAndDelete(Supplier<EntityManagerFactory> factorySupplier) {
        this.factorySupplier = factorySupplier;
    }

    @Override
    public void run() {
        Long firstPostId = doInJPA(factorySupplier, em -> {
            ArrayList<Post> posts = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                Post aPost = new Post("This is a post with number " + (i + i));
                em.persist(aPost);
                posts.add(aPost);
            }

            for (int i = 0; i < 3; i++) {
                Tag aTag = new Tag("This is a tag: " + i);
                aTag.tagPost(em.getReference(Post.class, posts.get(i).getId()));
                em.persist(aTag);
            }

            return posts.get(0).getId();
        });

        doInJPA(factorySupplier, em -> {
            Post toRemove = em.find(Post.class, firstPostId);
            em.remove(toRemove);
        });
    }
}
