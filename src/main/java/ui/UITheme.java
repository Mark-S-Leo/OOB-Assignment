package ui;

import java.awt.Color;
import java.awt.Font;

/**
 * Centralized UI Theme for Student Consultation Management System
 * 
 * This class contains all color constants, font definitions, and spacing
 * values used across the application to ensure consistent styling.
 * 
 * Benefits:
 * - Single source of truth for all UI styling
 * - Easy to update theme across entire application
 * - Supports future theming capabilities (light/dark mode)
 * - Improves maintainability and consistency
 * 
 * Usage:
 * Import statically in your UI classes:
 * import static ui.UITheme.*;
 * 
 * Then use directly:
 * panel.setBackground(PRIMARY_COLOR);
 * label.setForeground(TEXT_COLOR);
 */
public class UITheme {
    
    // ==================== COLOR PALETTE ====================
    
    /**
     * Primary brand color - Steel Blue
     * Used for: Headers, primary buttons, accents
     */
    public static final Color PRIMARY_COLOR = new Color(70, 130, 180);
    
    /**
     * Primary color hover state - Darker Steel Blue
     * Used for: Button hover effects
     */
    public static final Color PRIMARY_HOVER = new Color(56, 104, 144);
    
    /**
     * Main background color - Pure White
     * Used for: Window backgrounds, main panels
     */
    public static final Color BACKGROUND_COLOR = Color.WHITE;
    
    /**
     * Card/Panel background - Light Gray
     * Used for: Cards, elevated panels, secondary backgrounds
     */
    public static final Color CARD_COLOR = new Color(248, 249, 250);
    
    /**
     * Primary text color - Dark Gray
     * Used for: Main text, labels, titles
     */
    public static final Color TEXT_COLOR = new Color(33, 37, 41);
    
    /**
     * Secondary text color - Medium Gray
     * Used for: Descriptions, hints, secondary text
     */
    public static final Color TEXT_SECONDARY = new Color(108, 117, 125);
    
    /**
     * Hover state color - Lighter Gray
     * Used for: Card hover effects, button hover backgrounds
     */
    public static final Color HOVER_COLOR = new Color(233, 236, 239);
    
    /**
     * Success color - Green
     * Used for: Success messages, confirm buttons, positive states
     */
    public static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    
    /**
     * Success hover state - Darker Green
     */
    public static final Color SUCCESS_HOVER = new Color(32, 134, 55);
    
    /**
     * Danger/Error color - Red
     * Used for: Error messages, delete buttons, warnings
     */
    public static final Color DANGER_COLOR = new Color(220, 53, 69);
    
    /**
     * Danger hover state - Darker Red
     */
    public static final Color DANGER_HOVER = new Color(176, 42, 55);
    
    /**
     * Warning color - Orange
     * Used for: Warning messages, caution states
     */
    public static final Color WARNING_COLOR = new Color(255, 193, 7);
    
    /**
     * Info color - Light Blue
     * Used for: Information messages, neutral states
     */
    public static final Color INFO_COLOR = new Color(23, 162, 184);
    
    /**
     * Border color - Light Gray
     * Used for: Card borders, input borders, dividers
     */
    public static final Color BORDER_COLOR = new Color(220, 220, 220);
    
    /**
     * Input border color - Medium Gray
     * Used for: Text field borders, dropdown borders
     */
    public static final Color INPUT_BORDER = new Color(200, 200, 200);
    
    /**
     * Disabled color - Light Gray
     * Used for: Disabled buttons, read-only fields
     */
    public static final Color DISABLED_COLOR = new Color(233, 236, 239);
    
    /**
     * Selection background - Light Steel Blue
     * Used for: Table row selection, list item selection
     */
    public static final Color SELECTION_COLOR = new Color(184, 207, 229);
    
    /**
     * Table grid color - Light Gray
     * Used for: Table grid lines
     */
    public static final Color GRID_COLOR = new Color(220, 220, 220);
    
    
    // ==================== FONT DEFINITIONS ====================
    
    /**
     * Font family used throughout the application
     */
    public static final String FONT_FAMILY = "Segoe UI";
    
    /**
     * Emoji font family for icons and profile pictures
     */
    public static final String EMOJI_FONT_FAMILY = "Segoe UI Emoji";
    
    /**
     * Large header font - 24pt Bold
     * Used for: Main page titles, large headers
     */
    public static final Font FONT_HEADER_LARGE = new Font(FONT_FAMILY, Font.BOLD, 24);
    
    /**
     * Medium header font - 20pt Bold
     * Used for: Section titles, dialog titles
     */
    public static final Font FONT_HEADER_MEDIUM = new Font(FONT_FAMILY, Font.BOLD, 20);
    
    /**
     * Small header font - 18pt Bold
     * Used for: Card titles, subsection headers
     */
    public static final Font FONT_HEADER_SMALL = new Font(FONT_FAMILY, Font.BOLD, 18);
    
    /**
     * Subheader font - 14pt Bold
     * Used for: Field labels, small headers
     */
    public static final Font FONT_SUBHEADER = new Font(FONT_FAMILY, Font.BOLD, 14);
    
