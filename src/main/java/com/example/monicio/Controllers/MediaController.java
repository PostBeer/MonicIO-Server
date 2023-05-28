package com.example.monicio.Controllers;

import com.example.monicio.Models.Media;
import com.example.monicio.Services.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;

/**
 * The type Media controller.
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/media")
public class MediaController {

    /**
     * The Media service.
     */
    @Autowired
    private MediaService mediaService;

    /**
     * Gets image by id.
     *
     * @param id the id
     * @return the image by id
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getImageById(@PathVariable Long id) {
        Media image = mediaService.findMediaById(id).orElse(null);
        if (image != null) return ResponseEntity.ok()
                .header("fileName", image.getOriginalFileName())
                .contentType(MediaType.valueOf(image.getMediaType()))
                .contentLength(image.getSize())
                .body(new InputStreamResource(new ByteArrayInputStream(image.getBytes())));
        else {
            return ResponseEntity.notFound().build();
        }
    }


}
