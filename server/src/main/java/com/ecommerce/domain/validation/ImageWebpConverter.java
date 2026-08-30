package com.ecommerce.domain.validation;

import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

public final class ImageWebpConverter {

    private ImageWebpConverter() {
        // Utility class
    }

    public static byte[] convert(MultipartFile file) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Image file is empty");
        }

        /*
         * Read the actual image bytes.
         *
         * This decodes JPG/PNG/etc. into a BufferedImage.
         */
        BufferedImage image = ImageIO.read(file.getInputStream());

        if (image == null) {
            throw new IllegalArgumentException(
                    "File is not a supported image"
            );
        }

        /*
         * Find a WebP ImageIO writer.
         */
        Iterator<ImageWriter> writers =
                ImageIO.getImageWritersByFormatName("webp");

        if (!writers.hasNext()) {
            throw new IllegalStateException(
                    "No WebP ImageIO writer found"
            );
        }

        ImageWriter writer = writers.next();

        try (ByteArrayOutputStream outputStream =
                     new ByteArrayOutputStream();
             ImageOutputStream imageOutputStream =
                     ImageIO.createImageOutputStream(outputStream)) {

            writer.setOutput(imageOutputStream);

            /*
             * Configure WebP quality.
             *
             * 0.0 = lowest quality
             * 1.0 = highest quality
             */
            ImageWriteParam writeParam =
                    writer.getDefaultWriteParam();

            if (writeParam.canWriteCompressed()) {
                writeParam.setCompressionMode(
                        ImageWriteParam.MODE_EXPLICIT
                );

                writeParam.setCompressionQuality(0.85f);
            }

            writer.write(
                    null,
                    new IIOImage(image, null, null),
                    writeParam
            );

            imageOutputStream.flush();

            return outputStream.toByteArray();

        } finally {
            writer.dispose();
        }
    }
}