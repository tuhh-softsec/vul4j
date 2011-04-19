package net.webassembletool.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

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
		LineNumberReader in = new LineNumberReader(new InputStreamReader(new FileInputStream(src), "UTF-8"));
		try {
			HeadersFile result = new HeadersFile();

			String line = null;
			while ((line = in.readLine()) != null) {
				if (line.startsWith("#")) {
					continue;
				}
				int idx = line.indexOf('=');
				if (idx == -1) {
					continue;
				}
				String key = line.substring(0, idx).trim();
				String value = line.substring(idx + 1);
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

	public static void storeHeaders(File dest, HeadersFile headers) throws IOException {
		PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(dest), "UTF-8"));
		try {
			out.println("#Headers");
			out.println("#" + new Date().toString());
			for (Entry<String, List<String>> entry : headers.getHeadersMap().entrySet()) {
				for (String value : entry.getValue()) {
					out.println(entry.getKey() + "=" + value);
				}
			}
			out.println(Integer.toString(headers.getStatusCode()) + "=" + headers.getStatusMessage());
		} finally {
			out.close();
		}
	}
}
