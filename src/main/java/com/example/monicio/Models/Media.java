package com.example.monicio.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

/**
 * Media entity.
 *
 * @author HukoJlauII
 */
@Entity
@Builder
@Table(name = "media")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Media {

    /**
     * The Id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * The Original file name.
     */
    @Column(name = "original_file_name")
    private String originalFileName;


    /**
     * The Size.
     */
    @Column(name = "size")
    private Long size;


    /**
     * The Media type.
     */
    @Column(name = "media_type")
    private String mediaType;

    /**
     * The Bytes.
     */
    @Lob
    @JsonIgnore
    private byte[] bytes;

}