    /**
     * Body text font - 14pt Regular
     * Used for: Main text, input fields, buttons
     */
    public static final Font FONT_BODY = new Font(FONT_FAMILY, Font.PLAIN, 14);
    
    /**
     * Small text font - 13pt Regular
     * Used for: Table content, small text
     */
    public static final Font FONT_SMALL = new Font(FONT_FAMILY, Font.PLAIN, 13);
    
    /**
     * Tiny text font - 11pt Regular
     * Used for: Hints, footnotes, copyright text
     */
    public static final Font FONT_TINY = new Font(FONT_FAMILY, Font.PLAIN, 11);
    
    /**
     * Table header font - 13pt/14pt Bold
     */
    public static final Font FONT_TABLE_HEADER = new Font(FONT_FAMILY, Font.BOLD, 13);
    
    /**
     * Large icon/emoji font - 48pt
     * Used for: Card icons
     */
    public static final Font FONT_ICON_LARGE = new Font(EMOJI_FONT_FAMILY, Font.PLAIN, 48);
    
    /**
     * Medium icon/emoji font - 32pt
     * Used for: Profile pictures in header
     */
    public static final Font FONT_ICON_MEDIUM = new Font(EMOJI_FONT_FAMILY, Font.PLAIN, 32);
    
    /**
     * Small icon/emoji font - 40pt
     * Used for: Profile picture editor
     */
    public static final Font FONT_ICON_SMALL = new Font(EMOJI_FONT_FAMILY, Font.PLAIN, 40);
    
    
    // ==================== SPACING CONSTANTS ====================
    
    /**
     * Extra small spacing - 5px
     */
    public static final int SPACING_XS = 5;
    
    /**
     * Small spacing - 8px
     */
    public static final int SPACING_SM = 8;
    
    /**
     * Medium spacing - 10px
     */
    public static final int SPACING_MD = 10;
    
    /**
     * Large spacing - 15px
     */
    public static final int SPACING_LG = 15;
    
    /**
     * Extra large spacing - 20px
     */
    public static final int SPACING_XL = 20;
    
    /**
     * Extra extra large spacing - 30px
     */
    public static final int SPACING_XXL = 30;
    
    /**
     * Huge spacing - 40px
     */
    public static final int SPACING_HUGE = 40;
    
    
    // ==================== COMPONENT SIZE CONSTANTS ====================
    
    /**
     * Standard button width
     */
    public static final int BUTTON_WIDTH = 140;
    
    /**
     * Standard button height
     */
    public static final int BUTTON_HEIGHT = 40;
    
    /**
     * Standard table row height
     */
    public static final int TABLE_ROW_HEIGHT = 35;
    
    /**
     * Profile picture button size
     */
    public static final int PROFILE_PIC_SIZE = 70;
    
    
    // ==================== UTILITY METHODS ====================
    
    /**
     * Get primary color (for future dynamic theming)
     * @return Primary color
     */
    public static Color primary() {
        return PRIMARY_COLOR;
    }
    
    /**
     * Get background color (for future dynamic theming)
     * @return Background color
     */
    public static Color background() {
        return BACKGROUND_COLOR;
    }
    
    /**
     * Get text color (for future dynamic theming)
     * @return Text color
     */
    public static Color text() {
        return TEXT_COLOR;
    }
    
    /**
     * Get success color
     * @return Success color
     */
    public static Color success() {
        return SUCCESS_COLOR;
    }
    
    /**
     * Get danger color
     * @return Danger color
     */
    public static Color danger() {
        return DANGER_COLOR;
    }
    
    /**
     * Darken a color by a percentage (for hover effects)
     * @param color Original color
     * @param percentage Percentage to darken (0.0 to 1.0)
     * @return Darkened color
     */
    public static Color darken(Color color, double percentage) {
        int r = Math.max(0, (int) (color.getRed() * (1 - percentage)));
        int g = Math.max(0, (int) (color.getGreen() * (1 - percentage)));
        int b = Math.max(0, (int) (color.getBlue() * (1 - percentage)));
        return new Color(r, g, b);
    }
    
    /**
     * Lighten a color by a percentage
     * @param color Original color
     * @param percentage Percentage to lighten (0.0 to 1.0)
     * @return Lightened color
     */
    public static Color lighten(Color color, double percentage) {
        int r = Math.min(255, (int) (color.getRed() + (255 - color.getRed()) * percentage));
        int g = Math.min(255, (int) (color.getGreen() + (255 - color.getGreen()) * percentage));
        int b = Math.min(255, (int) (color.getBlue() + (255 - color.getBlue()) * percentage));
        return new Color(r, g, b);
    }
    
    /**
     * Future: Enable dark mode
     * This method is a placeholder for future dark mode implementation
     */
    public static void enableDarkMode() {
        // TODO: Implement dynamic theme switching
        // This would swap light colors with dark equivalents
    }
    
    /**
     * Future: Enable light mode (default)
     */
    public static void enableLightMode() {
        // TODO: Implement dynamic theme switching
    }
    
    // Private constructor to prevent instantiation
    private UITheme() {
        throw new AssertionError("UITheme is a utility class and should not be instantiated");
    }
}
