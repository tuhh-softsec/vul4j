package hudson.plugins.ccm.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtil {

	public static void fixCCMXmlHeader(File file) 
	throws IOException
	{
		if ( file.exists() && file.isFile() )
		{
			FileUtil.setXmlHeader(file);
		} else {
			throw new IOException("File " + file + " doest not exist or could not be read");
		}
	}

	public static void setXmlHeader(File file)  
	throws IOException
	{
		StringBuffer newFile = new StringBuffer();
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		String line = null;
		if ( (line = br.readLine() ) != null )
		{
			newFile.append("<?xml version=\"1.0\" encoding=\"iso-8859-1\" ?>\n");
		} else {
			throw new IOException("Invalid CCM result file");
		}
		while ( (line = br.readLine()) != null )
		{
			newFile.append( line );
			newFile.append("\n");
		}
		br.close();
		fr.close();
		
		FileWriter fw = new FileWriter(file);
		BufferedWriter bw = new BufferedWriter(fw);
		
		bw.append(newFile.toString());
		bw.flush();
		bw.close();
		fw.close();
	}
	
}
