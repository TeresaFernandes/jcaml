package interpreter;

import java.util.LinkedList;
import java.util.List;

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
		
//		LinkedList<SintaxElement> l = (LinkedList<SintaxElement>) exp.getLexems();
		
		SintaxElement current = exp;
/*		boolean control = (exp.getId()==SintaxElementId.ID || exp.getId()==SintaxElementId.CONST || exp.getId()==SintaxElementId.CHAMADA_FUNCAO);
		while (control || (l!=null && !l.isEmpty())) {
			// Verificar, se o getLexem for null, é pq é um elemento sintático composto
			// se o getLexem não for null, é um elemento simples, e 
			if (control) {
				current = exp;
				control = false;
			}
			else {
				current = l.removeFirst(); 
			}*/
			Variable reference;
			switch (current.getId()) {
				case PROGRAMA:
					for (int a=0;a<current.getLexems().size();a++) {
						r = evalue(scope,current.getLexems().get(a));
						if (r!=null) System.out.println(r);
					}
					return r;
			
				case DEFINICAO:
					// Print no scopo
					/*for (int a=0;a<scope.table.size();a++) {
						System.out.println(scope.table.get(a).getName());
					}*/
					return evalue(scope,current.getLexems().get(0));

				case CHAMADA_FUNCAO:
					return functionCall(scope.clone(), current); 
				
				case DEF_FUNCAO: // Cria uma variável do tipo função
					Variable v = new Variable("");
					v.setAux(getFormalParameters(current));
					v.setValue(current.getLexems().get(5));
					v.setType(VarType.FUNCTION_TYPE); // Tem que arrumar o tipo depois pra n dar pau
					return v;
					
				case DEFINICAO_GLOBAL:
					// Nome da variável está na posição 1 da lista, ai basta pegar o lex dele
					String varName = current.getLexems().get(1).getLexem().getLex();
					Variable var = scope.getElement(varName); // Variável que vai ser alterada na tabela de simbolos
					// O escopo em definição de funções não é importante
					// Mas vou colocar só pra evitar problemas
					reference = evalue(scope,current.getLexems().get(3));
					
					var.setType(reference.getType());
					var.setAux(reference.getAux());
					var.setValue(reference.getValue());
					return var;
					
				case DEFINICAO_LOCAL:
					// Copia o escopo
					Table newScope = scope.clone();
					// Cria a nova variável para ser adicionada no escopo
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
					break;
					
				case IF:
					Variable value = evalue(scope,current.getLexems().get(1));
					if (value.getType()!=VarType.BOOL_TYPE) {
						Error error = new Error(15);
						error.setLine(current.getLexems().get(0).getLexem().getLine());
						throw error;
					}
					// Se o valor de value for true executa o E da posição 3, senão o 5
					Boolean branchControl = ((String) value.getValue()).compareTo("true")==0;
					return evalue(
							scope,
							current.getLexems().get(
									(branchControl==true? 3 : 5)
								)
							);
					
				case MATCH: 
					//Variable match_v = evalue(scope,current.getLexems().get(1));
					// TODO arrumar aqui depois, tem que verificar os tipos de todos os <e>
					break;
					
				case ID:
					String vName = current.getLexem().getLex();
					r = scope.getElement(vName);
					break;
					
				case EXP:
					// TODO FAZER ISSO URGENTE
					//System.out.println("Pobrema aki");
					// Continua para pegar a conjunto de instruções abaixo, faz a mesma coisa
				case EXP_SIMPLES:
					// TODO
					// Verificar todos os tipos, eles têm de ser iguais
					// Verificar enquanto opera, para ganhar tempo
					// Os operadores também devem ser compatíveis
					
					// Transformar tudo em valores CONST, e passar para o solve
					List<SintaxElement> operandos = current.getLexems();
					List<SintaxElement> operandosConst = new LinkedList<SintaxElement>();
					for (int a=0; a<operandos.size();a++) {
						SintaxElement fse = operandos.get(a);

						// Se não for operador e nem consta, transformar para CONST
						if (operandos.get(a).getId()!=SintaxElementId.OP && operandos.get(a).getId()!=SintaxElementId.CONST) {
							//System.out.println(operandos.get(a));							
							Variable vr = evalue(scope,operandos.get(a));
							//System.out.println(vr);
							if (vr.getType()==VarType.FUNCTION_TYPE) {
								Error error = new Error(23);
								error.setExtra(" " +vr.getName() + " is a function. Are you trying to override " + vr.getName() + "?");
								throw error;
							}
							String str = (String) vr.getValue();
							Lexem lex = new Lexem(str);
							lex.evalue();
							fse = new SintaxElement(lex);
							// Checar
							if (fse.getId()!=SintaxElementId.CONST) {
								System.out.println("Erro, tipo do elemento é " + fse.getId());
							}
						}
						// Se for operador ele só faz jogar aqui dentro
						operandosConst.add(fse);
					}
					
					// Passando para o solve
					r = Calculator.solve(operandosConst);
					break;
					
				case E:
					//System.out.println(current.getFirstLexem().getId());
					return evalue(scope,current.getFirstLexem());
			}
		//}
		
		return r;
	}
	
	// Deve retornar um valor
	public static Variable functionCall(Table scope, SintaxElement funCall) throws Error {
		List<SintaxElement> funlist = funCall.getLexems();

		try {
			// Detectar a função
			Variable funcao = scope.getElement(funlist.get(0).getLexem().getLex());
			
			// Para função pre-definida
			if (funcao.getType()==VarType.DEFAULTFUNCTION_TYPE) {
				// Passar o escopo, os parametros reais e o nome da função
				if (funCall.getLexems().get(2).getId()==SintaxElementId.PAR_REAIS) {
					return defaultFunctionCall(scope.clone(), getRealParameters(funCall), funcao.getName());
				}
				else {
					Error r = new Error(14);
					r.setExtra(": " + funcao.getName());
					throw r;
				}
			}
			
			List<SintaxElement> formalParameters = funcao.getAux();
			List<SintaxElement> realParameters = getRealParameters(funCall);
			
			if (formalParameters.size()!=realParameters.size()) {
				Error r = new Error(16);
				r.setExtra(": "+funcao.getName() + " expected "+formalParameters.size()+" got "+realParameters.size());
				throw r;
			}
			// Se os parametros estiverem ok, ir colocando na cópia do scope, e verificando os tipos
			Table newScope = scope.clone();
			Variable var; Variable realVar;
			for (int c=0;c<realParameters.size();c++) {
				var = createVarFromParameter(newScope, formalParameters.get(c));
				realVar = evalue(newScope,realParameters.get(c));
				// Se os tipos forem incompatíveis, e o tipo da variável real já tiver sido determinado
				// * lembrando que os tipos formais já são estabelecidos préviamente
				if (realVar.getType() != var.getType() && realVar.getType()!=VarType.UNKNOWN) {
					Error r = new Error(18);
					r.setExtra(" expected " +var.getType() + " got " + realVar.getType());
					throw r;
				}
				var.setValue(realVar.getValue());
				newScope.insert(var);
			}
			
			// Quando o meliante sobre-escrever o nome da função
			if (funcao.getType()!=VarType.FUNCTION_TYPE) {
				Error r = new Error(24);
				r.setExtra(funCall.getFirstLexem().getLexem().getLex() + ". If you are trying to use a variable with this name in function's scope, rename it.");
				throw r;
			}
			SintaxElement funExp = (SintaxElement) funcao.getValue();
			return evalue(newScope,funExp);
			// TODO testar
			
		} catch (Error e) {
			e.setLine(funlist.get(0).getLexem().getLine());
			throw e;
		}
	}
	
	private static Variable createVarFromParameter(Table scope, SintaxElement sintaxElement) {
		// Criar a variável a partir de um parametro formal
		// formato <ID> : <TIPO>, onde cada um desses é um sintaxElement, e tem o lexema correspondente
		Variable v;
		String varName = sintaxElement.getLexems().get(0).getLexem().getLex();
		try { // Se já existir, sobre-escreve essa budega
			v = scope.getElement(varName);
			//System.out.println("peguei a var;");
		} catch (Error e) {
			v = new Variable(varName);
		}
		//System.out.println(sintaxElement.getLexems());
		Lexem type = sintaxElement.getLexems().get(2).getLexem();
		VarType t = VarType.UNKNOWN;
		switch (type.getId()) {
			case TYPE_BOOL: t = VarType.BOOL_TYPE; break;
			case TYPE_CHAR: t = VarType.CHAR_TYPE; break;
			case TYPE_STRING: t = VarType.STRING_TYPE; break;
			case TYPE_INT: t = VarType.INT_TYPE; break;
			case TYPE_FLOAT: t = VarType.FLOAT_TYPE; break;
			case TYPE_LIST: t = VarType.LIST_TYPE; break;
		}
		v.setType(t);
		v.setValue(null);
		return v;
	}

	private static Variable defaultFunctionCall(Table scope, List<SintaxElement> parameters, String name) throws Error {
		if (name.compareToIgnoreCase("abs")==0) {
			Variable v = evalue(scope,parameters.get(0));
			if (v.getType()!=VarType.FLOAT_TYPE || v.getType()!=VarType.INT_TYPE) {
				// TODO PAREI AKI
				//Error r = new Error
			}
			float f = Float.parseFloat((String)v.getValue());
			v.setValue(String.valueOf(Math.abs(f)));
		}
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
				}
			}
			System.out.println(l);
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
