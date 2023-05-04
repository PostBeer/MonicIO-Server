package com.example.monicio.Models;

import lombok.*;

import javax.persistence.*;
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
    private String name;
    /**
     * The Description.
     */
    @Column(name = "description")
    private String description;
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
    private User implementer;
    /**
     * The Status.
     */
    @Column(name = "status")
    private String status;

}
