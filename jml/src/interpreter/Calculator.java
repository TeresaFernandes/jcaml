package interpreter;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import symbolTable.VarType;
import symbolTable.Variable;
import CommonClasses.*;
import CommonClasses.Error;

public class Calculator {

	
	static Variable solve(List<SintaxElement> se) {
		// Esta lista contém apenas CONST e OP
		Variable result = new Variable("");
		
		// Colocar tudo na forma pós-fixa, pra facilitar minha vida
		Stack<SintaxElement> pilha = new Stack<SintaxElement>();

		SintaxElement e;
		while (!se.isEmpty()) {
			e = se.remove(0);
			
		}
		
		return result;
	}
	

	private static VarType typeFromLex(Lexem l) {
		switch (l.getId()) {
			case TYPE_BOOL: return VarType.BOOL_TYPE;
			case TYPE_FLOAT: return VarType.FLOAT_TYPE;
			case TYPE_CHAR: return VarType.CHAR_TYPE;
			case TYPE_INT: return VarType.INT_TYPE;
			case TYPE_LIST: return VarType.LIST_TYPE;
			case TYPE_STRING: return VarType.STRING_TYPE;
		}
		return null;
	}


	private static VarType operatorType(SintaxElement e) throws Error {
		Lexem l = e.getLexem();
		switch (l.getId()) {
			case OPERATOR_AND: return VarType.BOOL_TYPE;
			case OPERATOR_EQUAL: return VarType.BOOL_TYPE;
			case OPERATOR_GREATEREQUALTHAN: return VarType.BOOL_TYPE;
			case OPERATOR_GREATERTHAN: return VarType.BOOL_TYPE;
			case OPERATOR_LESSEQUALTHAN: return VarType.BOOL_TYPE;
			case OPERATOR_LESSTHAN: return VarType.BOOL_TYPE;
			case OPERATOR_NONEQUAL: return VarType.BOOL_TYPE;
			case OPERATOR_NOT: return VarType.BOOL_TYPE;
			case OPERATOR_OR: return VarType.BOOL_TYPE;
			case OPERATOR_POW: return VarType.BOOL_TYPE;
			case OPERATOR_LISTAPPEND: return VarType.LIST_TYPE;
			case OPERATOR_LISTCONCAT: return VarType.LIST_TYPE;
			case OPERATOR_MOD: return VarType.INT_TYPE;
			case OPERATOR_STRCONCAT: return VarType.STRING_TYPE;
			case OPERATOR_MUL:    
			case OPERATOR_DIV: 
			case OPERATOR_SUB:
			case OPERATOR_SUM:
			case OPERATOR_UNARYMINUS: return (l.getLex().endsWith(".")? VarType.FLOAT_TYPE : VarType.INT_TYPE);
			default : return VarType.UNKNOWN;
		}
	}
}