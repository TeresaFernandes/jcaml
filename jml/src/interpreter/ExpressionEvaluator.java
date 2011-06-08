package interpreter;

import java.util.Collection;
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
		
		if (exp.getId()!= SintaxElementId.E) {
			throw new Error(10);
		}
		
		LinkedList<SintaxElement> l = (LinkedList<SintaxElement>) exp.getLexems();
		Stack<Lexem> lex = new Stack<Lexem>();
		
		SintaxElement current;
		while (!l.isEmpty()) {
			current = l.getFirst();
			if (current.getId()==SintaxElementId.CHAMADA_FUNCAO) {
				return functionCall(scope.clone(), current);
			}
			else if (current.getId()==SintaxElementId.DEFINICAO_GLOBAL) {
				// Nome da variável está na posição 1 da lista, ai basta pegar o lex dele
				String varName = current.getLexems().get(1).getLexem().getLex();
				Variable var = scope.getElement(varName); // Variável que vai ser alterada na tabela de simbolos
				if (current.getLexems().get(3).getId()==SintaxElementId.DEF_FUNCAO) {
					// Se for uma função arrumar a tabela de simbolos
					// TODO arrumar a tabela de simbolos
				}
				else {
					// Se não for função, é só atualizar o valor e o tipo
					// Após chamar recursivamente o evalue
					Variable retorno = evalue(scope,current.getLexems().get(3));
					var.setValue(retorno.getValue());
					var.setType(retorno.getType());
					return retorno;
				}
			}
		}
		
		return r;
	}
	
	// Deve retornar um valor
	public static Variable functionCall(Table scope, SintaxElement fun) throws Error {
		List<SintaxElement> list = fun.getLexems();

		try {
			// Detectar a função
			Variable funcao = scope.getElement(list.get(0).getLexem().getLex());
			
			// Para função pre-definida
			if (funcao.getType()==VarType.DEFAULTFUNCTION_TYPE) {
				// Passar o escopo, os parametros reais e o nome da função
				if (fun.getLexems().get(2).getId()==SintaxElementId.PAR_REAIS) {
					return defaultFunctionCall(scope, fun.getLexems().get(2), funcao.getName());
				} // TODO tratar quando a função não tem parametros reais
				else {
					Error r = new Error(14);
					r.setExtra(": " + funcao.getName());
				}
			}
			
			List<SintaxElement> parameters = funcao.getAux();
			
			// Criar os elementos parametro na table
			Variable v;
			for (int a=0; a<parameters.size(); a+=2) {
				// Nome da variável
				v = new Variable(parameters.get(a).getLexems().get(0).getLexem().getLex());
				// Tipo da variável
				v.setType(typeFromLex(parameters.get(a).getLexems().get(2).getLexem()));
				// Insere a variável no escopo da função
				scope.insert(v);
			}
			// Agora pego os parametros reais passados
			// Começam em 2 e vão até tamanho-2 (o ultimo elemento é um ')' )
			for (int parR=2, parF=0;parR<fun.getLexems().size()-2; parR++, parF++) {
				// Pegar o parametro real e colocar no espaço de endereçamendo do parametro formal
				// TODO dá pra unir esses dois for
			}
			
		} catch (Error e) {
			e.setLine(list.get(0).getLexem().getLine());
			throw e;
		}
		return null;
	}
	
	private static Variable defaultFunctionCall(Table scope, SintaxElement parameters, String name) {
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
}
