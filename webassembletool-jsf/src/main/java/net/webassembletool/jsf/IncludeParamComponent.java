package net.webassembletool.jsf;

import java.io.IOException;

import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.apache.myfaces.custom.buffer.HtmlBufferResponseWriterWrapper;
import org.apache.myfaces.renderkit.RendererUtils;

public class IncludeParamComponent extends UIComponentBase {
	private String name;
	private String value;

	@Override
	public void encodeChildren(FacesContext context) throws IOException {
		ResponseWriter initialWriter = context.getResponseWriter();
		ResponseWriter newWriter = HtmlBufferResponseWriterWrapper
				.getInstance(initialWriter);
		context.setResponseWriter(newWriter);
		RendererUtils.renderChildren(context, this);
		value = newWriter.toString();
		context.setResponseWriter(initialWriter);
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public boolean getRendersChildren() {
		return true;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getFamily() {
		return IncludeParamComponent.class.getPackage().toString();
	}

	public void restoreState(FacesContext context, Object state) {
		Object[] values = (Object[]) state;
		super.restoreState(context, values[0]);
		name = (String) values[1];
	}

	public Object saveState(FacesContext context) {
		Object[] values = new Object[2];
		values[0] = super.saveState(context);
		values[1] = name;
		return ((Object) (values));
	}

}
