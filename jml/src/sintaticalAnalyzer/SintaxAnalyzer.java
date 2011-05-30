package sintaticalAnalyzer;

import java.util.List;
import java.util.Stack;

import CommonClasses.Lexem;
import CommonClasses.SintaxElement;

public class SintaxAnalyzer {

	private static Stack<SintaxElement> stack; // Usem para o parsing
	
	// Retorna um unico elemento, que deve ser do tipo PROGRAMA, exceto se o parse não der certo
	/**
	 * @return (SintaxElement e)
	 * e.getId() == SintaxElementId.PROGRAMA 
	 */
	public static SintaxElement parseLexems(List<Lexem> list) throws Error {
		return null;
	}
}
