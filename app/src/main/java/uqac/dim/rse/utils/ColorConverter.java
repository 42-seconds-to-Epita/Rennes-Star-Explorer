package uqac.dim.rse.utils;

public class ColorConverter {

    public static int hexToArgb(String hexColor) {
        // Check if the input string is valid
        if (hexColor == null || hexColor.length() != 7 || !hexColor.startsWith("#")) {
            throw new IllegalArgumentException("Invalid hex color format");
        }

        try {
            // Parse the individual color components
            int red = Integer.parseInt(hexColor.substring(1, 3), 16);
            int green = Integer.parseInt(hexColor.substring(3, 5), 16);
            int blue = Integer.parseInt(hexColor.substring(5, 7), 16);

            // Create the ARGB integer with full alpha (255)

            return (255 << 24) | (red << 16) | (green << 8) | blue;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid hex color format", e);
        }
    }
}

