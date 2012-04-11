package org.esigate.http;

/**
 * An interface that provides HTTP header constants.
 * <p>
 * Based on javax.ws.rs.core.HttpHeaders (which is only available on Java 6)
 * with additional headers used by ESIGate.
 * 
 * @see "http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html"
 * @author Nicolas Richeton
 * 
 */
public interface HttpHeaders {

	String ACCEPT = "Accept";
	String ACCEPT_CHARSET = "Accept-Charset";
	String ACCEPT_ENCODING = "Accept-Encoding";
	String ACCEPT_LANGUAGE = "Accept-Language";
	String AUTHORIZATION = "Authorization";
	String CACHE_CONTROL = "Cache-Control";
	String CONTENT_ENCODING = "Content-Encoding";
	String CONTENT_LANGUAGE = "Content-Language";
	String CONTENT_LENGTH = "Content-Length";
	String CONTENT_LOCATION = "Content-Location";
	String CONTENT_TYPE = "Content-Type";
	String COOKIE = "Cookie";
	String DATE = "Date";
	String ETAG = "ETag";
	String EXPIRES = "Expires";
	String EXPECT = "Expect";
	String HOST = "Host";
	String IF_MATCH = "If-Match";
	String IF_MODIFIED_SINCE = "If-Modified-Since";
	String IF_NONE_MATCH = "If-None-Match";
	String IF_UNMODIFIED_SINCE = "If-Unmodified-Since";
	String LAST_MODIFIED = "Last-Modified";
	String LOCATION = "Location";
	String SET_COOKIE = "Set-Cookie";
	String USER_AGENT = "User-Agent";
	String VARY = "Vary";
	String PRAGMA = "Pragma";
	String WWW_AUTHENTICATE = "WWW-Authenticate";
	String REFERER = "Referer";
	String LINK = "Link";
	String P3P = "P3P";
}
