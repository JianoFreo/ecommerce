package src.util;

import java.io.File;

public class ImageHelper {
    
    /**
     * Check if image file exists (handles absolute paths from database)
     */
    public static boolean imageExists(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return false;
        }
        return new File(imagePath).exists();
    }
    
    /**
     * Return absolute path as-is (database stores absolute paths)
     */
    public static String getAbsolutePath(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return null;
        }
        return imagePath;
    }
}

