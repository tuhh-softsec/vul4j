package net.webassembletool.jsf;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.el.ValueBinding;

public class UIComponentUtils {
	public static Boolean getParam(UIComponent uiComponent, String name,
			Boolean currentValue) {
		if (currentValue != null) {
			return currentValue;
		} else {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			ValueBinding valueBinding = uiComponent.getValueBinding(name);
			if (valueBinding != null) {
				return Boolean.valueOf((String) valueBinding
						.getValue(facesContext));
			}
		}
		return null;
	}

	public final static String getParam(UIComponent uiComponent, String name,
			String currentValue) {
		if (currentValue != null) {
			return currentValue;
		} else {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			ValueBinding valueBinding = uiComponent.getValueBinding(name);
			if (valueBinding != null) {
				return (String) valueBinding.getValue(facesContext);
			}
		}
		return null;
	}

	public final static void renderChild(UIComponent child) throws IOException {
		if (!(child.isRendered())) {
			return;
		}
		FacesContext facesContext = FacesContext.getCurrentInstance();
		child.encodeBegin(facesContext);
		if (child.getRendersChildren()) {
			child.encodeChildren(facesContext);
		} else {
			renderChildren(child);
		}
		child.encodeEnd(facesContext);
	}

	public final static void renderChildren(UIComponent component)
			throws IOException {
		if (component.getChildCount() > 0) {
			@SuppressWarnings("rawtypes")
			Iterator it = component.getChildren().iterator();
			while (it.hasNext()) {
				UIComponent child = (UIComponent) it.next();
				renderChild(child);
			}
		}
	}

	public final static String renderChildrenToString(UIComponent component)
			throws IOException {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ResponseWriter initialWriter = facesContext.getResponseWriter();
		StringWriter stringWriter = new StringWriter();
		ResponseWriter newWriter = initialWriter.cloneWithWriter(stringWriter);
		facesContext.setResponseWriter(newWriter);
		renderChildren(component);
		facesContext.setResponseWriter(initialWriter);
		return stringWriter.toString();
	}

}
