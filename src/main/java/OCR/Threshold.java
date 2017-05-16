package OCR;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Threshold {

	private static final String SAMPLE_REGULAR = "D:\\test\\sample_regular.png";
	private static final String SAMPLE_GHOST = "D:\\test\\sample_ghost.png";
	private static final String SAMPLE_HAMMER_HEAD = "D:\\test\\sample_hammer_head.png";
	private static final String SAMPLE_TIGER = "D:\\test\\sample_tiger.png";
	private static final String SAMPLE_PIRANHA = "D:\\test\\sample_piranha.png";
	private static final String SAMPLE_DOUBLE_PIRANHA = "D:\\test\\sample_double_piranha.png";
	private static final String SAMPLE_POISON = "D:\\test\\sample_poison.png";

	private static final String[] SAMPLE_FILES;
	public static final Threshold[] VALUES;
	static {
		SAMPLE_FILES = new String[SharkType.values().length];
		SAMPLE_FILES[SharkType.REGULAR.value] = SAMPLE_REGULAR;
		SAMPLE_FILES[SharkType.GHOST.value] = SAMPLE_GHOST;
		SAMPLE_FILES[SharkType.HAMMER_HEAD.value] = SAMPLE_HAMMER_HEAD;
		SAMPLE_FILES[SharkType.TIGER.value] = SAMPLE_TIGER;
		SAMPLE_FILES[SharkType.PIRANHA.value] = SAMPLE_PIRANHA;
		SAMPLE_FILES[SharkType.DOUBLE_PIRANHA.value] = SAMPLE_DOUBLE_PIRANHA;
		SAMPLE_FILES[SharkType.POISON.value] = SAMPLE_POISON;

		VALUES = new Threshold[SharkType.values().length];
	}

	public float[] min, max, avg;

	private Threshold(float[] min, float[] max, float[] avg) {
		this.min = min;
		this.max = max;
		this.avg = avg;
	}

	public static void readThresholds() {
		for (int typeIndex = 0; typeIndex < SAMPLE_FILES.length; typeIndex++) {
			String fileName = SAMPLE_FILES[typeIndex];

			BufferedImage img;
			try {
				img = ImageIO.read(new File(fileName));
				int width = img.getWidth();
			    int height = img.getHeight();

			    float[] average = new float[3];
			    float[] min = {999, 999, 999};
			    float[] max = new float[3];

			    for (int i = 0; i < width; i++) {
			    	for (int j = 0; j < height; j++) {
			    		int color = img.getRGB(i, j);
			    		Color c = new Color(color);

			    		float[] hsv = Utils.rgbToHsl(c.getRed(), c.getGreen(), c.getBlue());
			    		float h = hsv[0], s = hsv[1], v = hsv[2];

			    		for (int colorComponentIndex = 0; colorComponentIndex < average.length; colorComponentIndex++) {
			    			average[colorComponentIndex] += hsv[colorComponentIndex];

		    				max[colorComponentIndex] = Math.max(max[colorComponentIndex], hsv[colorComponentIndex]);
		    				min[colorComponentIndex] = Math.min(min[colorComponentIndex], hsv[colorComponentIndex]);
			    		}
			    	}
			    }
			    int size = width * height;
			    for (int colorComponentIndex = 0; colorComponentIndex < average.length; colorComponentIndex++) {
			    	average[colorComponentIndex] /= size;
			    }

//			    System.out.println("----------> Type index = " + typeIndex);
//			    System.out.println("Count is " + (size) + ". Avg is " + Utils.arrayToString(average));
//			    System.out.println("Min = " + Utils.arrayToString(min) + ". And max = " + Utils.arrayToString(max));
			    VALUES[typeIndex] = new Threshold(min, max, average);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}

		}
	}
}
