package com.example.monicio.Services;

import com.example.monicio.Models.Media;
import com.example.monicio.Repositories.MediaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * The type Media service.
 */
@Service
public class MediaService {

    /**
     * The Media repository.
     */
    @Autowired
    private MediaRepository mediaRepository;

    /**
     * Find media by id optional.
     *
     * @param id the id
     * @return the optional
     */
    public Optional<Media> findMediaById(Long id) {
        return mediaRepository.findMediaById(id);
    }
}