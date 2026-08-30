package com.ecommerce.domain.validation;

import com.ecommerce.domain.exception.VideoValidationException;
import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class VideoValidator {

    private static final long MAX_FILE_SIZE =
            100L * 1024 * 1024; // 100 MB

    private static final Tika TIKA = new Tika();

    /*
     * Allowed video MIME types.
     */
    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "video/mp4",
            "video/webm",
            "video/quicktime",
            "video/x-msvideo",
            "video/x-matroska",
            "video/x-m4v"
    );

    /*
     * MIME type -> allowed extensions.
     */
    private static final Map<String, Set<String>>
            MIME_EXTENSIONS = Map.of(

            "video/mp4",
            Set.of("mp4"),

            "video/webm",
            Set.of("webm"),

            "video/quicktime",
            Set.of("mov"),

            "video/x-msvideo",
            Set.of("avi"),

            "video/x-matroska",
            Set.of("mkv"),

            "video/x-m4v",
            Set.of("m4v")
    );

    private VideoValidator() {
    }

    public static void validate(
            MultipartFile file
    ) {

        validateBasicFile(file);

        String filename =
                file.getOriginalFilename();

        String extension =
                getExtension(filename);

        /*
         * 1. Check extension.
         */
        if (!isAllowedExtension(extension)) {

            throw new VideoValidationException(
                    "Unsupported video extension: ." +
                            extension
            );
        }

        try {

            /*
             * 2. Detect actual MIME type from
             *    file content.
             */
            String actualMimeType =
                    TIKA.detect(
                            file.getInputStream(),
                            filename
                    );

            /*
             * 3. Check actual MIME type.
             */
            if (!ALLOWED_MIME_TYPES
                    .contains(actualMimeType)) {

                throw new VideoValidationException(
                        "Invalid or unsupported video format: " +
                                actualMimeType
                );
            }

            /*
             * 4. Verify extension against
             *    actual MIME type.
             */
            validateExtensionMatchesMime(
                    extension,
                    actualMimeType
            );

        } catch (VideoValidationException e) {

            throw e;

        } catch (IOException e) {

            throw new VideoValidationException(
                    "Unable to read video"
            );
        }
    }

    private static void validateBasicFile(
            MultipartFile file
    ) {

        if (file == null || file.isEmpty()) {

            throw new VideoValidationException(
                    "Video file is empty"
            );
        }

        if (file.getSize() > MAX_FILE_SIZE) {

            throw new VideoValidationException(
                    "Video file is too large. " +
                            "Maximum allowed: 100 MB"
            );
        }

        if (file.getOriginalFilename() == null ||
                file.getOriginalFilename().isBlank()) {

            throw new VideoValidationException(
                    "Video filename is missing"
            );
        }
    }

    private static boolean isAllowedExtension(
            String extension
    ) {

        return MIME_EXTENSIONS
                .values()
                .stream()
                .anyMatch(
                        extensions ->
                                extensions.contains(extension)
                );
    }

    private static void validateExtensionMatchesMime(
            String extension,
            String mimeType
    ) {

        Set<String> extensions =
                MIME_EXTENSIONS.get(mimeType);

        if (extensions == null ||
                !extensions.contains(extension)) {

            throw new VideoValidationException(
                    "Video extension does not match " +
                            "actual video format. " +
                            "Extension: ." +
                            extension +
                            ", MIME: " +
                            mimeType
            );
        }
    }

    private static String getExtension(
            String filename
    ) {

        int lastDot =
                filename.lastIndexOf('.');

        if (lastDot == -1 ||
                lastDot == filename.length() - 1) {

            throw new VideoValidationException(
                    "Video must have a valid extension"
            );
        }

        return filename
                .substring(lastDot + 1)
                .toLowerCase(Locale.ROOT);
    }
}