package interpreter;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import symbolTable.Table;
import symbolTable.VarType;
import symbolTable.Variable;
import CommonClasses.*;
import CommonClasses.Error;

public class Calculator {

	
	static Variable solve(Table scope, List<SintaxElement> se) throws Error {
		// Esta lista contém apenas CONST e OP
		Variable result = new Variable("");
		
		SintaxElement first;
		SintaxElement second;
		// Associativa a esquerda para facilitar minha vida
		// Tem dois ou mais elementos
		while (se.size()!=1) {
			Variable v = new Variable("");
			first = se.remove(0); // Pegar os dois primeiros para avaliar
			second = se.remove(0);
			// Operadores unários
			if (first.getId()==SintaxElementId.OP && first.getLexem().isUnaryOperator()) {
				Lexem lex = first.getLexem();
				v = new Variable("");
				if (lex.getLex().compareTo("~-")==0) { // Inteiro
					if (typeFromLex(second.getLexem())==VarType.INT_TYPE) {
						v = variableFromElement(scope,second);
						Integer value = (Integer) v.getValue();
						v.setValue(value * -1);
					}
					else {
						Error r = new Error(20);
						r.setExtra(" " + lex.getLex() + " Expected INT_TYPE got " + typeFromLex(second.getLexem()));
						r.setLine(second.getLexem().getLine());
						throw r;
					}
				}
				else if (lex.getLex().compareTo("~-.")==0) { // Float
					if (typeFromLex(second.getLexem())==VarType.FLOAT_TYPE) {
						v = variableFromElement(scope,second);
						Float value = Float.parseFloat((String)v.getValue());
						v.setValue(value * -1);
					}					
					else {
						Error r = new Error(20);
						r.setExtra(" "+ lex.getLex() + " Expected FLOAT_TYPE got " + typeFromLex(second.getLexem()));
						r.setLine(second.getLexem().getLine());
						throw r;
					}
				}
				else if (lex.getLex().compareTo("!")==0) { // Bool
					if (typeFromLex(second.getLexem())==VarType.BOOL_TYPE) {
						v = variableFromElement(scope,second);
						Boolean value = Boolean.valueOf((String)v.getValue());
						v.setValue(!value);
					}
					else {
						Error r = new Error(20);
						r.setExtra(" "+ lex.getLex() + " Expected BOOL_TYPE got " + typeFromLex(second.getLexem()));
						r.setLine(second.getLexem().getLine());
						throw r;
					}
				}
				// Removes os dois valores, e coloca o novo elemento
				Lexem newLex = new Lexem(String.valueOf(v.getValue()));
				newLex.evalue();
				SintaxElement newElement = new SintaxElement(newLex);
				se.add(0, newElement);
				continue;
			}
			// Operadores binários
			else { // Tratar todas as outras operações aki
				if (second.getId()!=SintaxElementId.OP) {
					Error r = new Error(21);
					r.setExtra(" after " + second.getLexem().getLex() +  " token");
					r.setLine(second.getLexem().getLine());
					throw r;
				}
				SintaxElement third = se.remove(0); // Remove o terceiro
				// Olhar o tipo do operador
				VarType type = operatorType(second);
				
				if (type!=VarType.UNKNOWN) { // Operador NÃO depende dos operandos (igual à, e diferente de)
					if (typeFromLex(first.getLexem())!=type) { // Se o tipo do primeiro for diferente do operador 
						Error r = new Error(20);
						r.setExtra(" " + second.getLexem().getLex() + " Expected " + type + " got " + typeFromLex(first.getLexem()));
						r.setLine(first.getLexem().getLine());
						throw r;
					}
					if (typeFromLex(third.getLexem())!=type) { // Verificação do tipo do segundo
						Error r = new Error(20);
						r.setExtra(" " + second.getLexem().getLex() + " Expected " + type + " got " + typeFromLex(second.getLexem()));
						r.setLine(first.getLexem().getLine());
						throw r;
					}
				}
				else { // Tipo do operador depende do tipo dos operandos (igual à e diferente de)
					if (typeFromLex(first.getLexem())!=typeFromLex(third.getLexem())) {
						Error r = new Error(20);
						r.setExtra(" " + second.getLexem().getLex() + " Expected " + typeFromLex(first.getLexem()) + " got " + typeFromLex(second.getLexem()));
						r.setLine(first.getLexem().getLine());
						throw r;
					}
				}
				
				// Se passou tudo, tá tudo certo, é só operar
				v = new Variable("");
				v.setType(type);
				
				/* 
				 * Note que o valor de value vai variar de acordo com o tipo,
				 * mas isso não dá problema pq eu sempre faço cast quando precisa 
				*/
				Variable v1 = variableFromElement(scope,first);
				Variable v2 = variableFromElement(scope,third);
				switch (second.getLexem().getId()) {
					case OPERATOR_AND:
						v.setValue(String.valueOf(Boolean.parseBoolean((String)v1.getValue()) && Boolean.parseBoolean((String)v2.getValue()))); 
						break;
					case OPERATOR_OR:
						v.setValue(String.valueOf(Boolean.parseBoolean((String)v1.getValue()) || Boolean.parseBoolean((String)v2.getValue()))); 
						break;

					case OPERATOR_EQUAL:
						v.setValue(String.valueOf(0==((String)v1.getValue()).compareTo((String)v2.getValue())));
						break;
					case OPERATOR_NONEQUAL: // Fazer depois TODO 
						v.setValue(String.valueOf(((String)v1.getValue()).compareTo((String)v2.getValue())!=0));
						break;
					
					case OPERATOR_GREATEREQUALTHAN:
						if (type==VarType.FLOAT_TYPE) v.setValue(String.valueOf(Float.parseFloat((String)v1.getValue()) >= Float.parseFloat((String)v2.getValue())));
						else v.setValue(String.valueOf(Integer.parseInt((String)v1.getValue()) >= Integer.parseInt((String)v2.getValue())));
						break;

					case OPERATOR_GREATERTHAN:
						if (type==VarType.FLOAT_TYPE) v.setValue(String.valueOf(Float.parseFloat((String)v1.getValue()) > Float.parseFloat((String)v2.getValue())));
						else v.setValue(String.valueOf(Integer.parseInt((String)v1.getValue()) > Integer.parseInt((String)v2.getValue())));
						break;

					case OPERATOR_LESSEQUALTHAN:
						if (type==VarType.FLOAT_TYPE) v.setValue(String.valueOf(Float.parseFloat((String)v1.getValue()) <= Float.parseFloat((String)v2.getValue())));
						else v.setValue(String.valueOf(Integer.parseInt((String)v1.getValue()) <= Integer.parseInt((String)v2.getValue())));
						break;

					case OPERATOR_LESSTHAN:
						if (type==VarType.FLOAT_TYPE) v.setValue(String.valueOf(Float.parseFloat((String)v1.getValue()) < Float.parseFloat((String)v2.getValue())));
						else v.setValue(String.valueOf(Integer.parseInt((String)v1.getValue()) < Integer.parseInt((String)v2.getValue())));
						break;
						
					case OPERATOR_LISTAPPEND:
						// TODO lista é complicado
					case OPERATOR_LISTCONCAT:
						// TODO lista de novo
						break;
						
					case OPERATOR_MOD: 
						v.setValue(String.valueOf(Integer.parseInt((String)v1.getValue()) % Integer.parseInt((String)v2.getValue())));
						break;
						
					case OPERATOR_MUL:
						if (type==VarType.FLOAT_TYPE) v.setValue(String.valueOf(Float.parseFloat((String)v1.getValue()) * Float.parseFloat((String)v2.getValue())));
						else v.setValue(String.valueOf(Integer.parseInt((String)v1.getValue()) * Integer.parseInt((String)v2.getValue())));
						break;

					case OPERATOR_DIV: 
						if (type==VarType.FLOAT_TYPE) v.setValue(String.valueOf(Float.parseFloat((String)v1.getValue()) / Float.parseFloat((String)v2.getValue())));
						else v.setValue(String.valueOf(Integer.parseInt((String)v1.getValue()) / Integer.parseInt((String)v2.getValue())));
						break;
					
					case OPERATOR_POW:
						v.setValue(Math.pow(Float.parseFloat((String)v1.getValue()), Float.parseFloat((String)v2.getValue())));
						break;
						
					case OPERATOR_SUB:
						if (type==VarType.FLOAT_TYPE) v.setValue(String.valueOf(Float.parseFloat((String)v1.getValue()) - Float.parseFloat((String)v2.getValue())));
						else v.setValue(String.valueOf(Integer.parseInt((String)v1.getValue()) - Integer.parseInt((String)v2.getValue())));
						break;

					case OPERATOR_SUM:
						if (type==VarType.FLOAT_TYPE) v.setValue(String.valueOf(Float.parseFloat((String)v1.getValue()) + Float.parseFloat((String)v2.getValue())));
						else v.setValue(String.valueOf(Integer.parseInt((String)v1.getValue()) + Integer.parseInt((String)v2.getValue())));
						break;

					case OPERATOR_STRCONCAT:
						// Remover a ultima aspa do primeiro e a 
						String s1 = (String)v1.getValue();
						String s2 = (String)v2.getValue();
						v.setValue(s1.substring(0, s1.length()-1) + s2.substring(1,s2.length()));
						break;
						
						// Os dois abaixo dão erro, pois são unários
					case OPERATOR_NOT:
					case OPERATOR_UNARYMINUS:
					default:
						Error r = new Error(21);
						r.setLine(first.getLexem().getLine());
						r.setExtra(" " + second.getLexem().getLex());
						throw r;
				}
			}
			se.add(0,elementFromVariable(v));
		}
		return variableFromElement(scope,se.get(0));
	}
	

