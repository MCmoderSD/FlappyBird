package de.MCmoderSD.utilities.image;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

@SuppressWarnings("unused")
public class ImageStreamer extends ImageUtility {

    // Default Constructor
    public ImageStreamer() {
        isAbsolute = false;
        bufferedImageCache = new HashMap<>();
        imageIconCache = new HashMap<>();
        url = null;
    }

    // Constructor with URL
    public ImageStreamer(String url) {
        isAbsolute = false;
        bufferedImageCache = new HashMap<>();
        imageIconCache = new HashMap<>();
        this.url = url;
    }

    @Override
    public BufferedImage read(String url) {
        if (!url.endsWith(".png") && !url.endsWith(".jpg") && !url.endsWith(".jpeg") && !url.endsWith(".gif"))
            throw new IllegalArgumentException("Unsupported image format: " + url); // Image format is not supported
        if (bufferedImageCache.containsKey(url))
            return bufferedImageCache.get(url); // Checks the cache for the image

        BufferedImage image = null;

        try {
            if (this.url != null && !url.contains(this.url)) image = ImageIO.read(new URL(this.url + url));
            else image = ImageIO.read(new URL(url));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        // Null check
        if (image == null)
            throw new IllegalArgumentException("The image could not be loaded: " + url); // Image could not be loaded (Image is null

        // Add to cache
        bufferedImageCache.put(url, image);
        return image;
    }

    public BufferedImage read(String url, String resource) {
        if (!resource.endsWith(".png") && !resource.endsWith(".jpg") && !resource.endsWith(".jpeg") && !resource.endsWith(".gif"))
            throw new IllegalArgumentException("Unsupported image format: " + resource); // Image format is not supported
        if (bufferedImageCache.containsKey(resource))
            return bufferedImageCache.get(resource); // Checks the cache for the image

        BufferedImage image = null;

        try {
            image = ImageIO.read(new URL(url + resource));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        // Null check
        if (image == null)
            throw new IllegalArgumentException("The image could not be loaded: " + url + resource); // Image could not be loaded (Image is null

        // Add to cache
        bufferedImageCache.put(resource, image);
        return image;
    }

    @Override
    public ImageIcon readGif(String url) {
        if (!url.endsWith(".gif"))
            throw new IllegalArgumentException("Unsupported image format: " + url); // Animation format is not supported
        if (imageIconCache.containsKey(url))
            return imageIconCache.get(url); // Checks the cache for the Animation

        ImageIcon imageIcon; // Load the Animation

        try {
            if (this.url != null && !url.contains(this.url)) imageIcon = new ImageIcon(new URL(this.url + url));
            else imageIcon = new ImageIcon(new URL(url));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        imageIconCache.put(url, imageIcon);
        return imageIcon;
    }

    public ImageIcon readGif(String url, String resource) {
        if (!resource.endsWith(".gif"))
            throw new IllegalArgumentException("Unsupported image format: " + url + resource); // Animation format is not supported
        if (imageIconCache.containsKey(resource))
            return imageIconCache.get(resource); // Checks the cache for the Animation

        ImageIcon imageIcon; // Load the Animation

        try {
            imageIcon = new ImageIcon(new URL(url + resource));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        imageIconCache.put(resource, imageIcon);
        return imageIcon;
    }
}