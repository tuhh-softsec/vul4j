/* 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.esigate.impl;

import java.net.URI;
import java.util.Stack;

import org.apache.commons.lang3.StringUtils;
import org.esigate.Parameters;

public class UriBuilder {
    private String scheme;
    private String host;
    private int port = -1;
    private String path;
    private String file;
    private String queryString;
    private String fragment;

    /**
     * Creates a copy of an existing {@link UriBuilder}
     * 
     * @param uriBuilder
     */
    public UriBuilder(UriBuilder uriBuilder) {
        this.scheme = uriBuilder.scheme;
        this.host = uriBuilder.host;
        this.port = uriBuilder.port;
        this.path = uriBuilder.path;
        this.file = uriBuilder.file;
        this.queryString = uriBuilder.queryString;
        this.fragment = uriBuilder.fragment;
    }

    /**
     * Parses an uri written as a {@link String}
     * 
     * @param uriString
     */
    public UriBuilder(String uriString) {
        URI uri = createUri(uriString);
        scheme = uri.getScheme();
        host = uri.getHost();
        port = uri.getPort();
        String path = uri.getPath();
        if (!StringUtils.isEmpty(path)) {
            if (path.endsWith("/")) {
                this.path = path;
            } else {
                int position = path.lastIndexOf('/');
                this.path = path.substring(0, position + 1);
                this.file = path.substring(position + 1, path.length());
            }
        }
        queryString = uri.getRawQuery();
        fragment = uri.getRawFragment();
    }

    public UriBuilder() {
    }

    /**
     * Creates an {@link URI} after escaping some special characters in order to tolerate some incorrect URI types
     * 
     * @param uriString
     * @return
     */
    private URI createUri(String uriString) {
        uriString = uriString.replaceAll(" ", "+");
        return URI.create(uriString);
    }

    public String getPath() {
        return path;
    }

    /**
     * Sets the path of this uri or concatenates several path fragments. Adds a "/" at the beginning if necessary. While
     * concatenating, checks that there is only 1 "/" character between each path fragment.
     * 
     * @param path
     */
    public void setPath(String... path) {
        this.path = null;
        if (path != null) {
            String pathFragment;
            for (int i = 0; i < path.length; i++) {
                pathFragment = path[i];
                if (!StringUtils.isEmpty(pathFragment)) {
                    pathFragment = StringUtils.stripStart(pathFragment, "/");
                    pathFragment = StringUtils.stripEnd(pathFragment, "/");
                    if (this.path == null) {
                        this.path = "/" + pathFragment;
                    } else if (this.path.endsWith("/")) {
                        this.path = this.path + pathFragment;
                    } else {
                        this.path = this.path + "/" + pathFragment;
                    }
                }
            }
            if (this.path != null && !this.path.endsWith("/")) {
                this.path = this.path + "/";
            }
        }
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(Parameters.SMALL_BUFFER_SIZE);
        if (scheme != null) {
            result.append(scheme).append(":");
        }
        if (host != null) {
            result.append("//").append(host);
        }
        if (port > -1) {
            result.append(":").append(port);
        }
        if (path != null) {
            result.append(path);
        }
        if (file != null) {
            result.append(file);
        }
        if (queryString != null) {
            result.append("?").append(queryString);
        }
        if (fragment != null) {
            result.append("#").append(fragment);
        }
        return result.toString();
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean startsWith(UriBuilder uri) {
        if (scheme != null && !scheme.equals(uri.getScheme())) {
            return false;
        }
        if (host != null && !host.equals(uri.getHost())) {
            return false;
        }
        if (host != null && port != uri.getPort()) {
            return false;
        }
        String pathString = path;
        if (pathString == null) {
            pathString = "";
        }
        if (StringUtils.isNotEmpty(path) && !path.startsWith("/")) {
            // relative path
            return true;
        }
        String uriPathString = uri.getPath();
        if (uriPathString == null) {
            uriPathString = "";
        }
        return "".equals(pathString) || pathString.startsWith(uriPathString);
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public boolean isRelative() {
        return path != null && !path.startsWith("/");
    }

    public void normalize() {
        if (StringUtils.isEmpty(path)) {
            return;
        }
        boolean isAbsolute = path.startsWith("/");
        Stack<String> stack = new Stack<String>();
        String[] split = path.split("/");
        for (int i = 0; i < split.length; i++) {
            String pathElement = split[i];
            if (pathElement.equals(".") || pathElement.equals("")) {
                // ignore
            } else if (pathElement.equals("..")) {
                if (isAbsolute) {
                    if (!stack.isEmpty()) {
                        stack.pop();
                    }
                } else {
                    if (!stack.empty() && !stack.peek().equals("..")) {
                        stack.pop();
                    } else {
                        stack.push(pathElement);
                    }
                }
            } else {
                stack.push(pathElement);
            }
        }
        String result = "";
        while (!stack.isEmpty()) {
            result = "/" + result;
            ;
            result = stack.pop() + result;
        }
        if (isAbsolute) {
            result = "/" + result;
        }
        path = result;
    }

    public String getFragment() {
        return fragment;
    }

    public void setFragment(String fragment) {
        this.fragment = fragment;
    }

    /**
     * Creates a new {@link UriBuilder} based on an url interpreted relatively to another {@link UriBuilder}
     * 
     * @param relativeUrl
     * @param base
     */
    public UriBuilder resolve(String relativeUrl) {
        UriBuilder result = new UriBuilder(relativeUrl);
        if (result.getScheme() != null) {
            return result;
        }
        result.setScheme(this.getScheme());
        if (result.getHost() != null) {
            return result;
        }
        result.setHost(this.getHost());
        if (result.getPort() != -1) {
            return result;
        }
        result.setPort(this.getPort());
        if (result.getPath() != null && result.getPath().startsWith("/")) {
            return result;
        }
        result.setPath(this.getPath(), result.getPath());
        if (result.getFile() != null) {
            return result;
        }
        result.setFile(this.getFile());
        if (result.getQueryString() != null) {
            return result;
        }
        result.setQueryString(this.getQueryString());
        if (result.getFragment() != null) {
            return result;
        }
        result.setFragment(this.getFragment());
        return result;
    }

    public UriBuilder relativize(UriBuilder baseUriBuilder) {
        // TODO improve and test
        URI thisUri = createUri(this.toString());
        URI baseUri = createUri(baseUriBuilder.toString());
        return new UriBuilder(baseUri.relativize(thisUri).toString());
    }

    @Override
    public boolean equals(Object object) {
        // TODO improve and test
        if (object == null) {
            return false;
        }
        if (!(object instanceof UriBuilder)) {
            return false;
        }
        return this.toString().equals(object.toString());
    }

    public UriBuilder resolve(UriBuilder relativeUriBuilder) {
        // TODO improve and test
        return resolve(relativeUriBuilder.toString());
    }

}
