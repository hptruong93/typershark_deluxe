package OCR;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImagePreprocessor {

	protected static final int WHITE_RGB = Color.WHITE.getRGB();
	protected static final int BLACK_RGB = Color.BLACK.getRGB();
	protected static final int PIRANHA_EYE_SQUARE_SIZE = 5;

	public void preprocess(String filePath, String destPath) {
		BufferedImage img = null;
		try {
		    img = ImageIO.read(new File(filePath));
		    preprocess(img);

		    File outputfile = new File(destPath);
		    ImageIO.write(img, "png", outputfile);
		} catch (IOException e) {
		}
		System.out.println("Done image preprocessing");
	}

	public void preprocess(BufferedImage img, String destPath) {
		try {
		    preprocess(img);

		    File outputfile = new File(destPath);
		    ImageIO.write(img, "png", outputfile);
		} catch (IOException e) {
		}
		System.out.println("Done image preprocessing");
	}

	private boolean isType(SharkType type, Color c, float[] hsv) {
		Threshold thredhold = Threshold.VALUES[type.value];
		boolean result = Utils.between(hsv[0], thredhold.min[0], thredhold.max[0]) 
				&& Utils.between(hsv[1], thredhold.min[1], thredhold.max[1]);
		
		if (type == SharkType.GHOST) {
			result &= Utils.between(hsv[2], thredhold.min[2], thredhold.max[2]);
		}
		return result;
	}
	
	public SharkType identifyType(BufferedImage img) {
		int width = img.getWidth();
	    int height = img.getHeight();

	    int[] count = new int[SharkType.values().length];
	    for (int i = 0; i < width; i++) {
	    	for (int j = 0; j < height; j++) {
	    		int color = img.getRGB(i, j);
	    		Color c = new Color(color);

	    		float[] hsv = Utils.rgbToHsl(c.getRed(), c.getGreen(), c.getBlue());
	    		float h = hsv[0], s = hsv[1], v = hsv[2];

	    		for (SharkType typeIndex : SharkType.values()) {
	    			if (isType(typeIndex, c, hsv)) {
	    				count[typeIndex.value]++;
	    			}
	    		}
	    	}
	    }
	    
	    // Argmax
	    System.out.println("Counted " + Utils.arrayToString(count));
	    int maxValue = 0;
	    int maxArg = -1;
	    for (int i = 0; i < count.length; i++) {
	    	if (count[i] > maxValue) {
	    		maxValue = count[i];
	    		maxArg = i;
	    	}
	    }
	    
		return SharkType.identify(maxArg);
	}

	public void preprocess(BufferedImage img) {
		SharkType type = identifyType(img);
		System.out.println("Type is " + type);
		
		int width = img.getWidth();
	    int height = img.getHeight();

	    for (int i = 0; i < width; i++) {
	    	for (int j = 0; j < height; j++) {
	    		int color = img.getRGB(i, j);
	    		Color c = new Color(color);
	    		float[] hsl = Utils.rgbToHsl(c.getRed(), c.getGreen(), c.getBlue());
	    		float h = hsl[0], s = hsl[1], v = hsl[2];

	    		boolean ignore = true;
	    		if (type == SharkType.REGULAR) {
		    		boolean cond1 = c.getRed() + c.getGreen() + c.getBlue() > 100;
		    		boolean cond2 = s > 0.7;
		    		ignore = cond1 || cond2;
	    		} else if (type == SharkType.HAMMER_HEAD) {
		    		boolean cond1 = c.getRed() + c.getGreen() + c.getBlue() > 91;
		    		boolean cond2 = s > 0.7;
		    		ignore = cond1 || cond2;
	    		} else if (type == SharkType.GHOST) {
	    			ignore = c.getRed() + c.getGreen() + c.getBlue() > 120;
	    		} else if (type == SharkType.TIGER) {
	    			boolean cond1 = c.getRed() + c.getGreen() + c.getBlue() > 90;
		    		boolean cond2 = s < 0.75 && v < 0.80;
		    		ignore = cond1 || cond2;
	    		} else if (type == SharkType.PIRANHA) {
	    			ignore = c.getRed() + c.getGreen() + c.getBlue() > 120;
	    		} else if (type == SharkType.DOUBLE_PIRANHA) {
	    			ignore = c.getRed() + c.getGreen() + c.getBlue() > 100;
	    		} else if (type == SharkType.POISON) {
	    			boolean cond1 = c.getRed() + c.getGreen() + c.getBlue() > 100;
		    		ignore = cond1;
	    		}

	    		if (ignore) {
	    			img.setRGB(i, j, WHITE_RGB);
	    		} else {
	    			img.setRGB(i, j, BLACK_RGB);
	    		}
	    	}
	    }

	    if (type == SharkType.PIRANHA || type == SharkType.DOUBLE_PIRANHA) {
	    	int[] rgbs = new int[PIRANHA_EYE_SQUARE_SIZE*PIRANHA_EYE_SQUARE_SIZE];
	    	
	    	for (int i = 0; i < width - PIRANHA_EYE_SQUARE_SIZE; i++) {
	    		for (int j = 0; j < height - PIRANHA_EYE_SQUARE_SIZE; j++) {
	    			int count = 0;
	    			rgbs = img.getRGB(i, j, PIRANHA_EYE_SQUARE_SIZE, PIRANHA_EYE_SQUARE_SIZE, rgbs, 0, PIRANHA_EYE_SQUARE_SIZE);
	    			for (int index  = 0; index < rgbs.length; index++) {
	    				if (rgbs[index] == BLACK_RGB) {
	    					count++;
	    				}
	    			}
	    			
	    			if (count > (type == SharkType.PIRANHA ? 18 : 17)) {
	    				for (int index = 0; index < rgbs.length; index++) {
	    					rgbs[index] = WHITE_RGB;
	    				}
	    				img.setRGB(i, j, PIRANHA_EYE_SQUARE_SIZE, PIRANHA_EYE_SQUARE_SIZE, rgbs, 0, PIRANHA_EYE_SQUARE_SIZE);
	    			}
	    		}
	    	}
	    }
	}
}
