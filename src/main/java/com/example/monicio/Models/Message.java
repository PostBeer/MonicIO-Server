package com.example.monicio.Models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Message entity.
 *
 * @author HukoJlauII
 */
@Entity
@Table(name = "message")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class Message {
    /**
     * The Id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * The Text.
     */
    @Column(name = "content")
    private String content;

    /**
     * The Send at.
     */
    @Column(name = "send_at")
    private LocalDateTime sendAt;

    /**
     * The Sender.
     */
    @RestResource(exported = false)
    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "sender_id", referencedColumnName = "id")
    private User sender;

    /**
     * The Project.
     */
    @OneToOne(targetEntity = Project.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", referencedColumnName = "id")
    private Project project;
}
