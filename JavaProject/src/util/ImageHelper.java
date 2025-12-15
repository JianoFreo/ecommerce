package src.util;

import java.io.File;

public class ImageHelper {
    
    /**
     * Resolves image path to absolute path that works from any execution context
     * Handles both relative paths (images/filename.png) and absolute paths
     */
    public static String getAbsolutePath(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return null;
        }
        
        // If already absolute, return as-is
        File f = new File(imagePath);
        if (f.isAbsolute() && f.exists()) {
            return imagePath;
        }
        
        // Clean up the path - remove "images/" prefix if present
        String cleanPath = imagePath.startsWith("images/") ? imagePath.substring(7) : imagePath;
        
        // Strategy: Search for images folder in common locations
        
        // 1. Check current working directory
        File currentDir = new File(System.getProperty("user.dir"));
        File imagesFolder = new File(currentDir, "images");
        if (imagesFolder.exists()) {
            File imageFile = new File(imagesFolder, cleanPath);
            if (imageFile.exists()) {
                return imageFile.getAbsolutePath();
            }
        }
        
        // 2. Check parent directory
        File parentDir = currentDir.getParentFile();
        if (parentDir != null) {
            imagesFolder = new File(parentDir, "images");
            if (imagesFolder.exists()) {
                File imageFile = new File(imagesFolder, cleanPath);
                if (imageFile.exists()) {
                    return imageFile.getAbsolutePath();
                }
            }
        }
        
        // 3. Check JavaProject/images (common structure)
        File javaProjectImages = new File(currentDir, "JavaProject/images");
        if (javaProjectImages.exists()) {
            File imageFile = new File(javaProjectImages, cleanPath);
            if (imageFile.exists()) {
                return imageFile.getAbsolutePath();
            }
        }
        
        // 4. Search up directory tree for "images" folder
        File searchDir = currentDir;
        while (searchDir != null) {
            imagesFolder = new File(searchDir, "images");
            if (imagesFolder.exists()) {
                File imageFile = new File(imagesFolder, cleanPath);
                if (imageFile.exists()) {
                    return imageFile.getAbsolutePath();
                }
            }
            searchDir = searchDir.getParentFile();
        }
        
        // 5. Fallback: return original path (may fail, but at least try)
        return imagePath;
    }
    
    /**
     * Check if image file exists using absolute path resolution
     */
    public static boolean imageExists(String imagePath) {
        String absolutePath = getAbsolutePath(imagePath);
        return new File(absolutePath).exists();
    }
}
