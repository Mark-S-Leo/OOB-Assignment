package util;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageUtil {
    
    /**
     * Create a circular profile image from a file path
     * @param imagePath Path to the image file
     * @param size Diameter of the circular image
     * @return ImageIcon with circular image, or default silhouette if loading fails
     */
    public static ImageIcon createCircularProfileImage(String imagePath, int size) {
        if (imagePath == null || imagePath.isEmpty() || imagePath.equals("default")) {
            return createDefaultProfileImage(size);
        }
        
        try {
            File imageFile = new File(imagePath);
            if (!imageFile.exists()) {
                return createDefaultProfileImage(size);
            }
            
            BufferedImage originalImage = ImageIO.read(imageFile);
            if (originalImage == null) {
                return createDefaultProfileImage(size);
            }
            
            // Resize image to square
            BufferedImage squareImage = resizeToSquare(originalImage, size);
            
            // Create circular mask
            BufferedImage circularImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = circularImage.createGraphics();
            
            // Enable anti-aliasing for smooth edges
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Create circular clip
            Ellipse2D.Float circle = new Ellipse2D.Float(0, 0, size, size);
            g2.setClip(circle);
            
            // Draw the image
            g2.drawImage(squareImage, 0, 0, null);
            g2.dispose();
            
            return new ImageIcon(circularImage);
            
        } catch (IOException e) {
            System.err.println("Error loading profile image: " + e.getMessage());
            return createDefaultProfileImage(size);
        }
    }
    
    /**
     * Create a default silhouette profile image (gray circle with user icon)
     * @param size Diameter of the circular image
     * @return ImageIcon with default profile silhouette
     */
    public static ImageIcon createDefaultProfileImage(int size) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        
        // Enable anti-aliasing
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw gray circle background
        g2.setColor(new Color(189, 195, 199)); // Light gray
        g2.fillOval(0, 0, size, size);
        
        // Draw silhouette (head and shoulders)
        g2.setColor(new Color(236, 240, 241)); // Very light gray for icon
        
        // Head (circle)
        int headSize = size / 3;
        int headX = (size - headSize) / 2;
        int headY = size / 4;
        g2.fillOval(headX, headY, headSize, headSize);
        
        // Shoulders (ellipse bottom)
        int shoulderWidth = (int)(size * 0.65);
        int shoulderHeight = (int)(size * 0.5);
        int shoulderX = (size - shoulderWidth) / 2;
        int shoulderY = (int)(size * 0.55);
        g2.fillOval(shoulderX, shoulderY, shoulderWidth, shoulderHeight);
        
        g2.dispose();
        
        return new ImageIcon(image);
    }
    
    /**
     * Resize image to a square while maintaining aspect ratio (crop if needed)
     * @param image Original image
     * @param targetSize Target size for the square
     * @return Resized square image
     */
    private static BufferedImage resizeToSquare(BufferedImage image, int targetSize) {
        int width = image.getWidth();
        int height = image.getHeight();
        
        // Determine crop dimensions to make it square
        int cropSize = Math.min(width, height);
        int cropX = (width - cropSize) / 2;
        int cropY = (height - cropSize) / 2;
        
        // Crop to square
        BufferedImage croppedImage = image.getSubimage(cropX, cropY, cropSize, cropSize);
        
        // Resize to target size
        BufferedImage resizedImage = new BufferedImage(targetSize, targetSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImage.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(croppedImage, 0, 0, targetSize, targetSize, null);
        g2.dispose();
        
        return resizedImage;
    }
    
    /**
     * Validate if a file is a valid image file
     * @param file File to validate
     * @return true if file is .jpg, .jpeg, or .png
     */
    public static boolean isValidImageFile(File file) {
        if (file == null || !file.exists() || !file.isFile()) {
            return false;
        }
        
        String name = file.getName().toLowerCase();
        return name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png");
    }
    
    /**
     * Create a small circular profile image for header
     * @param imagePath Path to the image file
     * @return ImageIcon sized for header (50x50)
     */
    public static ImageIcon createHeaderProfileImage(String imagePath) {
        return createCircularProfileImage(imagePath, 50);
    }
    
    /**
     * Create a large circular profile image for profile dialog
     * @param imagePath Path to the image file
     * @return ImageIcon sized for dialog (100x100)
     */
    public static ImageIcon createDialogProfileImage(String imagePath) {
        return createCircularProfileImage(imagePath, 100);
    }
}