	private static SintaxElement elementFromVariable(Variable v) throws Error {
		Lexem l = new Lexem(String.valueOf(v.getValue()));
		l.evalue();
		SintaxElement s = new SintaxElement(l);
		return s;
	}


	private static Variable variableFromElement(Table scope, SintaxElement sintaxElement) throws Error {
		//System.out.println("Coisa : " + sintaxElement);
		Variable v = ExpressionEvaluator.evalue(scope, sintaxElement);
		return v;
	}


	protected static VarType typeFromLex(Lexem l) {
		switch (l.getId()) {
			case BOOL_VALUE: return VarType.BOOL_TYPE;
			case FLOAT_VALUE: return VarType.FLOAT_TYPE;
			case CHAR_VALUE: return VarType.CHAR_TYPE;
			case INT_VALUE: return VarType.INT_TYPE;
			case LIST: return VarType.LIST_TYPE;
			case STRING_VALUE: return VarType.STRING_TYPE;
			default: return VarType.FUNCTION_TYPE;
		}
	}


	private static VarType operatorType(SintaxElement e) throws Error {
		Lexem l = e.getLexem();
		switch (l.getId()) {
			case OPERATOR_AND: return VarType.BOOL_TYPE;
			case OPERATOR_EQUAL: return VarType.UNKNOWN;
			case OPERATOR_NONEQUAL: return VarType.UNKNOWN;
			case OPERATOR_NOT: return VarType.BOOL_TYPE;
			case OPERATOR_OR: return VarType.BOOL_TYPE;
			case OPERATOR_POW: return VarType.BOOL_TYPE;
			case OPERATOR_LISTAPPEND: return VarType.LIST_TYPE;
			case OPERATOR_LISTCONCAT: return VarType.LIST_TYPE;
			case OPERATOR_MOD: return VarType.INT_TYPE;
			case OPERATOR_STRCONCAT: return VarType.STRING_TYPE;
			case OPERATOR_GREATEREQUALTHAN:
			case OPERATOR_GREATERTHAN:
			case OPERATOR_LESSEQUALTHAN:
			case OPERATOR_LESSTHAN:
			case OPERATOR_MUL:    
			case OPERATOR_DIV: 
			case OPERATOR_SUB:
			case OPERATOR_SUM:
			case OPERATOR_UNARYMINUS: return (l.getLex().endsWith(".")? VarType.FLOAT_TYPE : VarType.INT_TYPE);
			default : return VarType.UNKNOWN;
		}
	}
}