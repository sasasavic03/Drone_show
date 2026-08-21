package org.droneshow.media_service.service;

import org.droneshow.media_service.dto.MediaResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface MediaService {

    List<MediaResponse> getAllMedia();

    List<MediaResponse> getMediaByEventType(String eventType);

    ResponseEntity<byte[]> getFile(String fileName);
}
