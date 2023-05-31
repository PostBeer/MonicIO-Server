package com.example.monicio.Models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.*;
import java.util.*;

/**
 * Project entity.
 *
 * @author HukoJlauII
 */
@Table(name = "project")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class Project {
    /**
     * The Id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToMany(targetEntity = User.class, fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
    @RestResource(exported = false)
    @JsonIgnoreProperties({"projects", "avatar"})
    private Set<User> users = new HashSet<>();

    @ManyToMany(targetEntity = User.class, fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
    @JoinTable(
            name = "projects_requests",
            joinColumns = {@JoinColumn(name = "project_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")}
    )
    @RestResource(exported = false)
    @JsonIgnoreProperties({"projects", "avatar"})
    private Set<User> requests = new HashSet<>();

    @JoinColumn(name = "creator_id", referencedColumnName = "id")
    @OneToOne(targetEntity = User.class)
    @RestResource(exported = false)
    @JsonIgnoreProperties({"projects", "avatar"})
    private User creator;

    /**
     * The Title.
     */
    @Column(name = "title")
    private String title;

    /**
     * The Description.
     */
    @Column(name = "description")
    private String description;

    /**
     * The Status.
     */
    @Column(name = "status")
    private String status;

    /**
     * The Tasks.
     */
    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinTable(
            name = "project_task",
            joinColumns = {@JoinColumn(name = "project_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "task_id", referencedColumnName = "id")}
    )
    private List<Task> tasks = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return id.equals(project.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
