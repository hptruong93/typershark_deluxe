package OCR;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class Utils {
	
	public static String arrayToString(int[] arr) {
		StringBuilder b = new StringBuilder();
		String joiner = "";
		for (int i = 0; i < arr.length; i++) {
			b.append(joiner);
			b.append(arr[i]);
			joiner = ", ";
		}
		return b.toString();
	}
	
	public static String arrayToString(float[] arr) {
		StringBuilder b = new StringBuilder();
		String joiner = "";
		for (int i = 0; i < arr.length; i++) {
			b.append(joiner);
			b.append(arr[i]);
			joiner = ", ";
		}
		return b.toString();
	}
	
	public static boolean between(float val, float low, float hi) {
		return val >= low && val <= hi;
	}
	
	public static void negative(BufferedImage img) {
		int width = img.getWidth();
	    int height = img.getHeight();
	    
	    for (int i = 0; i < width; i++) {
	    	for (int j = 0; j < height; j++) {
	    		int color = img.getRGB(i, j);
	    		Color c = new Color(color);
	    		
    			img.setRGB(i, j, new Color(255 - c.getRed(), 255 - c.getGreen(), 255 - c.getBlue()).getRGB());
	    	}
	    }
	}
	
	/**
	 * Converts an RGB color value to HSL. Conversion formula
	 * adapted from http://en.wikipedia.org/wiki/HSL_color_space.
	 * Assumes pR, pG, and bpBare contained in the set [0, 255] and
	 * returns h, s, and l in the set [0, 1].
	 *
	 * @param pR       The red color value
	 * @param pG       The green color value
	 * @param pB       The blue color value
	 * @return float array, the HSL representation
	 */
	public static float[] rgbToHsl(int pR, int pG, int pB) {
	    float r = pR / 255f;
	    float g = pG / 255f;
	    float b = pB / 255f;

	    float max = (r > g && r > b) ? r : (g > b) ? g : b;
	    float min = (r < g && r < b) ? r : (g < b) ? g : b;

	    float h, s, l;
	    l = (max + min) / 2.0f;

	    if (max == min) {
	        h = s = 0.0f;
	    } else {
	        float d = max - min;
	        s = (l > 0.5f) ? d / (2.0f - max - min) : d / (max + min);

	        if (r > g && r > b)
	            h = (g - b) / d + (g < b ? 6.0f : 0.0f);

	        else if (g > b)
	            h = (b - r) / d + 2.0f;

	        else
	            h = (r - g) / d + 4.0f;

	        h /= 6.0f;
	    }

	    float[] hsl = {h, s, l};
	    return hsl;
	}
	
	private Utils() {}
}
