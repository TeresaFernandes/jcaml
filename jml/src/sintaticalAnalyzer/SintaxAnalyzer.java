package sintaticalAnalyzer;

import java.util.List;
import java.util.Stack;

import javax.sql.rowset.spi.SyncFactoryException;

import CommonClasses.Lexem;
import CommonClasses.LexemId;
import CommonClasses.SintaxElement;
import CommonClasses.SintaxElementId;

public class SintaxAnalyzer {

	private static Stack<SintaxElement> stack; // Usem para o parsing
	
	// Retorna um unico elemento, que deve ser do tipo PROGRAMA, exceto se o parse não der certo
	/**
	 * @return (SintaxElement e)
	 * e.getId() == SintaxElementId.PROGRAMA 
	 */
	public static SintaxElement parseLexems(List<Lexem> list) throws Error {
		
		stack=new Stack<SintaxElement>();
		
		while (list.isEmpty()){
			
			
			
		}
		
		return null;
	}
	
	private static boolean reconhece_programa(List<Lexem> list, Stack<SintaxElement> stack){
		if(stack.isEmpty()|| (stack.size()==1 && stack.lastElement().getId()==SintaxElementId.DEFINICAO))
			return true;
		
		return false;
	}
	
	private static boolean reconhece_definicao(List<Lexem> list, Stack<SintaxElement> stack){
		if (   stack.lastElement().getId()==SintaxElementId.CHAMADA_FUNCAO 
			|| stack.lastElement().getId()==SintaxElementId.DEFINICAO_LOCAL
			|| stack.lastElement().getId()==SintaxElementId.DEFINICAO_GLOBAL
			|| stack.lastElement().getId()==SintaxElementId.EXP) 	return true;
		
		return false;
	}
	private static boolean reconhece_definicao_local(List<Lexem> list, Stack<SintaxElement> stack){
		
		if (  stack.remove(stack.size()-1).getId()==SintaxElementId.E
			&& stack.remove(stack.size()-1).getId()==SintaxElementId.KEYWORD_IN
			&& stack.remove(stack.size()-1).getId()==SintaxElementId.E
			&& stack.remove(stack.size()-1).getId()==SintaxElementId.ASSIGNMENT
			&& stack.remove(stack.size()-1).getId()==SintaxElementId.ID
			&& stack.remove(stack.size()-1).getId()==SintaxElementId.KEYWORD_LET)
			return true;
		
		return false;
	}
	private static boolean reconhece_global(List<Lexem> list, Stack<SintaxElement> stack){
		if (  	   stack.remove(stack.size()-1).getId()==SintaxElementId.E
				&& stack.remove(stack.size()-1).getId()==SintaxElementId.ASSIGNMENT
				&& stack.remove(stack.size()-1).getId()==SintaxElementId.ID
				&& stack.remove(stack.size()-1).getId()==SintaxElementId.KEYWORD_LET)
				return true;
			
		
		return false;
	}
	private static boolean reconhece_e(List<Lexem> list, Stack<SintaxElement> stack){
		return false;
	}
	private static boolean reconhece_def_funcao(List<Lexem> list, Stack<SintaxElement> stack){
		return false;
	}
	private static boolean reconhece_par_normais(List<Lexem> list, Stack<SintaxElement> stack){
		return false;
	}
	private static boolean reconhece_par(List<Lexem> list, Stack<SintaxElement> stack){
		return false;
	}
	private static boolean reconhece_exp(List<Lexem> list, Stack<SintaxElement> stack){
		return false;
	}
	private static boolean reconhece_exp_simples(List<Lexem> list, Stack<SintaxElement> stack){
		return false;
	}
	private static boolean reconhece_chamada_funcao(List<Lexem> list, Stack<SintaxElement> stack){
		return false;
	}
	private static boolean reconhece_parametros_reais(List<Lexem> list, Stack<SintaxElement> stack){
		return false;
	}
	private static boolean reconhece_if(List<Lexem> list, Stack<SintaxElement> stack){
		return false;
	}
	private static boolean reconhece_match(List<Lexem> list, Stack<SintaxElement> stack){
		return false;
	}
	private static boolean reconhece_match_var(List<Lexem> list, Stack<SintaxElement> stack){
		return false;
	}
	private static boolean reconhece_id(List<Lexem> list, Stack<SintaxElement> stack){
		return false;
	}
	private static boolean reconhece_tipo(List<Lexem> list, Stack<SintaxElement> stack){
		return false;
	}
	private static boolean reconhece_const(List<Lexem> list, Stack<SintaxElement> stack){
		return false;
	}
	private static boolean reconhece_op(List<Lexem> list, Stack<SintaxElement> stack){
		return false;
	}
	
	
}
