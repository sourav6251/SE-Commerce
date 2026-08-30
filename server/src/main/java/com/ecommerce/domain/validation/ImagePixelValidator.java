package com.ecommerce.domain.validation;

import com.ecommerce.domain.exception.ImagePixelLimitException;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class ImagePixelValidator {

    private static final long MAX_PIXELS = 50_000_000L;

    /*
     * Maximum file size.
     *
     * Example: 20 MB
     */
    private static final long MAX_FILE_SIZE = 20L * 1024 * 1024;

    /*
     * Extensions allowed for product images.
     */
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "jpg",
            "jpeg",
            "png",
            "webp",
            "gif",
            "bmp",
            "tif",
            "tiff",
            "avif",
            "heic",
            "heif"
    );

    /*
     * Formats that ImageIO can actually decode.
     *
     * Additional formats can appear here when
     * appropriate ImageIO plugins are installed.
     */
    private static final Map<String, Set<String>> FORMAT_EXTENSIONS = Map.of(
            "JPEG", Set.of("jpg", "jpeg"),
            "PNG", Set.of("png"),
            "WEBP", Set.of("webp"),
            "GIF", Set.of("gif"),
            "BMP", Set.of("bmp"),
            "TIFF", Set.of("tif", "tiff"),
            "AVIF", Set.of("avif"),
            "HEIC", Set.of("heic"),
            "HEIF", Set.of("heif")
    );

    private ImagePixelValidator() {
    }

    public static void validate(MultipartFile file) {

        validateBasicFile(file);

        String filename = file.getOriginalFilename();

        String extension = getExtension(filename);

        /*
         * 1. Check extension.
         */
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new ImagePixelLimitException(
                    "Unsupported image extension: ." + extension
            );
        }

        /*
         * 2. Detect actual file format.
         */
        try (
                InputStream inputStream = file.getInputStream();
                ImageInputStream imageInputStream =
                        ImageIO.createImageInputStream(inputStream)
        ) {

            if (imageInputStream == null) {
                throw new ImagePixelLimitException(
                        "Unable to read image"
                );
            }

            Iterator<ImageReader> readers =
                    ImageIO.getImageReaders(imageInputStream);

            if (!readers.hasNext()) {
                throw new ImagePixelLimitException(
                        "Invalid or unsupported image format"
                );
            }

            ImageReader reader = readers.next();

            try {

                String actualFormat =
                        reader.getFormatName()
                                .toUpperCase(Locale.ROOT);

                /*
                 * 3. Check actual format.
                 */
                if (!FORMAT_EXTENSIONS.containsKey(actualFormat)) {
                    throw new ImagePixelLimitException(
                            "Unsupported image format: " +
                                    actualFormat
                    );
                }

                /*
                 * 4. Verify extension matches
                 *    actual file format.
                 */
                Set<String> validExtensions =
                        FORMAT_EXTENSIONS.get(actualFormat);

                if (!validExtensions.contains(extension)) {

                    throw new ImagePixelLimitException(
                            "File extension does not match " +
                                    "actual image format. " +
                                    "Extension: ." + extension +
                                    ", Actual format: " +
                                    actualFormat
                    );
                }

                /*
                 * 5. Decode the image.
                 */
                reader.setInput(
                        imageInputStream,
                        true,
                        true
                );

                BufferedImage image = reader.read(0);

                if (image == null) {
                    throw new ImagePixelLimitException(
                            "Invalid or corrupted image"
                    );
                }

                /*
                 * 6. Pixel flood protection.
                 */
                long width = image.getWidth();
                long height = image.getHeight();

                long totalPixels =
                        Math.multiplyExact(width, height);

                if (totalPixels > MAX_PIXELS) {

                    throw new ImagePixelLimitException(
                            "Image exceeds maximum pixel limit. " +
                                    "Maximum allowed: " +
                                    MAX_PIXELS +
                                    ", Actual: " +
                                    totalPixels
                    );
                }

            } finally {
                reader.dispose();
            }

        } catch (ImagePixelLimitException e) {

            throw e;

        } catch (ArithmeticException e) {

            throw new ImagePixelLimitException(
                    "Image dimensions are invalid"
            );

        } catch (IOException e) {

            throw new ImagePixelLimitException(
                    "Unable to read or decode image"
            );
        }
    }

    private static void validateBasicFile(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new ImagePixelLimitException(
                    "Image file is empty"
            );
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ImagePixelLimitException(
                    "Image file is too large. " +
                            "Maximum allowed: " +
                            (MAX_FILE_SIZE / 1024 / 1024) +
                            " MB"
            );
        }

        String filename = file.getOriginalFilename();

        if (filename == null || filename.isBlank()) {
            throw new ImagePixelLimitException(
                    "Image filename is missing"
            );
        }
    }

    private static String getExtension(String filename) {

        int lastDot = filename.lastIndexOf('.');

        if (lastDot == -1 ||
                lastDot == filename.length() - 1) {

            throw new ImagePixelLimitException(
                    "Image file must have a valid extension"
            );
        }

        return filename
                .substring(lastDot + 1)
                .toLowerCase(Locale.ROOT);
    }
}