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
				lex.push(functionCall(scope, current));
			}
			// TODO continuar a fazer aqui, isso é só para empilhar chamadas de função
			
		}
		
		return r;
	}
	
	// Deve retornar um valor
	public static Lexem functionCall(Table scope, SintaxElement fun) throws Error {
		List<SintaxElement> list = fun.getLexems();

		try {
			// Detectar a função
			Variable funcao = scope.getElement(list.get(0).getLexem().getLex());
			
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
				// TODO
			}
			
		} catch (Error e) {
			e.setLine(list.get(0).getLexem().getLine());
			throw e;
		}
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
