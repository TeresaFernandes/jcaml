package CommonClasses;

import lexicalAnalyzer.Analyzer;


public class Lexem {
	private String lex;
	private LexemId id;
	private int line;
	
	public Lexem(String l) {
		this.lex = l;
	}

	private boolean validName() {
		if (Character.isDigit(lex.charAt(0))) return false; 
		if (lex.charAt(0)=='_' && (lex.length()==1)) return false;
		for (int a=0;a<lex.length();a++)
			if (!Character.isDigit(lex.charAt(a)) 
					&& !Character.isLetter(lex.charAt(a))
					&& lex.charAt(a)!='_') return false;
		return true;
	}
	
	public void evalue() throws Error {
		this.lex = Analyzer.removeBlankSpaces(this.lex);
		// Palavras reservadas
		if (lex.compareToIgnoreCase("let")==0) { id = LexemId.KEYWORD_LET; return; }
		else if (lex.compareToIgnoreCase("in")==0) { id = LexemId.KEYWORD_IN; return; }
		else if (lex.compareToIgnoreCase("if")==0) { id = LexemId.KEYWORD_IF; return; }
		else if (lex.compareToIgnoreCase("then")==0) { id = LexemId.KEYWORD_THEN; return; }
		else if (lex.compareToIgnoreCase("else")==0) { id = LexemId.KEYWORD_ELSE; return; }
		else if (lex.compareToIgnoreCase("fun")==0) { id = LexemId.KEYWORD_FUN; return; }
		else if (lex.compareToIgnoreCase("function")==0) { id = LexemId.KEYWORD_FUN; return; }
		else if (lex.compareToIgnoreCase("match")==0) { id = LexemId.KEYWORD_MATCH; return; }
		else if (lex.compareToIgnoreCase("with")==0) { id = LexemId.KEYWORD_WITH; return; }
		else if (lex.compareToIgnoreCase("as")==0) { id = LexemId.KEYWORD_AS; return; }
		else if (lex.compareToIgnoreCase("rec")==0) { id = LexemId.KEYWORD_REC; return; }
		else if (lex.compareToIgnoreCase("and")==0) { id = LexemId.KEYWORD_AND; return; }
		// Tipos
		else if (lex.compareToIgnoreCase("int")==0) { id = LexemId.TYPE_INT; return; }
		else if (lex.compareToIgnoreCase("float")==0) { id = LexemId.TYPE_FLOAT; return; }
		else if (lex.compareToIgnoreCase("string")==0) { id = LexemId.TYPE_STRING; return; }
		else if (lex.compareToIgnoreCase("char")==0) { id = LexemId.TYPE_CHAR; return; }
		else if (lex.compareToIgnoreCase("bool")==0) { id = LexemId.TYPE_BOOL; return; }
		else if (lex.compareToIgnoreCase("list")==0) { id = LexemId.TYPE_LIST; return; }
		// Marcação
		else if (lex.compareTo("|")==0) { id = LexemId.KEYWORD_MATCHBAR; return; }
		else if (lex.compareTo("_")==0) { id = LexemId.KEYWORD_JOKER; return; }
		else if (lex.compareTo(":")==0) { id = LexemId.COLON; return; }
		else if (lex.compareTo(";")==0) { id = LexemId.SEMICOLON; return; }
		else if (lex.compareTo(",")==0) { id = LexemId.COMMA; return; }
		else if (lex.compareTo("(")==0) { id = LexemId.BRACKET_OPEN; return; }
		else if (lex.compareTo(")")==0) { id = LexemId.BRACKET_CLOSE; return; }
		else if (lex.compareTo("=")==0) { id = LexemId.ASSIGNMENT; return; }
		else if (lex.compareTo("->")==0) { id = LexemId.KEYWORD_ARROW; return; }
		else if (lex.compareTo(".")==0) { id = LexemId.ACESS_DOT; return; }		
		// Operadores
		else if (lex.compareTo("::")==0) { id = LexemId.OPERATOR_LISTAPPEND; return; }
		else if (lex.compareTo("~-.")==0) { id = LexemId.OPERATOR_UNARYMINUS; return; }
		else if (lex.compareTo("~-")==0) { id = LexemId.OPERATOR_UNARYMINUS; return; }
		else if (lex.compareTo("@")==0) { id = LexemId.OPERATOR_LISTCONCAT; return; }
		else if (lex.compareTo("+")==0) { id = LexemId.OPERATOR_SUM; return; }
		else if (lex.compareTo("-")==0) { id = LexemId.OPERATOR_SUB; return; }
		else if (lex.compareTo("*")==0) { id = LexemId.OPERATOR_MUL; return; }
		else if (lex.compareTo("/")==0) { id = LexemId.OPERATOR_DIV; return; }
		else if (lex.compareTo("%")==0) { id = LexemId.OPERATOR_MOD; return; }
		else if (lex.compareTo("^")==0) { id = LexemId.OPERATOR_POW; return; }
		else if (lex.compareTo("<>")==0) { id = LexemId.OPERATOR_NONEQUAL; return; }
		else if (lex.compareTo("!=")==0) { id = LexemId.OPERATOR_NONEQUAL; return; }
		else if (lex.compareTo("!")==0) { id = LexemId.OPERATOR_NOT; return; }
		else if (lex.compareTo("+.")==0) { id = LexemId.OPERATOR_SUM; return; }
		else if (lex.compareTo("-.")==0) { id = LexemId.OPERATOR_SUB; return; }
		else if (lex.compareTo("*.")==0) { id = LexemId.OPERATOR_MUL; return; }
		else if (lex.compareTo("/.")==0) { id = LexemId.OPERATOR_DIV; return; }
		else if (lex.compareTo("==")==0) { id = LexemId.OPERATOR_EQUAL; return; }
		else if (lex.compareTo(">")==0) { id = LexemId.OPERATOR_GREATERTHAN; return; }
		else if (lex.compareTo("<")==0) { id = LexemId.OPERATOR_LESSTHAN; return; }
		else if (lex.compareTo(">=")==0) { id = LexemId.OPERATOR_GREATEREQUALTHAN; return; }
		else if (lex.compareTo("<=")==0) { id = LexemId.OPERATOR_LESSEQUALTHAN; return; }
		else if (lex.startsWith("[")) { id = LexemId.LIST; return; }
		else if (lex.startsWith("&&")) { id = LexemId.OPERATOR_AND; return; }
		else if (lex.startsWith("||")) { id = LexemId.OPERATOR_OR; return; }
		else if (lex.startsWith("&")) { id = LexemId.OPERATOR_STRCONCAT; return; }		
		// Valores
		else if (lex.startsWith("\"") && lex.endsWith("\"")) { id = LexemId.STRING_VALUE; return; }
		else if (lex.startsWith("'") && lex.endsWith("'")) { id = LexemId.CHAR_VALUE; return; }
		else if (lex.compareToIgnoreCase("true")==0) { id = LexemId.BOOL_VALUE; return; }
		else if (lex.compareToIgnoreCase("false")==0) { id = LexemId.BOOL_VALUE; return; }
		
		try {
			//Integer.parseInt(lex);
			Float.parseFloat(lex); // O parse int detecta parse float =D
		} catch (NumberFormatException e) {
			id = LexemId.NAME; // Se não for possível passar pra numéric, é um nome
			Error lerror = new Error(2);
			lerror.setExtra(": " +lex);
			if (!validName()) throw lerror;
			return;
		}
		if (lex.contains(".")) id=LexemId.FLOAT_VALUE;
		else id=LexemId.INT_VALUE;
		//id = LexemId.NUMERIC_VALUE;
	}

	public String getLex() {
		return lex;
	}

	public LexemId getId() {
		return id;
	}
	
	public int getLine() {
		return this.line;
	}
	
	public void setLine(int l) {
		this.line = l;
	}

	public boolean isUnaryOperator() {
		switch(id) {
			case OPERATOR_NOT:
			case OPERATOR_UNARYMINUS: return true;
			default: return false;
		}
	}
}
