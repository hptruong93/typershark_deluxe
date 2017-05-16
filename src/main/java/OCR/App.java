package OCR;

import java.awt.AWTException;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseMotionListener;

/**
 * Hello world!
 *
 */
public class App {
	
	private static final String IMAGE_FORMAT = "png";
	public static final String SCREEN_SHOT_FILE = Paths.get("D:", "test", "screenshot." + IMAGE_FORMAT).toAbsolutePath().toString();
	public static final String TEMP_FILE = Paths.get("D:", "test", "saved.png").toAbsolutePath().toString();
	public static Robot robot;
	
	public static void main(String[] args) throws AWTException, NativeHookException, InterruptedException {
		robot = new Robot();
		
		Threshold.readThresholds();
		
		KeyboardCore core = new KeyboardCore(robot);
		ImagePreprocessor preprocessor = new ImagePreprocessor();
		TreasureImagePreprocessor treasurePreprocessor = new TreasureImagePreprocessor();
		TextReader reader = new TextReader();
//		String[] files = new String[]{"D:\\test\\testImage.png", "D:\\test\\untitled.png"};
//		
//		for (String f : files) {
//			preprocessor.preprocess(f, TEMP_FILE);
//			List<String> words = reader.readText(f);
//			System.out.println(String.join(", ", words));
//		}
//		
//		reader.end();
		
		// Get the logger for "org.jnativehook" and set the level to off.
		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.OFF);

		// Change the level for all handlers attached to the default logger.
		Handler[] handlers = Logger.getLogger("").getHandlers();
		for (int i = 0; i < handlers.length; i++) {
			handlers[i].setLevel(Level.OFF);
		}

		List<String> toTypes = new ArrayList<>(10);
		Semaphore waiter = new Semaphore(0);
		GlobalScreen.registerNativeHook();
		GlobalScreen.addNativeKeyListener(new NativeKeyListener() {

			@Override
			public void nativeKeyTyped(NativeKeyEvent arg0) {

			}

			@Override
			public void nativeKeyReleased(NativeKeyEvent arg0) {
				if (arg0.getKeyCode() == NativeKeyEvent.VC_CONTROL_R) {
					process(toTypes, reader, preprocessor, true);
				} else if (arg0.getKeyCode() == NativeKeyEvent.VC_DELETE) {
					process(toTypes, reader, preprocessor, false);
				} else if (arg0.getKeyCode() == NativeKeyEvent.VC_INSERT) {
					for (String word : toTypes) {
						core.type(word);
						try {
							Thread.sleep(20);
						} catch (InterruptedException e) {
						}
					}
				} else if (arg0.getKeyCode() == NativeKeyEvent.VC_SCROLL_LOCK) {
					BufferedImage screen = screenShotTreasure(SCREEN_SHOT_FILE, false);
					treasurePreprocessor.preprocess(screen, TEMP_FILE);
					List<String> words = reader.readText(TEMP_FILE, true);
					System.out.println(String.join(", ", words));
					toTypes.clear();
					toTypes.addAll(words);
				} else if (arg0.getKeyCode() == NativeKeyEvent.VC_SHIFT_R) {
					waiter.release();
					System.out.println("Ending experiment!");
				} else if (arg0.getKeyCode() == NativeKeyEvent.VC_HOME) {
					screenShotFull(SCREEN_SHOT_FILE, true);
				}
			}

			@Override
			public void nativeKeyPressed(NativeKeyEvent arg0) {
			}
		});
		
		waiter.acquire();
		
		GlobalScreen.unregisterNativeHook();
		reader.end();
	}
	
	private static void process(List<String> toTypes, TextReader reader, ImagePreprocessor preprocessor, boolean fixWord) {
		System.out.println("Go");
		BufferedImage screen = screenShotFull(SCREEN_SHOT_FILE, false);
		preprocessor.preprocess(screen, TEMP_FILE);
		List<String> words = reader.readText(TEMP_FILE, fixWord);
		System.out.println(String.join(", ", words));
		toTypes.clear();
		toTypes.addAll(words);
	}

	public static BufferedImage screenShotFull(String destFile, boolean save) {
		Rectangle screenRect = new Rectangle(3270, 150, 550, 405);
		return screenShot(screenRect, destFile, save);
	}
	
	public static BufferedImage screenShotTreasure(String destFile, boolean save) {
		Rectangle screenRect = new Rectangle(3280, 320, 550, 200);
		return screenShot(screenRect, destFile, save);
	}
	
	public static BufferedImage screenShot(Rectangle screenRect, String destFile, boolean save) {
		BufferedImage screenFullImage = robot.createScreenCapture(screenRect);
		if (save) {
			try {
				ImageIO.write(screenFullImage, IMAGE_FORMAT, new File(destFile));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return screenFullImage;
	}
}
