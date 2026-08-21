package org.droneshow.media_service.controller;

import org.droneshow.media_service.dto.MediaResponse;
import org.droneshow.media_service.service.MediaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/media")
public class MediaController {

    private final MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @GetMapping({"", "/"})
    public ResponseEntity<List<MediaResponse>> getMedia(
            @RequestParam(required = false) String eventType) {

        List<MediaResponse> media;

        if (eventType == null || eventType.isBlank()) {
            media = mediaService.getAllMedia();
        } else {
            media = mediaService.getMediaByEventType(eventType);
        }

        return ResponseEntity.ok(media);
    }
    @GetMapping("/file/{fileName:.+}")
    public ResponseEntity<byte[]> getFile(
            @PathVariable String fileName) {

        return mediaService.getFile(fileName);
    }
}
