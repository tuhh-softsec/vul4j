/**
 * 
 */
package net.sf.xslthl;

import java.io.InputStream;
import java.util.Properties;

/**
 * Version information for xslthl
 */
public class Version {
    /**
     * @return the version string
     */
    public static String getVersion() {
	if (VersionInternal.type == null || VersionInternal.type.length() == 0) {
	    return String.format("%d.%d.%d", VersionInternal.major,
		    VersionInternal.minor, VersionInternal.revision);
	} else
	    return String.format("%d.%d.%d %s", VersionInternal.major,
		    VersionInternal.minor, VersionInternal.revision,
		    VersionInternal.type);
    }

    /**
     * @return the major version number
     */
    public static int getMajor() {
	return VersionInternal.major;
    }

    /**
     * @return the minor version number
     */
    public static int getMinor() {
	return VersionInternal.minor;
    }

    /**
     * @return the revision number
     */
    public static int getRevision() {
	return VersionInternal.revision;
    }

    /**
     * @return the release type: alpha, beta, rc, or null for stable releases
     */
    public static String getReleaseType() {
	return VersionInternal.type;
    }

    /**
     * Internal class responsible for loading
     */
    private static class VersionInternal {

	private static int major = 1;
	private static int minor = 9;
	private static int revision = 9;
	private static String type = null;

	static {
	    System.out.println("Loading version data");
	    Properties prop = new Properties();
	    InputStream is = VersionInternal.class
		    .getResourceAsStream("version.properties");
	    if (is != null) {
		try {
		    prop.load(is);
		    prop.list(System.out);
		    major = Integer.parseInt(prop.getProperty(
			    "xslthl.version.major", Integer.toString(major)));
		    minor = Integer.parseInt(prop.getProperty(
			    "xslthl.version.minor", Integer.toString(minor)));
		    revision = Integer.parseInt(prop.getProperty(
			    "xslthl.version.revision", Integer
				    .toString(revision)));
		    type = prop.getProperty("xslthl.version.type", type);
		} catch (Exception e) {
		    System.out.println(e.getMessage());
		}
	    }
	}
    }
}
