package net.webassembletool;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet used to proxy requests from a remote application.
 * 
 * @author François-Xavier Bonnet
 */
public class ProxyServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private String provider;

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String relUrl = request.getServletPath();
        if (request.getPathInfo() != null)
            relUrl += request.getPathInfo();
        boolean propagateJsessionId = response.encodeURL("/").contains("jsessionid");
        String url = request.getQueryString();
        Map<String, String> params = new HashMap<String, String>();
        if (url != null) {
            String[] parametersArray = url.split("&");
            if (parametersArray != null && parametersArray.length > 0)
                for (String parameter : parametersArray) {
                    String[] temp = parameter.split("=");
                    if (temp != null && temp.length == 2)
                        params.put(temp[0], temp[1]);
                    else if (temp != null && temp.length == 1)
                        params.put(temp[0], "");
                }
        }
        DriverFactory.getInstance(provider).proxy(relUrl, request, response, params, propagateJsessionId);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        provider = config.getInitParameter("provider");
    }
}
