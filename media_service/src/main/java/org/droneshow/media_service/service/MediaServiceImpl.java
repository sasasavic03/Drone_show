package org.droneshow.media_service.service;

import io.minio.GetObjectTagsArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.http.Method;
import io.minio.messages.Item;
import io.minio.messages.Tags;

import org.droneshow.media_service.dto.MediaResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class MediaServiceImpl implements MediaService {

    private final MinioClient minioClient;

    @Value("${minio.url}")
    private String minioUrl;

    @Value("${minio.public-url}")
    private String publicUrl;

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

                // Filter by event type when requested
                if (eventType != null && !eventType.isBlank()) {

                    if (objectEventType == null) {
                        continue;
                    }

                    if (!eventType.equalsIgnoreCase(objectEventType)) {
                        continue;
                    }
                }

                // Generate presigned URL using the INTERNAL MinIO address.
                String internalUrl = minioClient.getPresignedObjectUrl(
                        GetPresignedObjectUrlArgs.builder()
                                .method(Method.GET)
                                .bucket(postsBucket)
                                .object(objectName)
                                .expiry(60 * 60)
                                .build()
                );

                // Change only the hostname so the browser can access it.
                String publicImageUrl = internalUrl.replace(
                        minioUrl,
                        publicUrl
                );

                result.add(
                        MediaResponse.builder()
                                .fileName(objectName)
                                .url(publicImageUrl)
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

            String eventType = tagMap.get("eventType");

            if (eventType == null) {
                return null;
            }

            return eventType.toUpperCase(Locale.ROOT);

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

        String lower = objectName.toLowerCase(Locale.ROOT);

        return lower.endsWith(".jpg")
                || lower.endsWith(".jpeg")
                || lower.endsWith(".png")
                || lower.endsWith(".webp")
                || lower.endsWith(".gif");
    }
}
