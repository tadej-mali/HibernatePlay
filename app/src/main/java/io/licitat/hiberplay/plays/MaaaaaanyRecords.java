package io.licitat.hiberplay.plays;

import com.google.common.base.Supplier;
import io.licitat.hiberplay.model.Post;

import javax.persistence.EntityManagerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.hibernate.testing.transaction.TransactionUtil.doInJPA;

public class MaaaaaanyRecords implements Runnable {

    private static final String FILL_TEST_DATA = "DO\n" +
        "$do$\n" +
        "declare\n" +
        "  tag_id int8;\n" +
        "  post_id int8;\n" +
        "BEGIN \n" +
        "  FOR i IN 1..100000 loop \t \n" +
        "    tag_id = nextval('hibernate_sequence');\n" +
        "    post_id = nextval('hibernate_sequence');\n" +
        "    INSERT INTO Tag (id, name) values (tag_id, 'Tag #' || i);\n" +
        "    INSERT INTO Post (id, title) values (post_id, 'Title #' || i);\n" +
        "    INSERT INTO Post_Tag (post_id, tag_id) values (post_id, tag_id);\n" +
        "  END LOOP;\n" +
        "END\n" +
        "$do$;";

    private static final String GET_POSTS = "SELECT p FROM Post p JOIN p.tags t WHERE :from < t.id AND t.id < :to";

    private Supplier<EntityManagerFactory> factorySupplier;

    public MaaaaaanyRecords(Supplier<EntityManagerFactory> factorySupplier) {
        this.factorySupplier = factorySupplier;
    }

    @Override
    public void run() {
        Instant startFilling = Instant.now();
        doInJPA(factorySupplier, em -> {
            em.createNativeQuery(FILL_TEST_DATA).executeUpdate();
        });
        Instant endFilling = Instant.now();

        Duration timeElapsedFilling = Duration.between(startFilling, endFilling);
        System.out.println("Filled in " + timeElapsedFilling.toMillis() + "ms");

        Instant startSelecting = Instant.now();
        List<Post> _postList = doInJPA(factorySupplier, em -> {
            return em.createQuery(GET_POSTS, Post.class)
                .setParameter("from", 20_001L)
                .setParameter("to", 25_001L)
                .getResultList();
        });
        Instant endSelecting = Instant.now();

        Duration timeElapsedSelecting = Duration.between(startSelecting, endSelecting);
        System.out.println("Selected " + _postList.size() + " records in " + timeElapsedSelecting.toMillis() + "ms");

        //_postList.forEach(p -> System.out.println(String.format("Post.id: %d, %s", p.getId(), p.getTitle())));
    }
}
