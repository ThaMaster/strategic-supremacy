package se.umu.cs.ads.sp.view.util;

import se.umu.cs.ads.sp.util.enums.UnitType;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import javax.imageio.ImageIO;

public class ImageLoader {

    private static HashMap<String, BufferedImage> loadedImages = new HashMap<>();
    private static HashMap<String, String> loadedImagePaths = new HashMap<>();

    public static BufferedImage loadImage(String path) {
        if (loadedImages.containsKey(path)) {
            return loadedImages.get(path);
        }
        try {
            InputStream inputStream = ImageLoader.class.getResourceAsStream(path);
            if (inputStream != null) {
                BufferedImage bi = ImageIO.read(inputStream);

                loadedImages.put(path, bi);
                int lastSeparatorIndex = path.lastIndexOf('/');
                loadedImagePaths.put(path.substring(lastSeparatorIndex + 1), path);
                return bi;
            } else {
                System.err.println("[ImageLoader] Error: Image not found: " + path);
                return null;
            }
        } catch (IOException e) {
            System.err.println("[ImageLoader] Error: Failed to load image: " + path);
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<BufferedImage> loadImages(String folderPath, String imageName, int amount) {
        ArrayList<BufferedImage> images = new ArrayList<>();
        for (int i = 1; i <= amount; i++) {
            images.add(ImageLoader.loadImage(folderPath + "/" + imageName + i + ".png"));
        }
        return images;
    }

    public static BufferedImage loadUnitIcon(UnitType unitType) {
        String path = "";
        switch(unitType) {
            case GUNNER -> path = "/sprites/entities/units/basic/idle/idle1.png";
        }
        return Objects.requireNonNull(loadImage(path));
    }
}
