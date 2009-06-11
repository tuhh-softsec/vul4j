package net.webassembletool.jsf;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.servlet.http.HttpServletRequest;

import net.webassembletool.DriverFactory;
import net.webassembletool.RenderingException;
import net.webassembletool.taglib.ReplaceableTag;

import org.apache.myfaces.renderkit.RendererUtils;

public class IncludeTemplateComponent extends UIComponentBase implements
		ReplaceableTag {
	private String name;
	private String page;
	private String provider;
	private Boolean displayErrorPage;
	private Map<String, String> replaceRules = new HashMap<String, String>();
	private Map<String, String> params = new HashMap<String, String>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public boolean isDisplayErrorPage() {
		return displayErrorPage;
	}

	public void setDisplayErrorPage(boolean displayErrorPage) {
		this.displayErrorPage = displayErrorPage;
	}

	@Override
	public String getFamily() {
		return IncludeTemplateComponent.class.getPackage().toString();
	}

	@Override
	public void encodeEnd(FacesContext context) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		HttpServletRequest request = (HttpServletRequest) context
				.getExternalContext().getRequest();
		try {
			DriverFactory.getInstance(getProvider()).renderTemplate(getPage(),
					getName(), writer, request, params, replaceRules, null,
					false);
		} catch (RenderingException re) {
			if (isDisplayErrorPage())
				writer.write(re.getMessage());
		}
	}

	@Override
	public void encodeChildren(FacesContext context) throws IOException {
		Iterator it = getChildren().iterator();
		while (it.hasNext()) {
			UIComponent child = (UIComponent) it.next();
			RendererUtils.renderChild(context, child);
			if (child instanceof ReplaceComponent) {
				ReplaceComponent rc = (ReplaceComponent) child;
				replaceRules.put(rc.getExpression(), rc.getValue());
			}
			if (child instanceof IncludeParamComponent) {
				IncludeParamComponent ip = (IncludeParamComponent) child;
				params.put(ip.getName(), ip.getValue());
			}
		}

	}

	public Map<String, String> getReplaceRules() {
		return replaceRules;
	}

	@Override
	public boolean getRendersChildren() {
		return true;
	}

	public void restoreState(FacesContext context, Object state) {
		Object[] values = (Object[]) state;
		super.restoreState(context, values[0]);
		page = (String) values[1];
		name = (String) values[2];
		provider = (String) values[3];
		displayErrorPage = (Boolean) values[4];
	}

	public Object saveState(FacesContext context) {
		Object[] values = new Object[5];
		values[0] = super.saveState(context);
		values[1] = page;
		values[2] = name;
		values[3] = provider;
		values[4] = displayErrorPage;
		return ((Object) (values));
	}

}
