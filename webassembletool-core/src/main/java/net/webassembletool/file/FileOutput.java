package net.webassembletool.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import net.webassembletool.output.Output;
import net.webassembletool.output.OutputException;

/**
 * Output implementation that saves the file and headers into two distinct
 * files.
 * 
 * @author Francois-Xavier Bonnet
 * 
 */
public class FileOutput extends Output {
	private final File file;
	private final File headerFile;
	private FileOutputStream fileOutputStream;

	public FileOutput(File dataFile, File headersFile) {
		this.file = dataFile;
		this.headerFile = headersFile;
	}

	/** {@inheritDoc} */
	@Override
	public void open() {
		try {
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			fileOutputStream = new FileOutputStream(file);
		} catch (IOException e) {
			throw new OutputException("Could not create file: " + file.toURI(), e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public OutputStream getOutputStream() {
		return fileOutputStream;
	}

	/** {@inheritDoc} */
	@Override
	public void close() {
		try {
			// In case the file could not be written, fileOutputStream might be null
			if (fileOutputStream != null) {
				fileOutputStream.close();
			}
		} catch (IOException e) {
			throw new OutputException("Could not close file: " + file.toURI(), e);
		} finally {
			fileOutputStream = null;
		}
		try {
			FileUtils.storeHeaders(headerFile, new HeadersFile(getHeaders(), getStatusCode(), getStatusMessage()));
		} catch (IOException e) {
			throw new OutputException("Could not write to file: " + headerFile.toURI(), e);
		}
	}

	public void delete() {
		file.delete();
		headerFile.delete();
	}
}
