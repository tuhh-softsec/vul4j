package org.esigate.jsf;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.esigate.DriverFactory;
import org.esigate.HttpErrorPage;
import org.esigate.servlet.HttpRequestImpl;
import org.esigate.servlet.HttpResponseImpl;
import org.esigate.taglib.ReplaceableTag;

public class IncludeTemplateComponent extends UIComponentBase implements
		ReplaceableTag {
	private Boolean displayErrorPage;
	private String name;
	private String page;
	private final Map<String, String> params = new HashMap<String, String>();
	private String provider;
	private final Map<String, String> replaceRules = new HashMap<String, String>();

	@Override
	public void encodeChildren(FacesContext context) throws IOException {
		@SuppressWarnings("rawtypes")
		Iterator it = getChildren().iterator();
		while (it.hasNext()) {
			UIComponent child = (UIComponent) it.next();
			UIComponentUtils.renderChild(child);
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

	@Override
	public void encodeEnd(FacesContext context) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		HttpServletRequest request = (HttpServletRequest) context
				.getExternalContext().getRequest();
		HttpServletResponse response = (HttpServletResponse) context
				.getExternalContext().getResponse();
		try {
			DriverFactory.getInstance(getProvider()).renderTemplate(getPage(),
					getName(), writer, HttpRequestImpl.wrap(request),
					HttpResponseImpl.wrap(response), params, replaceRules,
					null, false);
		} catch (HttpErrorPage re) {
			if (isDisplayErrorPage()) {
				writer.write(re.getMessage());
			}
		}
	}

	@Override
	public String getFamily() {
		return IncludeTemplateComponent.class.getPackage().toString();
	}

	public String getName() {
		return UIComponentUtils.getParam(this, "name", name);
	}

	public String getPage() {
		return UIComponentUtils.getParam(this, "page", page);
	}

	public String getProvider() {
		return UIComponentUtils.getParam(this, "provider", provider);
	}

	@Override
	public boolean getRendersChildren() {
		return true;
	}

	public Map<String, String> getReplaceRules() {
		return replaceRules;
	}

	public boolean isDisplayErrorPage() {
		return UIComponentUtils.getParam(this, "displayErrorPage",
				displayErrorPage);
	}

	@Override
	public void restoreState(FacesContext context, Object state) {
		Object[] values = (Object[]) state;
		super.restoreState(context, values[0]);
		page = (String) values[1];
		name = (String) values[2];
		provider = (String) values[3];
		displayErrorPage = (Boolean) values[4];
	}

	@Override
	public Object saveState(FacesContext context) {
		Object[] values = new Object[5];
		values[0] = super.saveState(context);
		values[1] = page;
		values[2] = name;
		values[3] = provider;
		values[4] = displayErrorPage;
		return values;
	}

	public void setDisplayErrorPage(boolean displayErrorPage) {
		this.displayErrorPage = displayErrorPage;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

}
