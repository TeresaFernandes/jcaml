package interpreter;

import CommonClasses.*;
import CommonClasses.Error;
import symbolTable.Table;
import symbolTable.Variable;

public class ExpressionEvaluator {

	static public Variable evalue(Table scope, SintaxElement exp) throws Error {
		Variable r = null;
		
		if (exp.getId()!= SintaxElementId.E) {
			throw new Error(10);
		}
		
		return r;
	}
}
