package com.thefa.audit.config.converter;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

@Component
public class ImageSizeConverter {

    public CompletableFuture<InputStream> resize(MultipartFile file, int size) {

        Image img = null;
        try {
            img = ImageIO.read(file.getInputStream());

            int imageHeight, imageWidth;
            imageHeight = ((BufferedImage) img).getHeight();
            imageWidth = ((BufferedImage) img).getWidth();
            BufferedImage tempPNG;

            if (imageWidth > imageHeight) {
                double aspectRatio = (double) img.getWidth(null) / (double) img.getHeight(null);
                tempPNG = resizeImage(img, size, (int) (size / aspectRatio));
            } else {
                double aspectRatio = (double) img.getHeight(null) / (double) img.getWidth(null);
                tempPNG = resizeImage(img, (int) (size / aspectRatio), size);
            }


            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(tempPNG, "png", byteArrayOutputStream);
            return CompletableFuture.completedFuture(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));


        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    public BufferedImage resizeImage(final Image image, int width, int height) {

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.setComposite(AlphaComposite.Src);
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

        graphics2D.drawImage(image, 0, 0, width, height, null);
        graphics2D.dispose();
        return bufferedImage;
    }
}
