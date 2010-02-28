package render.quantifyit.model.operations;

import render.quantifyit.model.Decimal;

public interface Operation {

	Decimal eval();
	
	<T extends Operation> T precision(final int precision);

}
