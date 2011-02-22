package net.webassembletool.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

/**
 * Internal utility class to store/retrieve data from disk.
 * 
 * @author <a href="stanislav.bernatskyi@smile.fr">Stanislav Bernatskyi</a>
 */
class FileUtils {
	private FileUtils() {
	}

	public static HeadersFile loadHeaders(File src) throws IOException {
		InputStream in = new FileInputStream(src);
		try {
			Properties prop = new Properties();
			prop.load(in);

			HeadersFile result = new HeadersFile();
			for (Entry<Object, Object> header : prop.entrySet()) {
				String key = header.getKey().toString();
				String value = header.getValue().toString();
				if (StringUtils.isNumeric(key)) {
					result.setStatusCode(Integer.parseInt(key));
					result.setStatusMessage(value);
				} else {
					result.addHeader(key, value);
				}
			}

			return result;
		} finally {
			in.close();
		}
	}

	public static void storeHeaders(File dest, HeadersFile headers)
			throws IOException {
		OutputStream out = new FileOutputStream(dest);
		try {
			Properties prop = new Properties();
			for (Entry<String, Object> entry : headers.getHeadersMap().entrySet()) {
				prop.setProperty(entry.getKey(), entry.getValue().toString());
			}
			prop.setProperty(Integer.toString(headers.getStatusCode()), headers.getStatusMessage());
			prop.store(out, "Headers");
		} finally {
			out.close();
		}
	}
}
