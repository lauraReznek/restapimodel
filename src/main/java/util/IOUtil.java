package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class IOUtil {

	public static String getFileContentFromResourceStreamBufferedReader(final String fileOnResources) {
		StringBuffer contentAsStrBuffer = new StringBuffer();

		try (InputStream resourceAsStream = IOUtil.class.getResourceAsStream(fileOnResources.trim());
			 BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resourceAsStream))) {

			String strCurrentLine;

			while ((strCurrentLine = bufferedReader.readLine()) != null) {
				contentAsStrBuffer.append(strCurrentLine);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return contentAsStrBuffer.toString();
	}


}
