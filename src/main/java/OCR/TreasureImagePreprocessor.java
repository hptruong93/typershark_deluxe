package OCR;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class TreasureImagePreprocessor extends ImagePreprocessor {
	
	public void preprocess(BufferedImage img) {
		int width = img.getWidth();
	    int height = img.getHeight();
	    
	    for (int i = 0; i < width; i++) {
	    	for (int j = 0; j < height; j++) {
	    		int color = img.getRGB(i, j);
	    		Color c = new Color(color);
	    		
	    		boolean cond1 = c.getRed() + c.getGreen() + c.getBlue() < 700;
	    		boolean ignore = cond1;
	    		
	    		if (ignore) {
	    			img.setRGB(i, j, WHITE_RGB);
	    		} else {
	    			img.setRGB(i, j, BLACK_RGB);
	    		}
	    	}
	    }
	}
}
