package com.example.monicio.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * Task entity.
 *
 * @author HukoJlauII
 */
@Table(name = "task")
@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    /**
     * The Id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * The Name.
     */
    @Column(name = "name")
    @NotBlank(message = "Поле не должно быть пустым")
    private String name;
    /**
     * The Description.
     */
    @Column(name = "description")
    @NotBlank(message = "Поле не должно быть пустым")
    private String description;

    /**
     * The Creation date.
     */
    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    /**
     * The Complete date.
     */
    @Column(name = "complete_date")
    private LocalDateTime completeDate;

    /**
     * The Implementer.
     */
    @OneToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "implementer_id", referencedColumnName = "id")
    @JsonIgnore
    private User implementer;
    /**
     * The Status.
     */
    @Column(name = "status")
    private String status;

}
