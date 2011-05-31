package sintaticalAnalyzer;

import java.util.LinkedList;
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
		
		if (stack==null) stack=new Stack<SintaxElement>();
		
		while (!list.isEmpty()){
			
			if (!(reconhece_definicao_local(list)||reconhece_definicao_global(list))){
			
				stack.push(new SintaxElement(list.remove(0)));
			}
			System.out.println(stack.size()+" "+stack.lastElement().getId().toString());
		}
		
		return null;
	}
	
	private static boolean reconhece_programa(List<Lexem> list){
		List<SintaxElement> laux=new LinkedList<SintaxElement>();
		
		if (stack.size()>0){
			laux.add(stack.pop());
			
			if(laux.get(0).getId()==SintaxElementId.DEFINICAO){
				stack.push(new SintaxElement(SintaxElementId.PROGRAMA, laux));
				return true;
			}else{
				stack.add(laux.remove(0));
			}
		}
		
		return false;
	}
	
	private static boolean reconhece_definicao(List<Lexem> list){
		List<SintaxElement> laux=new LinkedList<SintaxElement>();
		
		if (stack.size()>3){
			
			laux.add(0,stack.pop());
			laux.add(0,stack.pop());
			laux.add(0,stack.pop());
			laux.add(0,stack.pop());
			
			if (   laux.get(3).getId()==SintaxElementId.CHAMADA_FUNCAO 
				&&  laux.get(2).getId()==SintaxElementId.DEFINICAO_LOCAL
				&&  laux.get(1).getId()==SintaxElementId.DEFINICAO_GLOBAL
				&&  laux.get(0).getId()==SintaxElementId.EXP){
				
				stack.push(new SintaxElement(SintaxElementId.DEFINICAO, laux));
				return true;
			}else{
				while(!laux.isEmpty()){
					stack.add(laux.remove(0));
				}
			}
		}
		
		return false;
	}
	private static boolean reconhece_definicao_local(List<Lexem> list){
		List<SintaxElement> laux=new LinkedList<SintaxElement>();
		
		if (stack.size()>3 && (list.get(0).getId()== LexemId.KEYWORD_IN)){
			
			laux.add(0,stack.pop());
			laux.add(0,stack.pop());
			laux.add(0,stack.pop());
			laux.add(0,stack.pop());
			
			if (  	   laux.get(3).getId()==SintaxElementId.E
					&& laux.get(2).getId()==SintaxElementId.ASSIGNMENT
					&& laux.get(1).getId()==SintaxElementId.ID
					&& laux.get(0).getId()==SintaxElementId.KEYWORD_LET){
				
				laux.add(new SintaxElement(list.remove(0)));
				stack.push(new SintaxElement(SintaxElementId.DEFINICAO_LOCAL, laux));
				return true;
			}else{
				while(!laux.isEmpty()){
					stack.add(laux.remove(0));
				}
			}
		}
		
		return false;
	}
	
	
	private static boolean reconhece_definicao_global(List<Lexem> list){
		List<SintaxElement> laux=new LinkedList<SintaxElement>();
		if (stack.size()>3){
			
			laux.add(0,stack.pop());
			laux.add(0,stack.pop());
			laux.add(0,stack.pop());
			laux.add(0,stack.pop());
			
			if (  	   laux.get(3).getId()==SintaxElementId.E
					&& laux.get(2).getId()==SintaxElementId.ASSIGNMENT
					&& laux.get(1).getId()==SintaxElementId.ID
					&& laux.get(0).getId()==SintaxElementId.KEYWORD_LET){
				
				stack.push(new SintaxElement(SintaxElementId.DEFINICAO_GLOBAL, laux));
				return true;
			}else{
				while(!laux.isEmpty()){
					stack.add(laux.remove(0));
				}
			}
		}
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
