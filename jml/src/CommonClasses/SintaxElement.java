package CommonClasses;

import java.util.List;

public class SintaxElement {
	
	private List<Lexem> lexems;
	private SintaxElementId id;
	
	public SintaxElement(SintaxElementId id, List<Lexem> l) {
		this.lexems = l;
		this.id = id;
	}

	public Lexem getFirstLexem() {
		return lexems.get(0);
	}
	
	public List<Lexem> getLexems() {
		return lexems;
	}
	
	public SintaxElementId getId() {
		return this.id;
	}
}
