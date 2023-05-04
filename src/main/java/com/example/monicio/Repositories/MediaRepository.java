package com.example.monicio.Repositories;

import com.example.monicio.Models.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for {@link Media} entity.
 *
 * @author HukoJlauII
 */
@Repository
public interface MediaRepository extends JpaRepository<Media, Long> {

    /**
     * Find media by id.
     *
     * @param id the id
     * @return the media if it present
     */
    Optional<Media> findMediaById(Long id);
}