package net.webassembletool.jsf;

import java.io.IOException;

import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.apache.myfaces.custom.buffer.HtmlBufferResponseWriterWrapper;
import org.apache.myfaces.renderkit.RendererUtils;

public class ReplaceComponent extends UIComponentBase {
	private String expression;
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

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	@Override
	public String getFamily() {
		return ReplaceComponent.class.getPackage().toString();
	}

	public void restoreState(FacesContext context, Object state) {
		Object[] values = (Object[]) state;
		super.restoreState(context, values[0]);
		expression = (String) values[1];
	}

	public Object saveState(FacesContext context) {
		Object[] values = new Object[2];
		values[0] = super.saveState(context);
		values[1] = expression;
		return ((Object) (values));
	}

}
