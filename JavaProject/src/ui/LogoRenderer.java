package src.ui;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Utility class to render the JianoFreo logo
 */
public class LogoRenderer {
    public static BufferedImage createLogo(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        
        // Set rendering hints
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // Draw background circle
        g2d.setColor(new Color(100, 80, 180, 255));
        g2d.fillRoundRect(0, 0, width, height, 10, 10);
        
        // Draw GitHub-like icon background
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, (int)(height * 0.6)));
        String text = "JF";
        FontMetrics fm = g2d.getFontMetrics();
        int x = (width - fm.stringWidth(text)) / 2;
        int y = ((height - fm.getHeight()) / 2) + fm.getAscent();
        g2d.drawString(text, x, y);
        
        g2d.dispose();
        return image;
    }
}
