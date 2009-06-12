package net.webassembletool.jsf;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

public class UIComponentUtils {
	public final static void renderChildren(FacesContext facesContext,
			UIComponent component) throws IOException {
		if (component.getChildCount() > 0) {
			for (Iterator it = component.getChildren().iterator(); it.hasNext();) {
				UIComponent child = (UIComponent) it.next();
				renderChild(facesContext, child);
			}
		}
	}

	public final static void renderChild(FacesContext facesContext,
			UIComponent child) throws IOException {
		if (!(child.isRendered())) {
			return;
		}
		child.encodeBegin(facesContext);
		if (child.getRendersChildren()) {
			child.encodeChildren(facesContext);
		} else {
			renderChildren(facesContext, child);
		}
		child.encodeEnd(facesContext);
	}

	public final static String renderChildrenToString(FacesContext context,
			UIComponent component) throws IOException {
		ResponseWriter initialWriter = context.getResponseWriter();
		StringWriter stringWriter = new StringWriter();
		ResponseWriter newWriter = initialWriter.cloneWithWriter(stringWriter);
		context.setResponseWriter(newWriter);
		renderChildren(context, component);
		context.setResponseWriter(initialWriter);
		return stringWriter.toString();
	}

}
