package OCR;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.lept;
import org.bytedeco.javacpp.lept.PIX;
import org.bytedeco.javacpp.tesseract.TessBaseAPI;

public class TextReader {

	private final TessBaseAPI api;
	
	public TextReader() {
		// Initialize tesseract-ocr with English, without specifying tessdata
		api = new TessBaseAPI();
		if (api.Init(".", "ENG") != 0) {
			System.err.println("Could not initialize tesseract.");
			System.exit(1);
		}
		
		System.out.println("Initialized tesseract.");
	}
	
	public List<String> readText(String fileName) {
		return readText(fileName, true);
	}
	
	public List<String> readText(String fileName, boolean fixWord) {
		String text = doTesseract(fileName);
		
		// Filter for only meaningful texts
		List<String> words = new ArrayList<>();
		for (String line : text.split("\n")) {
			Stream<String> found = Arrays.asList(line.split(" ")).stream().filter(word -> word.chars().allMatch(Character::isLetter));
			found = found.filter(s -> !s.isEmpty()).map(s -> s.toLowerCase());
			
			List<String> toAdd = found.collect(Collectors.toList());
			if (fixWord) {
				words.addAll(toAdd.stream().map(s -> fixWord(s)).collect(Collectors.toList()));
			} else {
				words.addAll(toAdd);
			}
		}
		
		boolean isMoreThanOne = words.stream().anyMatch(s -> s.length() > 1);
		if (isMoreThanOne) { // Not single characters
			words = words.stream().filter(s -> s.length() > 1).collect(Collectors.toList());
		}
		
		return words.stream().distinct().collect(Collectors.toList());
	}
	
	public static String fixWord(String word) {
		// If v is preceeded by a consonant then change it to y, except when that v is followed by a vowel.
		char[] chars = word.toCharArray();
		int vIndex = 0;
		while (true) {
			vIndex = word.indexOf('v', vIndex);
			if (vIndex < 0) {
				break;
			}
			if (vIndex == 0) {
				vIndex++;
				continue;
			}

			System.out.println("Index " + vIndex);
			if (!isVowel(chars[vIndex - 1])) {
				if (vIndex + 1 != chars.length) {
					if (!isVowel(chars[vIndex + 1])) {
						chars[vIndex] = 'y';
					}
				} else {
					chars[vIndex] = 'y';
				}
			} else if (vIndex + 1 == chars.length) {
				chars[vIndex] = 'y';
			}
			
			vIndex++;
		}
		
		return new String(chars);
	}
	
	private static boolean isVowel(char c) {
		return c == 'a' ||
				c == 'e' || 
				c == 'i' || 
				c == 'o' || 
				c == 'u';
	}
	
	private String doTesseract(String fileName) {
		BytePointer outText;
		
		// Open input image with leptonica library
		PIX image = lept.pixRead(App.TEMP_FILE);
		api.SetImage(image);
		// Get OCR result
		outText = api.GetUTF8Text();
		String string = outText.getString();
		
		outText.deallocate();
		lept.pixDestroy(image);
		
//		System.out.println("OCR output:\n" + string);
		return string;
	}
	
	public void end() {
		// Destroy used object and release memory
		api.End();
	}
}
