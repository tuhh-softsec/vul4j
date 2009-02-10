package net.webassembletool.parse;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;

import net.webassembletool.RetrieveException;
import net.webassembletool.StringUtils;
import net.webassembletool.output.StringOutput;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Template renderer.
 * <p>
 * Retrieves a template from the provider application and renders it to the
 * writer replacing the parameters with the given map. If <code>name</code>
 * param is <code>null</code>, the whole page will be used as the template.<br />
 * eg: The template <code>mytemplate</code> can be delimited in the provider
 * page by comments <code>&lt;!--$begintemplate$mytemplate$--&gt;</code> and
 * <code>&lt;!--$endtemplate$mytemplate$--&gt;</code>.<br />
 * Inside the template, the parameters can be defined by comments.<br />
 * eg: parameter named <code>myparam</code> should be delimited by comments
 * <code>&lt;!--$beginparam$myparam$--&gt;</code> and
 * <code>&lt;!--$endparam$myparam$--&gt;</code>
 * 
 * @author Stanislav Bernatskyi
 */
public class TemplateRenderer implements Renderer {
    private final static Log log = LogFactory.getLog(TemplateRenderer.class);

    private final String page;
    private final String name;
    private final Map<String, String> params;

    public TemplateRenderer(String name, Map<String, String> params, String page) {
        this.name = name;
        this.params = params;
        this.page = page;
    }

    /** {@inheritDoc} */
    public void render(StringOutput src, Writer out,
            Map<String, String> replaceRules) throws IOException,
            RetrieveException {
        if (src.getStatusCode() != HttpServletResponse.SC_OK) {
            throw new RetrieveException(src.getStatusCode(), src
                    .getStatusMessage(), src.toString());
        }
        String content = src.toString();
        StringBuilder sb = new StringBuilder();
        if (content != null) {
            if (name != null) {
                Tag openTag = Tag.find("begintemplate$" + name, content);
                Tag closeTag = Tag.find("endtemplate$" + name, content);
                if (openTag == null || closeTag == null) {
                    log.warn("Template not found: page=" + page + " template="
                            + name);
                } else {
                    log.debug("Serving template: page=" + page + " template="
                            + name);
                    sb.append(content, openTag.getEndIndex(), closeTag
                            .getBeginIndex());
                }
            } else {
                log.debug("Serving template: page=" + page);
                sb.append(content);
            }
            if (params != null) {
                for (Entry<String, String> param : params.entrySet()) {
                    String key = param.getKey();
                    String value = param.getValue();
                    Tag openTag = Tag.find("beginparam$" + key, sb);
                    Tag closeTag = Tag.find("endparam$" + key, sb);
                    while (openTag != null && closeTag != null) {
                        sb.replace(openTag.getBeginIndex(), closeTag
                                .getEndIndex(), value);
                        openTag = Tag.findNext("beginparam$" + key, sb,
                                closeTag);
                        closeTag = Tag
                                .findNext("endparam$" + key, sb, closeTag);
                    }
                }
            }

        } else {
            if (params != null) {
                for (String value : params.values()) {
                    sb.append(value);
                }
            }
        }
        out.append(StringUtils.replace(sb, replaceRules));
    }

}
