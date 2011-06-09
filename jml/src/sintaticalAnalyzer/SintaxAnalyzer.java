package sintaticalAnalyzer;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import CommonClasses.Lexem;
import CommonClasses.LexemId;
import CommonClasses.SintaxElement;
import CommonClasses.SintaxElementId;

public class SintaxAnalyzer {


	private static Stack<SintaxElement> stack;
	private static List<Lexem> list;
	
	public static SintaxElement parseLexems(List<Lexem> l) throws Error {
		
		if (list==null)list=l;
		if (stack==null)stack=new Stack<SintaxElement>();		
		
		while (!reconhece_programa()){//...	
			
			stack.push(new SintaxElement(list.remove(0)));
			
		}
		
		
		if (reconhece_programa()){
			parseLexems(list);
		}
		if (stack.size()==1 && list.size()==0){
			return stack.pop();
		}
		
		return null;		
				
	}

	private static boolean reconhece_programa(){
		List<SintaxElement> laux=new LinkedList<SintaxElement>();
		
		if (stack.size()>1){
			if (stack.peek().getId()== SintaxElementId.SEMICOLON){
				laux.add(0,stack.pop());
				laux.add(0,stack.pop());
				
				 stack.push(new SintaxElement(SintaxElementId.PROGRAMA, laux));
				return true;
			}
		}
		return false;
	}
	
	private static boolean reconhece_definicao(){
		List<SintaxElement> laux=new LinkedList<SintaxElement>();
		
		if(stack.size()>0){
			if (stack.peek().getId()==SintaxElementId.CHAMADA_FUNCAO
				||stack.peek().getId()==SintaxElementId.DEFINICAO_LOCAL
				||stack.peek().getId()==SintaxElementId.DEFINICAO_GLOBAL
				||stack.peek().getId()==SintaxElementId.EXP	){
				
				laux.add(0,stack.pop());
				 stack.push(new SintaxElement(SintaxElementId.DEFINICAO, laux));
				return true;
			}
		}
			
		return false;
	}
	
	private static boolean reconhece_def_global(){
		List<SintaxElement> laux=new LinkedList<SintaxElement>();
		
		if (stack.size()>3 && list.size()>0){
			if(stack.get(stack.size()-1).getId()==SintaxElementId.E
				&& stack.get(stack.size()-2).getId()==SintaxElementId.ASSIGNMENT
				&& stack.get(stack.size()-3).getId()==SintaxElementId.ID
				&& stack.get(stack.size()-4).getId()==SintaxElementId.KEYWORD_LET
				&& !(list.get(0).getId() == LexemId.KEYWORD_IN)){
				
				laux.add(0,stack.pop());
				laux.add(0,stack.pop());
				laux.add(0,stack.pop());
				laux.add(0,stack.pop());
				
				stack.push(new SintaxElement(SintaxElementId.DEFINICAO_GLOBAL, laux));
			}
		}
		return false;
	}
	
	private static boolean reconhece_def_local(){
		List<SintaxElement> laux=new LinkedList<SintaxElement>();
		
		if (stack.size()>5 &&(stack.get(stack.size()-1).getId()==SintaxElementId.E
								&& stack.get(stack.size()-2).getId()==SintaxElementId.KEYWORD_IN
								&& stack.get(stack.size()-3).getId()==SintaxElementId.E
								&& stack.get(stack.size()-4).getId()==SintaxElementId.ASSIGNMENT
								&& stack.get(stack.size()-5).getId()==SintaxElementId.ID
								&& stack.get(stack.size()-6).getId()==SintaxElementId.KEYWORD_LET)){
								
				laux.add(0,stack.pop());
				laux.add(0,stack.pop());
				laux.add(0,stack.pop());
				laux.add(0,stack.pop());
				laux.add(0,stack.pop());
				laux.add(0,stack.pop());
				 stack.push(new SintaxElement(SintaxElementId.DEFINICAO_LOCAL, laux));
				 return true;
		}
		return false;
	}
	
	private static boolean reconhece_e(){
		List<SintaxElement> laux=new LinkedList<SintaxElement>();
		if (stack.size()>0 && (stack.peek().getId() == SintaxElementId.DEF_FUNCAO
								|| stack.peek().getId() == SintaxElementId.EXP
								||stack.peek().getId() == SintaxElementId.DEFINICAO_LOCAL)){
								
				laux.add(0,stack.pop());
				 stack.push(new SintaxElement(SintaxElementId.E, laux));
				return true;
		}
		return false;
	}
	
	private static boolean reconhece_def_funcao(){
		List<SintaxElement> laux=new LinkedList<SintaxElement>();
		
		if (stack.size()>5 && stack.get(stack.size()-1).getId()==SintaxElementId.EXP
							&& stack.get(stack.size()-2).getId()==SintaxElementId.KEYWORD_ARROW
							&& stack.get(stack.size()-3).getId()==SintaxElementId.BRACKET_CLOSE
							&& stack.get(stack.size()-4).getId()==SintaxElementId.PAR_FORMAIS
							&& stack.get(stack.size()-5).getId()==SintaxElementId.BRACKET_OPEN
							&& stack.get(stack.size()-6).getId()==SintaxElementId.KEYWORD_FUN){
				
			laux.add(0,stack.pop());
			laux.add(0,stack.pop());
			laux.add(0,stack.pop());
			laux.add(0,stack.pop());
			laux.add(0,stack.pop());
			laux.add(0,stack.pop());
			stack.push(new SintaxElement(SintaxElementId.DEF_FUNCAO, laux));
			return true;
			
		}else if (stack.size()>4 && stack.get(stack.size()-1).getId()==SintaxElementId.EXP
								&& stack.get(stack.size()-2).getId()==SintaxElementId.KEYWORD_ARROW
								&& stack.get(stack.size()-3).getId()==SintaxElementId.BRACKET_CLOSE
								&& stack.get(stack.size()-4).getId()==SintaxElementId.BRACKET_OPEN
								&& stack.get(stack.size()-5).getId()==SintaxElementId.KEYWORD_FUN){
				
			laux.add(0,stack.pop());
			laux.add(0,stack.pop());
			laux.add(0,stack.pop());
			laux.add(0,stack.pop());
			laux.add(0,stack.pop());
			stack.push(new SintaxElement(SintaxElementId.DEF_FUNCAO, laux));
			return true;
				
		} 
		
		return false;
	}
	
	private static boolean reconhece_par_formais(){
		List<SintaxElement> laux=new LinkedList<SintaxElement>();
		
		if (stack.size()>0 && list.size()>0 && stack.get(stack.size()-1).getId()==SintaxElementId.PAR
											&& !(list.get(0).getId()==LexemId.COLON)){
			
			while(stack.peek().getId()==SintaxElementId.COLON || stack.peek().getId()==SintaxElementId.PAR){
				laux.add(0,stack.pop());
			}
			
			stack.push(new SintaxElement(SintaxElementId.PAR_FORMAIS, laux));
			return true;
		}
			
		return false;
	}
	
	private static boolean reconhece_par(){
		List<SintaxElement> laux=new LinkedList<SintaxElement>();
		if (stack.size()>2 && stack.get(stack.size()-1).getId()==SintaxElementId.TIPO
							&& stack.get(stack.size()-2).getId()==SintaxElementId.COLON
							&& stack.get(stack.size()-3).getId()==SintaxElementId.ID){
			
			laux.add(0,stack.pop());
			laux.add(0,stack.pop());
			laux.add(0,stack.pop());
			stack.push(new SintaxElement(SintaxElementId.PAR, laux));
			return true;

		}
		
		
		return false;
	}
}