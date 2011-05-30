package CommonClasses;

import java.util.List;

public class SintaxElement {
	
	private List<SintaxElement> elements;
	private SintaxElementId id;
	private Lexem lexem; // Caso o elemento seja um nó final
	
	public SintaxElement(Lexem l) {
		lexem = l;
		switch (l.getId()) {
		case NAME: this.id = SintaxElementId.ID; break;
		
		case TYPE_INT:
		case TYPE_CHAR:
		case TYPE_STRING:
		case TYPE_FLOAT:
		case TYPE_BOOL:
		case TYPE_LIST: this.id = SintaxElementId.TIPO; break;
		
		case ACESS_DOT: this.id = SintaxElementId.DOT; break;
			
		case KEYWORD_ARROW: this.id = SintaxElementId.KEYWORD_ARROW; break;
		case KEYWORD_LET: this.id = SintaxElementId.KEYWORD_LET; break;
		case KEYWORD_IN: this.id = SintaxElementId.KEYWORD_IN; break;
		case KEYWORD_IF: this.id = SintaxElementId.KEYWORD_IF; break;
		case KEYWORD_ELSE: this.id = SintaxElementId.KEYWORD_ELSE; break;
		case KEYWORD_THEN: this.id = SintaxElementId.KEYWORD_THEN; break;
		case KEYWORD_FUN: this.id = SintaxElementId.KEYWORD_FUN; break;
		case KEYWORD_MATCH: this.id = SintaxElementId.KEYWORD_MATCH; break;
		case KEYWORD_WITH: this.id = SintaxElementId.KEYWORD_WITH; break;
		case KEYWORD_MATCHBAR: this.id = SintaxElementId.KEYWORD_MATCHBAR; break;
		case KEYWORD_JOKER: this.id = SintaxElementId.KEYWORD_JOKER; break;
		case KEYWORD_REC: this.id = SintaxElementId.KEYWORD_REC; break;
		case KEYWORD_AND: this.id = SintaxElementId.KEYWORD_AND; break;
		case KEYWORD_AS: this.id = SintaxElementId.KEYWORD_AS; break;
			
		case INT_VALUE:
		case FLOAT_VALUE:
		case CHAR_VALUE:
		case STRING_VALUE:
		case BOOL_VALUE: this.id = SintaxElementId.CONST; break;
		
		case LIST_START: this.id = SintaxElementId.LIST_START; break;
		case LIST_END: this.id = SintaxElementId.LIST_END; break;
			
		case OPERATOR_LISTCONCAT:
		case OPERATOR_LISTAPPEND:
		case OPERATOR_UNARYMINUS:
		case OPERATOR_SUM:
		case OPERATOR_SUB:
		case OPERATOR_MUL:
		case OPERATOR_MOD:
		case OPERATOR_POW:
		case OPERATOR_DIV:
		case OPERATOR_NOT:
		case OPERATOR_EQUAL:
		case OPERATOR_NONEQUAL:
		case OPERATOR_AND:
		case OPERATOR_OR:
		case OPERATOR_LESSTHAN:
		case OPERATOR_GREATERTHAN:
		case OPERATOR_LESSEQUALTHAN:
		case OPERATOR_GREATEREQUALTHAN:
		case OPERATOR_STRCONCAT: this.id = SintaxElementId.OP; break;
			
		case ASSIGNMENT: this.id = SintaxElementId.ASSIGNMENT; break;
		case COLON: this.id = SintaxElementId.COLON; break;
		case SEMICOLON: this.id = SintaxElementId.SEMICOLON; break;
		case COMMA: this.id = SintaxElementId.COMMA; break;
		case BRACKET_OPEN: this.id = SintaxElementId.BRACKET_OPEN; break;
		case BRACKET_CLOSE: this.id = SintaxElementId.BRACKET_CLOSE; break;
		case END_LINE: this.id = SintaxElementId.END_LINE; break;
		}
	}
	
	public SintaxElement(SintaxElementId id, List<SintaxElement> l) {
		this.elements = l;
		this.id = id;
	}

	public SintaxElement getFirstLexem() {
		return elements.get(0);
	}
	
	public List<SintaxElement> getLexems() {
		return elements;
	}
	
	public SintaxElementId getId() {
		return this.id;
	}
	
	public boolean isFinal() {
		return lexem!=null;
	}
	
	public Lexem getLexem() {
		return this.lexem;
	}
}
