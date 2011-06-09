package interpreter;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import CommonClasses.*;
import CommonClasses.Error;
import symbolTable.Table;
import symbolTable.VarType;
import symbolTable.Variable;

public class ExpressionEvaluator {

	static public Variable evalue(Table scope, SintaxElement exp) throws Error {
		Variable r = null;
		
		/*if (exp.getId()!= SintaxElementId.E) {
			throw new Error(10);
		}*/
		
		LinkedList<SintaxElement> l = (LinkedList<SintaxElement>) exp.getLexems();
		Stack<Lexem> lex = new Stack<Lexem>();
		
		SintaxElement current;
		while (!l.isEmpty()) {
			current = l.getFirst();
			Variable reference;
			switch (current.getId()) {
				case CHAMADA_FUNCAO:
					return functionCall(scope.clone(), current); 
				
				case DEF_FUNCAO: // Cria uma vari�vel do tipo fun��o
					Variable v = new Variable("");
					v.setType(VarType.FUNCTION_TYPE);
					v.setAux(getFormalParameters(current));
					v.setValue(current.getLexems().get(5));
					return v;
					
				case DEFINICAO_GLOBAL: 
					// Nome da vari�vel est� na posi��o 1 da lista, ai basta pegar o lex dele
					String varName = current.getLexems().get(1).getLexem().getLex();
					Variable var = scope.getElement(varName); // Vari�vel que vai ser alterada na tabela de simbolos
					// O escopo em defini��o de fun��es n�o � importante
					// Mas vou colocar s� pra evitar problemas
					reference = evalue(scope,current.getLexems().get(3));
					var.setType(reference.getType());
					var.setAux(reference.getAux());
					var.setValue(reference.getValue());
					return var;
					
				case DEFINICAO_LOCAL:
					// Copia o escopo
					Table newScope = scope.clone();
					// Cria a nova vari�vel para ser adicionada no escopo
					// com o nome adequado
					Variable newVar = new Variable(current.getLexems().get(1).getLexem().getLex());
					// Agora arruma o valor dela
					reference = evalue(scope,current.getLexems().get(3));
					newVar.setType(reference.getType());
					newVar.setAux(reference.getAux());
					newVar.setValue(reference.getValue());
					// Insere
					newScope.insert(newVar);
					// Chama recursivamente
					return evalue(newScope,current.getLexems().get(5));
					
				case CONST:
					r = new Variable("");
					r.setValue(current.getLexem().getLex());
					switch (current.getLexem().getId()) {
						case BOOL_VALUE:
							r.setType(VarType.BOOL_TYPE);
							break;
						case CHAR_VALUE:
							r.setType(VarType.CHAR_TYPE);
							break;
						case INT_VALUE:
							r.setType(VarType.INT_TYPE);
							break;
						case FLOAT_VALUE:
							r.setType(VarType.FLOAT_TYPE);
							break;
						case STRING_VALUE:
							r.setType(VarType.STRING_TYPE);
							break;
						case LIST:
							r.setType(VarType.LIST_TYPE);
							// Aqui o valor tem que ser alterado
							r.setValue(parseList(current.getLexem().getLex()));
							break;
					}
					return r;
					
				case IF:
					Variable value = evalue(scope,current.getLexems().get(1));
					if (value.getType()!=VarType.BOOL_TYPE) {
						Error error = new Error(15);
						error.setLine(current.getLexems().get(0).getLexem().getLine());
						throw error;
					}
					// Se o valor de value for true 
					return evalue(
							scope,
							current.getLexems().get(
									(Boolean.getBoolean((String) value.getValue())? 3 : 5)
								)
							);
					
				case MATCH: 
					Variable match_v = evalue(scope,current.getLexems().get(1));
					// TODO arrumar aqui depois, tem que verificar os tipos de todos os <e>
					break;
			}
		}
		
		return r;
	}
	
	// Deve retornar um valor
	public static Variable functionCall(Table scope, SintaxElement fun) throws Error {
		List<SintaxElement> list = fun.getLexems();

		try {
			// Detectar a fun��o
			Variable funcao = scope.getElement(list.get(0).getLexem().getLex());
			
			// Para fun��o pre-definida
			if (funcao.getType()==VarType.DEFAULTFUNCTION_TYPE) {
				// Passar o escopo, os parametros reais e o nome da fun��o
				if (fun.getLexems().get(2).getId()==SintaxElementId.PAR_REAIS) {
					return defaultFunctionCall(scope, getRealParameters(fun), funcao.getName());
				}
				else {
					Error r = new Error(14);
					r.setExtra(": " + funcao.getName());
				}
			}
			
			List<SintaxElement> parameters = funcao.getAux();
			
			// Criar os elementos parametro na table
			Variable v;
			for (int a=0; a<parameters.size(); a+=2) {
				// Nome da vari�vel
				v = new Variable(parameters.get(a).getLexems().get(0).getLexem().getLex());
				// Tipo da vari�vel
				v.setType(typeFromLex(parameters.get(a).getLexems().get(2).getLexem()));
				// Insere a vari�vel no escopo da fun��o
				scope.insert(v);
			}
			// Agora pego os parametros reais passados
			// Come�am em 2 e v�o at� tamanho-2 (o ultimo elemento � um ')' )
			for (int parR=2, parF=0;parR<fun.getLexems().size()-2; parR++, parF++) {
				// Pegar o parametro real e colocar no espa�o de endere�amendo do parametro formal
				// TODO d� pra unir esses dois for
			}
			
		} catch (Error e) {
			e.setLine(list.get(0).getLexem().getLine());
			throw e;
		}
		return null;
	}
	
