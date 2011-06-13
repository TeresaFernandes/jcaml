package interpreter;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import symbolTable.VarType;
import symbolTable.Variable;
import CommonClasses.*;
import CommonClasses.Error;

public class Calculator {

	
	static Variable solve(List<SintaxElement> se) throws Error {
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
						v = variableFromElement(second);
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
						v = variableFromElement(second);
						Float value = (Float) v.getValue();
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
						v = variableFromElement(second);
						Boolean value = (Boolean) v.getValue();
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
				Lexem newLex = new Lexem((String) v.getValue());
				newLex.evalue();
				SintaxElement newElement = new SintaxElement(newLex);
				se.add(0, newElement);
			}
			// Operadores binários
			else { // Tratar todas as outras operações aki
				if (second.getId()!=SintaxElementId.OP) {
					Error r = new Error(21);
					r.setExtra(" after " + second.getLexem().getLex() +  "token");
					r.setLine(second.getLexem().getLine());
					throw r;
				}
				SintaxElement third = se.remove(0); // Remove o terceiro
				// Olhar o tipo do operador
				VarType type = operatorType(second);
				
				if (type!=VarType.UNKNOWN) { // Operador NÃO depende dos operandos (igual à, e diferente de)
					if (typeFromLex(first.getLexem())!=type) { // Se o tipo do primeiro for diferente do operador 
						Error r = new Error(20);
						r.setExtra(" " + second.getLexem().getLex() + " Expected " + type + " got " + typeFromLex(second.getLexem()));
						r.setLine(first.getLexem().getLine());
						throw r;
					}
					if (typeFromLex(second.getLexem())!=type) { // Verificação do tipo do segundo
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
				Variable v1 = variableFromElement(first);
				Variable v2 = variableFromElement(third);
				switch (second.getLexem().getId()) {
					case OPERATOR_AND:
						v.setValue((Boolean)v1.getValue() && (Boolean)v2.getValue()); 
						break;
					case OPERATOR_OR:
						v.setValue((Boolean)v1.getValue() || (Boolean)v2.getValue()); 
						break;

					case OPERATOR_EQUAL: // Fazer depois
					case OPERATOR_NONEQUAL: // Fazer depois TODO 
						break;
					
					case OPERATOR_GREATEREQUALTHAN:
						if (type==VarType.FLOAT_TYPE) v.setValue((Float)v1.getValue() >= (Float)v2.getValue());
						else v.setValue((Integer)v1.getValue() >= (Integer)v2.getValue());
						break;

					case OPERATOR_GREATERTHAN:
						if (type==VarType.FLOAT_TYPE) v.setValue((Float)v1.getValue() > (Float)v2.getValue());
						else v.setValue((Integer)v1.getValue() > (Integer)v2.getValue());
						break;

					case OPERATOR_LESSEQUALTHAN:
						if (type==VarType.FLOAT_TYPE) v.setValue((Float)v1.getValue() <= (Float)v2.getValue());
						else v.setValue((Integer)v1.getValue() <= (Integer)v2.getValue());
						break;

					case OPERATOR_LESSTHAN:
						if (type==VarType.FLOAT_TYPE) v.setValue((Float)v1.getValue() < (Float)v2.getValue());
						else v.setValue((Integer)v1.getValue() < (Integer)v2.getValue());
						break;
						
					case OPERATOR_LISTAPPEND:
						// TODO lista é complicado
					case OPERATOR_LISTCONCAT:
						// TODO lista de novo
						break;
						
					case OPERATOR_MOD: 
						v.setValue((Integer)v1.getValue() % (Integer)v2.getValue());
						break;
						
					case OPERATOR_MUL:
						if (type==VarType.FLOAT_TYPE) v.setValue((Float)v1.getValue() * (Float)v2.getValue());
						else v.setValue((Integer)v1.getValue() * (Integer)v2.getValue());
						break;

					case OPERATOR_DIV: 
						if (type==VarType.FLOAT_TYPE) v.setValue((Float)v1.getValue() / (Float)v2.getValue());
						else v.setValue((Integer)v1.getValue() / (Integer)v2.getValue());
						break;
					
					case OPERATOR_POW:
						v.setValue(Math.pow((Float)v1.getValue(), (Float)v2.getValue()));
						break;
						
					case OPERATOR_SUB:
						if (type==VarType.FLOAT_TYPE) v.setValue((Float)v1.getValue() - (Float)v2.getValue());
						else v.setValue((Integer)v1.getValue() - (Integer)v2.getValue());
						break;

					case OPERATOR_SUM:
						if (type==VarType.FLOAT_TYPE) v.setValue((Float)v1.getValue() + (Float)v2.getValue());
						else v.setValue((Integer)v1.getValue() + (Integer)v2.getValue());
						break;

					case OPERATOR_STRCONCAT:
						v.setValue((String)v1.getValue() + (String)v2.getValue());
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
		}
		return variableFromElement(se.get(0));
	}
	

	private static Variable variableFromElement(SintaxElement sintaxElement) throws Error {
		//System.out.println("Coisa : " + sintaxElement);
		Variable v = new Variable("");
		Lexem l = sintaxElement.getLexem();
		l.evalue();
		//System.out.println(l.getId());
		v.setType(typeFromLex(l));
		v.setValue(l.getLex());
		return v;
	}


	private static VarType typeFromLex(Lexem l) {
		switch (l.getId()) {
			case BOOL_VALUE: return VarType.BOOL_TYPE;
			case FLOAT_VALUE: return VarType.FLOAT_TYPE;
			case CHAR_VALUE: return VarType.CHAR_TYPE;
			case INT_VALUE: return VarType.INT_TYPE;
			case LIST: return VarType.LIST_TYPE;
			case STRING_VALUE: return VarType.STRING_TYPE;
		}
		return null;
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
			case OPERATOR_STRCONCAT:
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