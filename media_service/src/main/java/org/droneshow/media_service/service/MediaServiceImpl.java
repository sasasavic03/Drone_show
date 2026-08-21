package org.droneshow.media_service.service;

import io.minio.GetObjectArgs;
import io.minio.GetObjectTagsArgs;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.messages.Item;
import io.minio.messages.Tags;

import org.droneshow.media_service.dto.MediaResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class MediaServiceImpl implements MediaService {

    private final MinioClient minioClient;

    @Value("${minio.bucket.posts}")
    private String postsBucket;

    public MediaServiceImpl(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @Override
    public List<MediaResponse> getAllMedia() {
        return findMedia(null);
    }

    @Override
    public List<MediaResponse> getMediaByEventType(String eventType) {
        return findMedia(eventType);
    }

    @Override
    public ResponseEntity<byte[]> getFile(String fileName) {

        try {

            // Get file from MinIO
            try (InputStream inputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(postsBucket)
                            .object(fileName)
                            .build()
            )) {

                byte[] fileBytes = inputStream.readAllBytes();

                MediaType mediaType = getMediaType(fileName);

                return ResponseEntity.ok()
                        .contentType(mediaType)
                        .contentLength(fileBytes.length)
                        .header(
                                HttpHeaders.CONTENT_DISPOSITION,
                                "inline; filename=\"" + fileName + "\""
                        )
                        .body(fileBytes);
            }

        } catch (Exception e) {

            System.err.println(
                    "Failed to get file from MinIO: "
                            + fileName
                            + " - "
                            + e.getMessage()
            );

            return ResponseEntity.notFound().build();
        }
    }

    private List<MediaResponse> findMedia(String eventType) {

        List<MediaResponse> result = new ArrayList<>();

        Iterable<Result<Item>> objects = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(postsBucket)
                        .recursive(true)
                        .build()
        );

        for (Result<Item> resultItem : objects) {

            try {

                Item item = resultItem.get();

                if (item.isDir()) {
                    continue;
                }

                String objectName = item.objectName();

                // Only images
                if (!isImage(objectName)) {
                    continue;
                }

                String objectEventType = getEventType(objectName);

                // Filter by event type
                if (eventType != null) {

                    if (objectEventType == null) {
                        continue;
                    }

                    if (!eventType.equalsIgnoreCase(objectEventType)) {
                        continue;
                    }
                }

                /*
                 * IMPORTANT:
                 *
                 * Do NOT return the MinIO URL.
                 *
                 * Return URL of OUR backend.
                 */
                String url = "/media/file/" + objectName;

                result.add(
                        MediaResponse.builder()
                                .fileName(objectName)
                                .url(url)
                                .eventType(objectEventType)
                                .build()
                );

            } catch (Exception e) {

                System.err.println(
                        "Failed to process MinIO object: "
                                + e.getMessage()
                );
            }
        }

        return result;
    }

    private String getEventType(String objectName) {

        try {

            Tags tags = minioClient.getObjectTags(
                    GetObjectTagsArgs.builder()
                            .bucket(postsBucket)
                            .object(objectName)
                            .build()
            );

            Map<String, String> tagMap = tags.get();

            return tagMap.get("eventType");

        } catch (Exception e) {

            System.err.println(
                    "Could not read tags for "
                            + objectName
                            + ": "
                            + e.getMessage()
            );

            return null;
        }
    }

    private boolean isImage(String objectName) {

        String lower =
                objectName.toLowerCase(Locale.ROOT);

        return lower.endsWith(".jpg")
                || lower.endsWith(".jpeg")
                || lower.endsWith(".png")
                || lower.endsWith(".webp")
                || lower.endsWith(".gif");
    }

    private MediaType getMediaType(String fileName) {

        String lower =
                fileName.toLowerCase(Locale.ROOT);

        if (lower.endsWith(".png")) {
            return MediaType.IMAGE_PNG;
        }

        if (lower.endsWith(".jpg")
                || lower.endsWith(".jpeg")) {
            return MediaType.IMAGE_JPEG;
        }

        if (lower.endsWith(".gif")) {
            return MediaType.IMAGE_GIF;
        }

        if (lower.endsWith(".webp")) {
            return MediaType.parseMediaType("image/webp");
        }

        return MediaType.APPLICATION_OCTET_STREAM;
    }
}
