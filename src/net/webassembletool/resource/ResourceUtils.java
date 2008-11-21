package net.webassembletool.resource;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import net.webassembletool.RequestContext;
import net.webassembletool.UserContext;

/**
 * Utility class to generate URL and path for Resources
 * 
 * @author François-Xavier Bonnet
 */
public class ResourceUtils {

    private final static String buildQueryString(RequestContext target) {
	UserContext context = target.getUserContext();
	try {

	    StringBuilder queryString = new StringBuilder();
	    String charset = target.getOriginalRequest().getCharacterEncoding();
	    if (charset == null)
		charset = "ISO-8859-1";
	    String qs = target.getOriginalRequest().getQueryString();
	    if (qs != null && !qs.equals(""))
		queryString.append(qs).append("&");
	    Map<String, String> parameters = target.getParameters();
	    if (context != null) {
		for (Map.Entry<String, String> temp : context.getParameterMap()
			.entrySet()) {
		    queryString.append(
			    URLEncoder.encode(temp.getKey(), charset)).append(
			    "=").append(
			    URLEncoder.encode(temp.getValue(), charset))
			    .append("&");
		}
	    }
	    if (parameters != null) {
		for (Map.Entry<String, String> temp : parameters.entrySet()) {
		    queryString.append(
			    URLEncoder.encode(temp.getKey(), charset)).append(
			    "=").append(
			    URLEncoder.encode(temp.getValue(), charset))
			    .append("&");
		}
	    }
	    if (queryString.length() == 0)
		return "";
	    return queryString.substring(0, queryString.length() - 1);
	} catch (UnsupportedEncodingException e) {
	    throw new RuntimeException(e);
	}
    }

    private final static String concatUrl(String baseUrl, String relUrl) {
	StringBuilder url = new StringBuilder();
	if (baseUrl != null && relUrl != null
		&& (baseUrl.endsWith("/") || baseUrl.endsWith("\\"))
		&& relUrl.startsWith("/")) {
	    url.append(baseUrl.substring(0, baseUrl.length() - 1)).append(
		    relUrl);
	} else {
	    url.append(baseUrl).append(relUrl);
	}
	return url.toString();
    }

    public final static String getHttpUrlWithQueryString(RequestContext target) {
	String url = concatUrl(target.getDriver().getBaseURL(), target
		.getRelUrl());
	String queryString = ResourceUtils.buildQueryString(target);
	if ("".equals(queryString))
	    return url;
	else
	    return url + "?" + queryString;
    }

    public final static String getHttpUrl(RequestContext target) {
	String url = concatUrl(target.getDriver().getBaseURL(), target
		.getRelUrl());
	return url;
    }

    public final static String getFileUrl(String localBase,
	    RequestContext target) {
	String url = concatUrl(localBase, target.getRelUrl());
	// Append queryString hashcode to supply different cache
	// filenames
	String queryString = ResourceUtils.buildQueryString(target);
	if ("".equals(queryString))
	    return url;
	else
	    return url + "_" + queryString.hashCode();
    }
}
