package se.umu.cs.ads.sp.view.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

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

    public static ArrayList<BufferedImage> loadMultipleImages(String folderPath, String imageName, int amount) {
        ArrayList<BufferedImage> images = new ArrayList<>();
        for (int i = 1; i <= amount; i++) {
            images.add(ImageLoader.loadImage(folderPath + "/" + imageName + i + ".png"));
        }
        return images;
    }
}