	private static Variable defaultFunctionCall(Table scope, List<SintaxElement> parameters, String name) {
		if (name.compareToIgnoreCase("abs")==0) {}
		else if (name.compareToIgnoreCase("ceil")==0) {}
		else if (name.compareToIgnoreCase("floor")==0) {}
		else if (name.compareToIgnoreCase("sqrt")==0) {}
		else if (name.compareToIgnoreCase("exp")==0) {}
		
		else if (name.compareToIgnoreCase("length")==0) {}
		else if (name.compareToIgnoreCase("get")==0) {}
		else if (name.compareToIgnoreCase("uppercase")==0) {}
		else if (name.compareToIgnoreCase("lowercase")==0) {}
		
		else if (name.compareToIgnoreCase("int_of_char")==0) {}
		else if (name.compareToIgnoreCase("int_of_string")==0) {}
		else if (name.compareToIgnoreCase("int_of_float")==0) {}
		else if (name.compareToIgnoreCase("int_of_char")==0) {}
		
		else if (name.compareToIgnoreCase("char_of_int")==0) {}
		
		else if (name.compareToIgnoreCase("float_of_string")==0) {}
		else if (name.compareToIgnoreCase("float_of_int")==0) {}
		
		else if (name.compareToIgnoreCase("string_of_bool")==0) {}
		else if (name.compareToIgnoreCase("string_of_char")==0) {}
		else if (name.compareToIgnoreCase("string_of_int")==0) {}
		else if (name.compareToIgnoreCase("string_of_float")==0) {}
		else if (name.compareToIgnoreCase("string_of_list")==0) {}

		else if (name.compareToIgnoreCase("read_int")==0) {}
		else if (name.compareToIgnoreCase("read_float")==0) {}
		else if (name.compareToIgnoreCase("read_string")==0) {}
		else if (name.compareToIgnoreCase("read_char")==0) {}
		
		else if (name.compareToIgnoreCase("print_int")==0) {}
		else if (name.compareToIgnoreCase("print_float")==0) {}
		else if (name.compareToIgnoreCase("print_string")==0) {}
		else if (name.compareToIgnoreCase("print_char")==0) {}
		
		// TODO
		return null;
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
	
	public static Object valueFromLexem(Lexem l) throws Error {
		switch (l.getId()) {
		case BOOL_VALUE: return Boolean.parseBoolean(l.getLex());
		case FLOAT_VALUE: return Float.parseFloat(l.getLex());
		case INT_VALUE: return Integer.parseInt(l.getLex());
		case LIST: return parseList(l.getLex());
		case CHAR_VALUE: return l.getLex().replace("\'",""); // Remover as aspas simples
		case STRING_VALUE: return l.getLex();
	}
	return null;
	}
	
	public static List parseList(String s) throws Error {
		// [ n,m,j,k,l ]
		// Remover o primeiro e o ultimo elementos
		s = s.substring(1, s.length()-1);
		String[] elements = s.split(",");
		if (elements.length==0) return new LinkedList<Lexem>();
		LinkedList<Lexem> lexems = new LinkedList<Lexem>();
		Lexem l;
		for (int a=0; a<elements.length; a++) {
			l = new Lexem(elements[a]);
			l.evalue();
			lexems.add(l);
		}
		// Verificar se todos tem o mesmo tipo
		//enquanto isso, vou fazendo o parsing de cada elemento, e colocando numa nova lista
		LinkedList list = new LinkedList();
		LexemId id = lexems.get(0).getId();
		for (int a=0; a<lexems.size(); a++) {
			if (id != lexems.get(a).getId()) {
				Error r = new Error(12);
				r.setExtra(". Expected " + id + " found " + lexems.get(a).getId() + ": "+ lexems.get(a).getLex() );
				throw r;
			}
			list.add(valueFromLexem(lexems.get(a)));
		}
		return list;
	}
	
	private static List<SintaxElement> getFormalParameters(SintaxElement fun) {
		List<SintaxElement> l = new LinkedList<SintaxElement>();
		
		SintaxElement par = fun.getLexems().get(2);
		if (par.getId()==SintaxElementId.PAR_FORMAIS) {
			// Coloca os parametros na lista
			l = par.getLexems();
			// Limpar a lista, remover as virgulas
			for (int a=0;a<l.size();a++) {
				if (l.get(a).getId()==SintaxElementId.COMMA) {
					l.remove(a);
					a--;
				}
			}
		}		
		return l;
	}

	// Retorna lista de parametros reais
	private static List<SintaxElement> getRealParameters(SintaxElement fun) {
		List<SintaxElement> l = new LinkedList<SintaxElement>();
		// TODO depois
		SintaxElement par = fun.getLexems().get(2);
		if (par.getId()==SintaxElementId.PAR_REAIS) {
			l = par.getLexems();
			// Limpar a lista, remover as virgulas
			for (int a=0;a<l.size();a++) {
				if (l.get(a).getId()==SintaxElementId.COMMA) {
					l.remove(a);
					a--;
				}
			}
		}

		return l;
	}
	
	private static List<Variable> getMatchVars(SintaxElement match) {
		List<Variable> vars = new LinkedList<Variable>();
		List<SintaxElement> match_lines = match.getLexems().get(3).getLexems();
		// TODO depois
		return vars;
	}
}
