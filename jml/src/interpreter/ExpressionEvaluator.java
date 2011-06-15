package interpreter;

import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;

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
					
				case DEF_TYPE: 
					Variable varType = new Variable(current.getLexems().get(1).getLexem().getLex());
					varType.setType(VarType.USER_TYPE);
					varType.setAux(getFormalParameters(current));
					scope.insert(varType);
					return varType;
					
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
					//JOptionPane.showMessageDialog(null, value);
					// Se o valor de value for true executa o E da posição 3, senão o 5
					Boolean branchControl = ((String) value.getValue()).compareTo("true")==0;
					return evalue(
							scope,
							current.getLexems().get(
									(branchControl==true? 3 : 5)
								)
							);
							
				case MATCH: 			
					
					Variable match_v = evalue(scope,current.getLexems().get(1));//<exp> do match
					SintaxElement s = current.getLexems().get(3);//match_line
					Variable le_v;//match_var
					
					for (int i=0; i<s.getLexems().size();i=i+4){
						
						//se for um "_" não avalia. Jah retorna o resultado
						if (s.getLexems().get(i).getLexems().get(0).getId()!=SintaxElementId.KEYWORD_JOKER){
							le_v =evalue (scope,s.getLexems().get(i).getLexems().get(0));
						}else{
							//verificar se tem o "as"
							if (s.getLexems().get(i).getLexems().size()>2){
								Variable x = new Variable(s.getLexems().get(i).getLexems().get(2).getLexem().getLex());
								x.setValue(match_v.getValue());
								x.setType(match_v.getType());
								x.setAux(match_v.getAux());
								
								Table t = scope.clone();
								t.insert(x);
								return evalue(t, s.getLexems().get(i+2));
								
							}else{
								return evalue (scope,s.getLexems().get(i+2));
							}
						}
													
						if (le_v.getType()==match_v.getType() &&  le_v.getValue().equals(match_v.getValue())) {
							//verificar os tipos de todos os <e>
							return evalue (scope,s.getLexems().get(i+2));
						}
						
						
					}
					
					Error er = new Error(27);
					er.setLine(current.getLexems().get(0).getLexem().getLine());
					throw er;
					
					//break;
					
				case ID:
					if (current.getLexems()!=null) {
						SintaxElement first = current.getLexems().get(0);
						SintaxElement third = current.getLexems().get(2);
						String fieldName = third.getLexem().getLex();
						Variable v1 = Calculator.variableFromElement(scope,first);
						Variable vz = null;
						List l1 = (List)v1.getValue();
						for (int lc=0;lc<l1.size();lc++) {
							if (((Variable)l1.get(lc)).getName().compareTo(fieldName)==0) {
								return (Variable) l1.get(lc);
							}
						}
					} else {
						String vName = current.getLexem().getLex();
						r = scope.getElement(vName);
					}
					break;
					
				case EXP:
					return evalue(scope,current.getFirstLexem());
					
				case EXP_SIMPLES:
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
							//System.out.println("wwwwaaaa "+operandos.get(a));
							//System.out.println(operandos.get(a));
							Variable vr = evalue(scope,operandos.get(a));
							//System.out.println(vr);
							if (vr.getType()==VarType.FUNCTION_TYPE || vr.getType()==VarType.LIST_TYPE || vr.getType()==VarType.CUSTOM_TYPE || vr.getType()==VarType.USER_TYPE) {
								r = vr;
							} else {
								//JOptionPane.showMessageDialog(null, scope.getElement("xs"));
								String str = (String) vr.getValue();
								Lexem lex = new Lexem(str);
								lex.evalue();
								fse = new SintaxElement(lex);
								// Checar
								if (fse.getId()!=SintaxElementId.CONST) {
									System.out.println("Erro, tipo do elemento é " + fse.getId());
								}
							}
						}
						// Se for operador ele só faz jogar aqui dentro
						operandosConst.add(fse);
					}
					r = Calculator.solve(scope,operandosConst);
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
			
			List<SintaxElement> formalParameters = funcao.getAux();
			List<SintaxElement> realParameters = getRealParameters(funCall);
			
			// Detectar instanciação de tipo e fazer malassombra
			if (funcao.getType()==VarType.USER_TYPE) {
				Variable r = new Variable("");
				r.setType(VarType.CUSTOM_TYPE);
				Variable tipo = scope.getElement(funcao.getName());
				//r.setAux(tipo.getAux());
				r.setRealType(funcao.getName());
				// Verificar o tamanho dos parametros
				if (tipo.getAux().size()!=realParameters.size()) {
					Error err = new Error(16);
					err.setExtra(": "+funcao.getName() + " expected "+formalParameters.size()+" got "+realParameters.size());
					throw err;
				} else {
					// Verificar o tipo dos parametros
					// Criar lista de variáveis com os valores dos coisas
					List<Variable> lv = new LinkedList<Variable>();
					for (int a=0; a<tipo.getAux().size();a++) {
						Variable vzin = evalue(scope,realParameters.get(a));
						Variable expected = createVarFromParameter(scope, formalParameters.get(a));
						if (vzin.getType()==expected.getType()) {
							expected.setValue(vzin.getValue());
							lv.add(expected);
						} else {
							Error err = new Error(25);
							err.setExtra(": " + vzin.getType() + ", expected "+expected.getType());
							throw err;
						}
					}
					r.setValue(lv);
				}
				return r;
			}
			
			
			// Para função pre-definida
			if (funcao.getType()==VarType.DEFAULTFUNCTION_TYPE) {
				// Passar o escopo, os parametros reais e o nome da função
				if (funCall.getLexems().get(2).getId()==SintaxElementId.PAR_REAIS) {
					return defaultFunctionCall(scope.clone(), getRealParameters(funCall), funcao.getName());
				} else if (funCall.getLexems().get(2).getId()==SintaxElementId.BRACKET_CLOSE) {
					return defaultFunctionCall(scope.clone(), getRealParameters(funCall), funcao.getName());
				}
				else {
					Error r = new Error(14);
					r.setExtra(": " + funcao.getName());
					throw r;
				}
			}
			
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
				//System.out.println("D=" + realVar);
				var.setValue(realVar.getValue());
				var.setAux(realVar.getAux());
				//System.out.println("Var: "+var);				
				newScope.insert(var);
			}
			
			// Quando o meliante sobre-escrever o nome da função
			if (funcao.getType()!=VarType.FUNCTION_TYPE) {
				Error r = new Error(24);
				r.setExtra(funCall.getFirstLexem().getLexem().getLex() + ". If you are trying to use a variable with this name in function's scope, rename it.");
				throw r;
			}
			SintaxElement funExp = (SintaxElement) funcao.getValue();
			//System.out.println(funcao);
			return evalue(newScope,funExp);
			
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
			case TYPE_FUNCTION: t = VarType.FUNCTION_TYPE; break;
		}
		v.setType(t);
		//System.out.println(v.getValue());
		//v.setValue(null);
		return v;
	}

	private static Variable defaultFunctionCall(Table scope, List<SintaxElement> parameters, String name) throws Error {
		if (name.compareToIgnoreCase("abs")==0) {
			if (parameters.size()!=1) { // Numero inválido de parametros
				Error r = new Error(16);
				r.setExtra(". Expected 1 got "+ parameters.size());
				throw r;
			}
			
			Variable v = evalue(scope,parameters.get(0));
			if (v.getType()!=VarType.FLOAT_TYPE && v.getType()!=VarType.INT_TYPE) {
				Error r = new Error(16);
				r.setExtra(" in fuction "+name + ". Got a " + v.getType());
				throw r;
			}
			String newv = "";
			if (v.getType()==VarType.FLOAT_TYPE) {
				float f = Math.abs(Float.parseFloat((String)v.getValue()));
				newv = String.valueOf(f);
			} else { // Integer
				int i = Math.abs(Integer.parseInt((String)v.getValue()));
				newv = String.valueOf(i);				
			}
			v.setValue(newv);
			return v;
		}
		
		else if (name.compareToIgnoreCase("ceil")==0) {
			if (parameters.size()!=1) { // Numero inválido de parametros
				Error r = new Error(16);
				r.setExtra(". Expected 1 got "+ parameters.size());
				throw r;
			}
			Variable v = evalue(scope,parameters.get(0));
			if (v.getType()!=VarType.FLOAT_TYPE) {
				Error r = new Error(16);
				r.setExtra(" in fuction "+name + ". Got a " + v.getType() + ", expected " + VarType.FLOAT_TYPE);
				throw r;
			} else {
				float f = (float) Math.ceil(Float.parseFloat((String)v.getValue()));
				v.setValue(String.valueOf(f));
			}
			return v;
		}
		
		else if (name.compareToIgnoreCase("floor")==0) {
			if (parameters.size()!=1) { // Numero inválido de parametros
				Error r = new Error(16);
				r.setExtra(". Expected 1 got "+ parameters.size());
				throw r;
			}
			Variable v = evalue(scope,parameters.get(0));
			if (v.getType()!=VarType.FLOAT_TYPE) {
				Error r = new Error(16);
				r.setExtra(" in fuction "+name + ". Got a " + v.getType() + ", expected " + VarType.FLOAT_TYPE);
				throw r;
			} else {
				float f = (float) Math.floor(Float.parseFloat((String)v.getValue()));
				v.setValue(String.valueOf(f));
			}
			return v;
		}
		else if (name.compareToIgnoreCase("sqrt")==0) {
			if (parameters.size()!=1) { // Numero inválido de parametros
				Error r = new Error(16);
				r.setExtra(". Expected 1 got "+ parameters.size());
				throw r;
			}
			Variable v = evalue(scope,parameters.get(0));
			if (v.getType()!=VarType.FLOAT_TYPE) {
				Error r = new Error(16);
				r.setExtra(" in fuction "+name + ". Got a " + v.getType() + ", expected " + VarType.FLOAT_TYPE);
				throw r;
			} else {
				float f = (float) Math.sqrt(Float.parseFloat((String)v.getValue()));
				v.setValue(String.valueOf(f));
			}
			return v;
		}
		//else if (name.compareToIgnoreCase("exp")==0) {}
		
		else if (name.compareToIgnoreCase("length")==0) {
			if (parameters.size()!=1) { // Numero inválido de parametros
				Error r = new Error(16);
				r.setExtra(". Expected 1 got "+ parameters.size());
				throw r;
			}
			Variable v = evalue(scope,parameters.get(0));
			if (v.getType()!=VarType.STRING_TYPE) {
				Error r = new Error(16);
				r.setExtra(" in fuction "+name + ". Got a " + v.getType() + ", expected " + VarType.STRING_TYPE);
				throw r;
			} else {
				int l = ((String) v.getValue()).length();
				v.setValue(String.valueOf(l-2)); // -2 pq tem que tirar as "
				v.setType(VarType.INT_TYPE);
			}
			return v;
		}
		
		else if (name.compareToIgnoreCase("set")==0) { 
			if (parameters.size()!=3) { // Numero inválido de parametros
				Error r = new Error(16);
				r.setExtra(". Expected 3 got "+ parameters.size());
				throw r;
			}
			Variable id = evalue(scope, parameters.get(0));
			Variable list = evalue(scope, parameters.get(1));
			Variable newValue = evalue(scope,parameters.get(2));
			
			// Fazer um get pra descobrir o tipo atual do elemento
			if (id.getType()!=VarType.INT_TYPE) {
				Error r = new Error(16);
				r.setExtra(" in fuction "+name + ". Got a " + list.getType() + ", expected " + VarType.INT_TYPE);
				throw r;
			} else if (list.getType()!=VarType.LIST_TYPE) {
				Error r = new Error(16);
				r.setExtra(" in fuction "+name + ". Got a " + list.getType() + ", expected " + VarType.LIST_TYPE);
				throw r;
			} else {
				//System.out.println(id);
				// DONE
				int realId = Integer.parseInt(((String)id.getValue()));
				List realList = (List)list.getValue();
				Variable oldValue;
				try {
					Lexem realLexem;
					try {
						realLexem = (Lexem) realList.get(realId);
					} catch(ClassCastException e) {
						realLexem = new Lexem((String) realList.get(realId));
					}
					realLexem.evalue();
					oldValue = evalue(scope,new SintaxElement(realLexem));
					if (newValue.getType()==oldValue.getType()) {
						Lexem newLex = new Lexem((String)newValue.getValue());
						((List)list.getValue()).set(realId, newLex);
						return list;
					} else {
						Error r = new Error(16);
						r.setExtra(" in fuction "+name + ". Got a " + newValue.getType() + ", expected " + oldValue.getType());
						throw r;
					}
				} catch (IndexOutOfBoundsException e) {
					Error r = new Error(25);
					r.setExtra(". "+e.getMessage());
					throw r;
				}
			}
		}
		else if (name.compareToIgnoreCase("get")==0) {
			// Dois parametros, o primeiro será o elemento, o segundo a lista
			if (parameters.size()!=2) { // Numero inválido de parametros
				Error r = new Error(16);
				r.setExtra(". Expected 2 got "+ parameters.size());
				throw r;
			}
			
			Variable id = evalue(scope, parameters.get(0));
			Variable list = evalue(scope, parameters.get(1));
			if (id.getType()!=VarType.INT_TYPE) {
				Error r = new Error(16);
				r.setExtra(" in fuction "+name + ". Got a " + list.getType() + ", expected " + VarType.INT_TYPE);
				throw r;
			} else if (list.getType()!=VarType.LIST_TYPE) {
				Error r = new Error(16);
				r.setExtra(" in fuction "+name + ". Got a " + list.getType() + ", expected " + VarType.LIST_TYPE);
				throw r;
			} else {
				//System.out.println(id);
				// DONE
				int realId = Integer.parseInt(((String)id.getValue()));
				List realList = (List)list.getValue();
				try {
					Lexem realLexem = (Lexem)realList.get(realId);
					realLexem.evalue();
					return evalue(scope,new SintaxElement(realLexem));
				} catch (IndexOutOfBoundsException e) {
					Error r = new Error(25);
					r.setExtra(". "+e.getMessage());
					throw r;
				}
			}
		} 
		
		else if (name.compareToIgnoreCase("uppercase")==0) {
			if (parameters.size()!=1) { // Numero inválido de parametros
				Error r = new Error(16);
				r.setExtra(". Expected 1 got "+ parameters.size());
				throw r;
			}
			Variable v = evalue(scope,parameters.get(0));
			if (v.getType()!=VarType.STRING_TYPE) {
				Error r = new Error(16);
				r.setExtra(" in fuction "+name + ". Got a " + v.getType() + ", expected " + VarType.STRING_TYPE);
				throw r;
			} else {
				String l = ((String) v.getValue()).toUpperCase();
				v.setValue(l);
			}
			return v;
		}
		else if (name.compareToIgnoreCase("lowercase")==0) {
			if (parameters.size()!=1) { // Numero inválido de parametros
				Error r = new Error(16);
				r.setExtra(". Expected 1 got "+ parameters.size());
				throw r;
			}
			Variable v = evalue(scope,parameters.get(0));
			if (v.getType()!=VarType.STRING_TYPE) {
				Error r = new Error(16);
				r.setExtra(" in fuction "+name + ". Got a " + v.getType() + ", expected " + VarType.STRING_TYPE);
				throw r;
			} else {
				String l = ((String) v.getValue()).toLowerCase();
				v.setValue(l); 
			}
			return v;
		}
		
		else if (name.compareToIgnoreCase("int_of_char")==0) {
			if (parameters.size()!=1) { // Numero inválido de parametros
				Error r = new Error(16);
				r.setExtra(". Expected 1 got "+ parameters.size());
				throw r;
			}
			Variable v = evalue(scope,parameters.get(0));
			if (v.getType()!=VarType.CHAR_TYPE) {
				Error r = new Error(16);
				r.setExtra(" in fuction "+name + ". Got a " + v.getType() + ", expected " + VarType.CHAR_TYPE);
				throw r;
			} else {
				String s = ((String) v.getValue());
				s = s.substring(1,s.length()-1);
				int l = s.charAt(0);
				v.setValue(String.valueOf(l));
				v.setType(VarType.INT_TYPE);
			}
			return v;	
		}
		else if (name.compareToIgnoreCase("int_of_string")==0) {
			if (parameters.size()!=1) { // Numero inválido de parametros
				Error r = new Error(16);
				r.setExtra(". Expected 1 got "+ parameters.size());
				throw r;
			}
			Variable v = evalue(scope,parameters.get(0));
			if (v.getType()!=VarType.STRING_TYPE) {
				Error r = new Error(16);
				r.setExtra(" in fuction "+name + ". Got a " + v.getType() + ", expected " + VarType.STRING_TYPE);
				throw r;
			} else {
				String s = ((String) v.getValue());
				s = s.substring(1,s.length()-1);
				int l = Integer.parseInt(s);
				v.setValue(String.valueOf(l));
				v.setType(VarType.INT_TYPE);
			}
			return v;
		}
		else if (name.compareToIgnoreCase("int_of_float")==0) {
			if (parameters.size()!=1) { // Numero inválido de parametros
				Error r = new Error(16);
				r.setExtra(". Expected 1 got "+ parameters.size());
				throw r;
			}
			Variable v = evalue(scope,parameters.get(0));
			if (v.getType()!=VarType.FLOAT_TYPE) {
				Error r = new Error(16);
				r.setExtra(" in fuction "+name + ". Got a " + v.getType() + ", expected " + VarType.FLOAT_TYPE);
				throw r;
			} else {
				int l = (int) Float.parseFloat((String) v.getValue());
				v.setValue(String.valueOf(l));
				v.setType(VarType.INT_TYPE);
			}
			return v;
		}
		
		else if (name.compareToIgnoreCase("char_of_int")==0) {
			if (parameters.size()!=1) { // Numero inválido de parametros
				Error r = new Error(16);
				r.setExtra(". Expected 1 got "+ parameters.size());
				throw r;
			}
			Variable v = evalue(scope,parameters.get(0));
			if (v.getType()!=VarType.INT_TYPE) {
				Error r = new Error(16);
				r.setExtra(" in fuction "+name + ". Got a " + v.getType() + ", expected " + VarType.INT_TYPE);
				throw r;
			} else {
				String l = "'"+Integer.parseInt((String) v.getValue()) +"'";
				v.setValue(String.valueOf(l));
				v.setType(VarType.CHAR_TYPE);
			}
			return v;
		}
		
		else if (name.compareToIgnoreCase("float_of_string")==0) {
			if (parameters.size()!=1) { // Numero inválido de parametros
				Error r = new Error(16);
				r.setExtra(". Expected 1 got "+ parameters.size());
				throw r;
			}
			Variable v = evalue(scope,parameters.get(0));
			if (v.getType()!=VarType.STRING_TYPE) {
				Error r = new Error(16);
				r.setExtra(" in fuction "+name + ". Got a " + v.getType() + ", expected " + VarType.STRING_TYPE);
				throw r;
			} else {
				String s = ((String) v.getValue());
				s = s.substring(1,s.length()-1);
				float l = Float.parseFloat(s);
				v.setValue(String.valueOf(l));
				v.setType(VarType.FLOAT_TYPE);
			}
			return v;
		}
		else if (name.compareToIgnoreCase("float_of_int")==0) {
			if (parameters.size()!=1) { // Numero inválido de parametros
				Error r = new Error(16);
				r.setExtra(". Expected 1 got "+ parameters.size());
				throw r;
			}
			Variable v = evalue(scope,parameters.get(0));
			if (v.getType()!=VarType.INT_TYPE) {
				Error r = new Error(16);
				r.setExtra(" in fuction "+name + ". Got a " + v.getType() + ", expected " + VarType.INT_TYPE);
				throw r;
			} else {
				float l = (float) Integer.parseInt((String) v.getValue());
				v.setValue(String.valueOf(l));
				v.setType(VarType.FLOAT_TYPE);
			}
			return v;
		}
		
		else if (name.compareToIgnoreCase("string_of_bool")==0) {
			if (parameters.size()!=1) { // Numero inválido de parametros
				Error r = new Error(16);
				r.setExtra(". Expected 1 got "+ parameters.size());
				throw r;
			}
			Variable v = evalue(scope,parameters.get(0));
			if (v.getType()!=VarType.BOOL_TYPE) {
				Error r = new Error(16);
				r.setExtra(" in fuction "+name + ". Got a " + v.getType() + ", expected " + VarType.BOOL_TYPE);
				throw r;
			} else {
				String s =((String) v.getValue());
				s= "\""+ s + "\"";
				v.setValue(String.valueOf(s));
				v.setType(VarType.STRING_TYPE);
			}
			return v;
		}
		else if (name.compareToIgnoreCase("string_of_char")==0) {
			if (parameters.size()!=1) { // Numero inválido de parametros
				Error r = new Error(16);
				r.setExtra(". Expected 1 got "+ parameters.size());
				throw r;
			}
			Variable v = evalue(scope,parameters.get(0));
			if (v.getType()!=VarType.CHAR_TYPE) {
				Error r = new Error(16);
				r.setExtra(" in fuction "+name + ". Got a " + v.getType() + ", expected " + VarType.CHAR_TYPE);
				throw r;
			} else {
				String s = ((String) v.getValue());
				s = s.substring(1,s.length()-1);
				s= "\""+ s + "\"";
				v.setValue(String.valueOf(s));
				v.setType(VarType.STRING_TYPE);
			}
			return v;
		}
		else if (name.compareToIgnoreCase("string_of_int")==0) {
			if (parameters.size()!=1) { // Numero inválido de parametros
				Error r = new Error(16);
				r.setExtra(". Expected 1 got "+ parameters.size());
				throw r;
			}
			Variable v = evalue(scope,parameters.get(0));
			if (v.getType()!=VarType.INT_TYPE) {
				Error r = new Error(16);
				r.setExtra(" in fuction "+name + ". Got a " + v.getType() + ", expected " + VarType.INT_TYPE);
				throw r;
			} else {
				String s = "\""+((String) v.getValue())+ "\"";
				v.setValue(String.valueOf(s));
				v.setType(VarType.STRING_TYPE);
			}
			return v;
		}
		else if (name.compareToIgnoreCase("string_of_float")==0) {
			if (parameters.size()!=1) { // Numero inválido de parametros
				Error r = new Error(16);
				r.setExtra(". Expected 1 got "+ parameters.size());
				throw r;
			}
			Variable v = evalue(scope,parameters.get(0));
			if (v.getType()!=VarType.FLOAT_TYPE) {
				Error r = new Error(16);
				r.setExtra(" in fuction "+name + ". Got a " + v.getType() + ", expected " + VarType.FLOAT_TYPE);
				throw r;
			} else {
				String s = "\""+((String) v.getValue())+ "\"";
				v.setValue(String.valueOf(s));
				v.setType(VarType.STRING_TYPE);
			}
			return v;
		}
		else if (name.compareToIgnoreCase("string_of_list")==0) { // TODO
			if (parameters.size()!=1) { // Numero inválido de parametros
				Error r = new Error(16);
				r.setExtra(". Expected 1 got "+ parameters.size());
				throw r;
			}
			Variable v = evalue(scope,parameters.get(0));
			if (v.getType()!=VarType.LIST_TYPE) {
				Error r = new Error(16);
				r.setExtra(" in fuction "+name + ". Got a " + v.getType() + ", expected " + VarType.LIST_TYPE);
				throw r;
			} else {
				String s = "\""+((String) v.getValue())+ "\"";
				v.setValue(String.valueOf(s));
				v.setType(VarType.STRING_TYPE);
			}
			return v;
		}

		else if (name.compareToIgnoreCase("read_int")==0) {
			if (parameters.size()!=0) { // Numero inválido de parametros
				Error r = new Error(16);
				r.setExtra(". Expected 0 got "+ parameters.size());
				throw r;
			} else {
				Variable v = new Variable("");
				v.setType(VarType.INT_TYPE);
				try {
					v.setValue(String.valueOf(Integer.parseInt(JOptionPane.showInputDialog("READ_INT"))));
					return v;
				}catch (NumberFormatException e) {
					Error r = new Error(100);
					r.setExtra(" value, expected INT_TYPE");
					throw r;
				}
			}
		}
		else if (name.compareToIgnoreCase("read_float")==0) {
			if (parameters.size()!=0) { // Numero inválido de parametros
				Error r = new Error(16);
				r.setExtra(". Expected 0 got "+ parameters.size());
				throw r;
			} else {
				Variable v = new Variable("");
				v.setType(VarType.FLOAT_TYPE);
				try {
					v.setValue(String.valueOf(Float.parseFloat(JOptionPane.showInputDialog("READ_FLOAT"))));
					return v;
				}catch (NumberFormatException e) {
					Error r = new Error(100);
					r.setExtra(" value, expected FLOAT_TYPE");
					throw r;
				}
			}
		}
		else if (name.compareToIgnoreCase("read_string")==0) {
			if (parameters.size()!=0) { // Numero inválido de parametros
				Error r = new Error(16);
				r.setExtra(". Expected 0 got "+ parameters.size());
				throw r;
			} else {
				Variable v = new Variable("");
				v.setType(VarType.STRING_TYPE);
				v.setValue(JOptionPane.showInputDialog("READ_FLOAT"));
				return v;
			}
		}
		else if (name.compareToIgnoreCase("read_char")==0) {
			if (parameters.size()!=0) { // Numero inválido de parametros
				Error r = new Error(16);
				r.setExtra(". Expected 0 got "+ parameters.size());
				throw r;
			} else {
				Variable v = new Variable("");
				v.setType(VarType.CHAR_TYPE);
				Lexem l = null;
				try {
					l = new Lexem("'" + JOptionPane.showInputDialog("READ_FLOAT") + "'");
					l.evalue();
				} catch (Error e) {
					Error r = new Error(100);
					r.setExtra(" value, expected CHAR_TYPE");
					throw r;
				}
				if (l.getId()==LexemId.CHAR_VALUE) {
					v.setValue("'" + JOptionPane.showInputDialog("READ_FLOAT") + "'");
					return v;
				}
				else {
					Error r = new Error(100);
					r.setExtra(" value, expected CHAR_TYPE");
					throw r;
				}
			}
		}
		
		else if (name.compareToIgnoreCase("print")==0) {
			if (parameters.size()!=1) { // Numero inválido de parametros
				Error r = new Error(16);
				r.setExtra(". Expected 1 got "+ parameters.size());
				throw r;
			}
			Variable v = evalue(scope,parameters.get(0));
			JOptionPane.showMessageDialog(null, (String)v.getValue(), "Print", JOptionPane.INFORMATION_MESSAGE);
			return v;
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
/*		s = s.substring(1, s.length()-1);
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
		return list;*/
		
		// Refazendo
		s = s.substring(1,s.length()-1); // Removo o primeiro e o ultimo elemento, que são [ e ]
		List list = new LinkedList<Lexem>();
		int bracketCounter = 0;
		String currentString="";
		for (int a=0; a<s.length();a++) {
			if (s.charAt(a)=='[') bracketCounter++;
			else if (s.charAt(a)==']') bracketCounter--;
			
			if (s.charAt(a)==',' && bracketCounter==0) {
				Lexem l = new Lexem(currentString);
				l.evalue();
				list.add(l);
				currentString=new String();
			}
			else currentString=currentString+s.charAt(a);
		}
		Lexem l = new Lexem(currentString);
		l.evalue();
		list.add(l);
		currentString=new String();
		//System.out.println(list + " : " + list.size());
		// Agora verifico se todos os tipos na lista são iguais
		if (list.size()>0) {
			VarType tipo = Calculator.typeFromLex((Lexem) list.get(0));
			//System.out.println(((Lexem)list.get(1)).getLex());
			for (int a=0; a<list.size();a++) {
				if (Calculator.typeFromLex((Lexem)list.get(a))!=tipo) {
					Error r = new Error(12);
					r.setExtra(". Expected " + tipo + " found " + Calculator.typeFromLex((Lexem)list.get(a)) + ": "+ ((Lexem)list.get(a)).getLex() );
					throw r;
				}
			}
		} // TODO parei aqui
		return list;
		
	}
	
	private static List<SintaxElement> getFormalParameters(SintaxElement fun) {
		List<SintaxElement> l = new LinkedList<SintaxElement>();
		
		if (fun.getId()==SintaxElementId.DEF_FUNCAO) {
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
		}
		else { // Definição de tipo
			SintaxElement par = fun.getLexems().get(4);
			if (par.getId()==SintaxElementId.TYPE_CAMPOS) {
				l = par.getLexems();
				for (int a=0;a<l.size();a++) {
					if (l.get(a).getId()==SintaxElementId.COMMA) {
						l.remove(a);
						a--;
					}
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
			//if (par.getFirstLexem().getId()==)
			l = par.getLexems();
			// Limpar a lista, remover as virgulas
			for (int a=0;a<l.size();a++) {
				if (l.get(a).getId()==SintaxElementId.COMMA) {
					l.remove(a);
				}
			}
			//System.out.println(l);
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
