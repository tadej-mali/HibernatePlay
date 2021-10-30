package io.licitat.hiberplay.model;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.*;

@Audited
@Entity(name = "Tag")
@Table(name = "tag")
public class Tag {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    // https://vladmihalcea.com/the-best-way-to-use-the-manytomany-annotation-with-jpa-and-hibernate/
    @ManyToMany(mappedBy = "tags")
    /*
    This does not work - it even causes exception on creating new Post-s (duplicate entry inside the link table)
    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "post_tag",
        joinColumns = @JoinColumn(name = "tag_id"),
        inverseJoinColumns = @JoinColumn(name = "post_id")
    )
   */
    private Set<Post> posts = new HashSet<>();

    public Tag() {}

    public Tag(String name) {
        this.name = name;
    }

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

    public Set<Post> getPosts() {
        return posts;
    }

    public void setPosts(Set<Post> posts) {
        this.posts = posts;
    }

    public void tagPost(Post post) {
        posts.add(post);
        post.getTags().add(this);
    }

    public void removeTag(Post post) {
        posts.remove(post);
        post.getTags().remove(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return Objects.equals(name, tag.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}