package sintaticalAnalyzer;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import javax.sql.rowset.spi.SyncFactoryException;

import org.w3c.dom.ls.LSInput;

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
		
		 stack.push(new SintaxElement(list.remove(0)));
		 while (!list.isEmpty()){
			 if (	  reconhece_chamada_funcao(list) 
				   || reconhece_def_funcao(list)
				   || reconhece_definicao(list)
				   || reconhece_definicao_global(list)
				   || reconhece_definicao_local(list)
				   || reconhece_e(list)
				   || reconhece_exp(list)
				   || reconhece_exp_simples(list)
				   || reconhece_if(list)
				   || reconhece_match(list)
				   || reconhece_match_var(list)
				   || reconhece_par(list)
				   || reconhece_par_formais(list)
				   || reconhece_parametros_reais(list)
				   || reconhece_programa(list)){}
			
			 for ( int i=0;i<stack.size();i++)
					System.out.println(i+" "+stack.get(i).getId().toString());
					System.out.println("\n");
		
			 stack.push(new SintaxElement(list.remove(0)));
				
		}
		
		return null;
	}
	
	
	private static boolean reconhece_programa(List<Lexem> list){
		List<SintaxElement> laux=new LinkedList<SintaxElement>();
		
		//if (stack.size()==0) return true;
		if (stack.size()>0){
			laux.add(stack.pop());
			
			if(laux.get(0).getId()==SintaxElementId.DEFINICAO){
				stack.push(new SintaxElement(SintaxElementId.PROGRAMA, laux));
				return true;
			}else{
				stack.push(laux.remove(0));
			}
		}
		
		return false;
	}	
	private static boolean reconhece_definicao(List<Lexem> list){
		List<SintaxElement> laux=new LinkedList<SintaxElement>();
		
		if (stack.size()>0){
			
			laux.add(0,stack.pop());
			
			if (   laux.get(0).getId()==SintaxElementId.CHAMADA_FUNCAO 
				||  laux.get(0).getId()==SintaxElementId.DEFINICAO_LOCAL
				||  laux.get(0).getId()==SintaxElementId.DEFINICAO_GLOBAL
				||  laux.get(0).getId()==SintaxElementId.EXP){
				
				stack.push(new SintaxElement(SintaxElementId.DEFINICAO, laux));
				return true;
			}
		}
		
		while(!laux.isEmpty()){
			stack.push(laux.remove(0));
		}
		return false;
	}
	private static boolean reconhece_definicao_local(List<Lexem> list){
		List<SintaxElement> laux=new LinkedList<SintaxElement>();
		SintaxElement se = new SintaxElement(list.get(0));
		int i=0;
		if (stack.size()>0){
			laux.add(0,stack.pop());
			if (laux.get(0).getId()==SintaxElementId.KEYWORD_LET){
				
				while(list.get(i).getId()!){
				  laux.add(list.remove(0));
				}
				
			}
		}
		/*if (stack.size()>5){
			
			laux.add(0,stack.pop());
			laux.add(0,stack.pop());
			laux.add(0,stack.pop());
			laux.add(0,stack.pop());
			laux.add(0,stack.pop());
			laux.add(0,stack.pop());
			
			if (    laux.get(5).getId()==SintaxElementId.E
					&& laux.get(4).getId()==SintaxElementId.KEYWORD_IN
					&& laux.get(3).getId()==SintaxElementId.E
					&& laux.get(2).getId()==SintaxElementId.ASSIGNMENT
					&& laux.get(1).getId()==SintaxElementId.ID
					&& laux.get(0).getId()==SintaxElementId.KEYWORD_LET){
				
				stack.push(new SintaxElement(SintaxElementId.DEFINICAO_LOCAL, laux));
				return true;
			}
		}
		while(!laux.isEmpty()){
			stack.push(laux.remove(0));
		}*/	
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
			}
		}
		
		while(!laux.isEmpty()){
			stack.push(laux.remove(0));
		}
		return false;
	}
	private static boolean reconhece_e(List<Lexem> list){
		List<SintaxElement> laux=new LinkedList<SintaxElement>();
		
		if (stack.size()>0){
			
			laux.add(0,stack.pop());
			
			if ( laux.get(0).getId()==SintaxElementId.DEF_FUNCAO
				||  laux.get(0).getId()==SintaxElementId.EXP
				||  laux.get(0).getId()==SintaxElementId.DEFINICAO_LOCAL){
				
				stack.push(new SintaxElement(SintaxElementId.E, laux));
				return true;
			}
		}
		
		while(!laux.isEmpty()){
			stack.push(laux.remove(0));
		}
		return false;
	}
	private static boolean reconhece_def_funcao(List<Lexem> list){
		List<SintaxElement> laux=new LinkedList<SintaxElement>();
		if (stack.size()>5){
			
			laux.add(0,stack.pop());
			laux.add(0,stack.pop());
			laux.add(0,stack.pop());
			laux.add(0,stack.pop());
			laux.add(0,stack.pop());
			laux.add(0,stack.pop());
			
			if (   laux.get(5).getId()==SintaxElementId.EXP
				&&  laux.get(4).getId()==SintaxElementId.KEYWORD_ARROW
				&&  laux.get(3).getId()==SintaxElementId.BRACKET_CLOSE
				&&  laux.get(2).getId()==SintaxElementId.PAR_FORMAIS
				&&  laux.get(1).getId()==SintaxElementId.BRACKET_OPEN
				&&  laux.get(0).getId()==SintaxElementId.KEYWORD_FUN){
				
				stack.push(new SintaxElement(SintaxElementId.DEF_FUNCAO, laux));
				return true;
			}
		}	
		
		while(!laux.isEmpty()){
			stack.push(laux.remove(0));
		}
		return false;
	}
	private static boolean reconhece_par_formais(List<Lexem> list){
		
		List<SintaxElement> laux=new LinkedList<SintaxElement>();
		if (stack.size()>0){			
			
			laux.add(0,stack.pop());
			
			if ((laux.get(0).getId()==SintaxElementId.PAR)){
				
				stack.push(new SintaxElement(SintaxElementId.PAR_FORMAIS, laux));
				return true;		
			}
		}
		
		if ( stack.size()>2){
			
			laux.add(0,stack.pop());
			laux.add(0,stack.pop());
			
			if (laux.get(0).getId()==SintaxElementId.PAR
				&& laux.get(1).getId()==SintaxElementId.COMMA 
				&& laux.get(2).getId()==SintaxElementId.PAR_FORMAIS){
				
				stack.push(new SintaxElement(SintaxElementId.PAR_FORMAIS, laux));
				return true;
			} 
		}
			
		
		while(!laux.isEmpty()){
			stack.push(laux.remove(0));
		}
		return false;
	}
	private static boolean reconhece_par(List<Lexem> list){
		
		List<SintaxElement> laux=new LinkedList<SintaxElement>();
		if (stack.size()>2){
			
			laux.add(0,stack.pop());
			laux.add(0,stack.pop());
			laux.add(0,stack.pop());
			
			if (    laux.get(2).getId()==SintaxElementId.TIPO
				&&  laux.get(1).getId()==SintaxElementId.COLON
				&&  laux.get(0).getId()==SintaxElementId.ID){
				
				stack.push(new SintaxElement(SintaxElementId.PAR, laux));
				return true;
			}
		}
		
		while(!laux.isEmpty()){
			stack.push(laux.remove(0));
		}
		return false;
	}
	private static boolean reconhece_exp(List<Lexem> list){
		List<SintaxElement> laux=new LinkedList<SintaxElement>();
		if (stack.size()>0){
			
			laux.add(0,stack.pop());
			
			if (    laux.get(0).getId()==SintaxElementId.EXP_SIMPLES
				||  laux.get(0).getId()==SintaxElementId.MATCH
				||  laux.get(0).getId()==SintaxElementId.IF){
				
				stack.push(new SintaxElement(SintaxElementId.EXP, laux));
				return true;
		
			}
		}
		while(!laux.isEmpty()){
			stack.push(laux.remove(0));
		}
		return false;
	}
	private static boolean reconhece_exp_simples(List<Lexem> list){
		List<SintaxElement> laux=new LinkedList<SintaxElement>();
		if (stack.size()>0){
			
			laux.add(0,stack.pop());
			
			if (    laux.get(0).getId()==SintaxElementId.ID
				||  laux.get(0).getId()==SintaxElementId.CHAMADA_FUNCAO){
				
				stack.push(new SintaxElement(SintaxElementId.EXP_SIMPLES, laux));
				return true;		
			}
		}
		if (stack.size()>2){
			
			laux.add(0,stack.pop());
			laux.add(0,stack.pop());
			
			if ((laux.get(0).getId()==SintaxElementId.ID
				||  laux.get(0).getId()==SintaxElementId.CHAMADA_FUNCAO)
				&& laux.get(1).getId()==SintaxElementId.OP 
				&& laux.get(2).getId()==SintaxElementId.EXP_SIMPLES){
				
				stack.push(new SintaxElement(SintaxElementId.EXP_SIMPLES, laux));
				return true;
			}
		}	
		
		
		while(!laux.isEmpty()){
			stack.push(laux.remove(0));
		}
		return false;
	}
	private static boolean reconhece_chamada_funcao(List<Lexem> list){
		List<SintaxElement> laux=new LinkedList<SintaxElement>();
		if (stack.size()>2){
			
			laux.add(0,stack.pop());
			laux.add(0,stack.pop());
			laux.add(0,stack.pop());
			
			if (    laux.get(0).getId()==SintaxElementId.ID
				&&  laux.get(1).getId()==SintaxElementId.BRACKET_OPEN
				&&  laux.get(2).getId()==SintaxElementId.BRACKET_CLOSE){
				
				stack.push(new SintaxElement(SintaxElementId.CHAMADA_FUNCAO, laux));
				return true;
		
			}
		}
		if (stack.size()>3){
				
			laux.add(0,stack.pop());
				
			if (    laux.get(0).getId()==SintaxElementId.ID
					&&  laux.get(1).getId()==SintaxElementId.BRACKET_OPEN
					&&  laux.get(2).getId()==SintaxElementId.PAR_REAIS
					&&  laux.get(3).getId()==SintaxElementId.BRACKET_CLOSE){
						
				stack.push(new SintaxElement(SintaxElementId.CHAMADA_FUNCAO, laux));
				return true;
			}
		}	
		
		while(!laux.isEmpty()){
			stack.push(laux.remove(0));
		}	
		return false;
	}
	private static boolean reconhece_parametros_reais(List<Lexem> list){
		List<SintaxElement> laux=new LinkedList<SintaxElement>();
		if (stack.size()>0){
			
			laux.add(0,stack.pop());
			
			if (    laux.get(0).getId()==SintaxElementId.ID
				||  laux.get(0).getId()==SintaxElementId.CONST
				||  laux.get(0).getId()==SintaxElementId.CHAMADA_FUNCAO){
				
				stack.push(new SintaxElement(SintaxElementId.PAR_REAIS, laux));
				return true;
		
			}
		}
		if (stack.size()>2){
				
			laux.add(0,stack.pop());
			laux.add(0,stack.pop());
			
			if (    (    laux.get(0).getId()==SintaxElementId.ID
					||  laux.get(0).getId()==SintaxElementId.CONST
					||  laux.get(0).getId()==SintaxElementId.CHAMADA_FUNCAO)
					&& laux.get(1).getId()==SintaxElementId.COMMA
					&& laux.get(2).getId()==SintaxElementId.PAR_REAIS){
						
				stack.push(new SintaxElement(SintaxElementId.PAR_REAIS, laux));
				return true;
			}	
		}
		
		while(!laux.isEmpty()){
			stack.push(laux.remove(0));
		}
		return false;
	}
	private static boolean reconhece_if(List<Lexem> list){
		List<SintaxElement> laux=new LinkedList<SintaxElement>();
		if (stack.size()>5){
			
			laux.add(0,stack.pop());
			laux.add(0,stack.pop());
			laux.add(0,stack.pop());
			laux.add(0,stack.pop());
			laux.add(0,stack.pop());
			laux.add(0,stack.pop());
			
			
			if (    laux.get(0).getId()==SintaxElementId.IF
				&&  laux.get(1).getId()==SintaxElementId.EXP
				&&  laux.get(2).getId()==SintaxElementId.KEYWORD_THEN
				&&  laux.get(3).getId()==SintaxElementId.E
				&&  laux.get(4).getId()==SintaxElementId.KEYWORD_ELSE
				&&  laux.get(5).getId()==SintaxElementId.E){
				
				stack.push(new SintaxElement(SintaxElementId.IF, laux));
				return true;
			}
		}
		
		while(!laux.isEmpty()){
			stack.push(laux.remove(0));
		}	
		return false;
	}
	private static boolean reconhece_match(List<Lexem> list){
		List<SintaxElement> laux=new LinkedList<SintaxElement>();
		
		if (stack.size()>5){
			
			laux.add(0,stack.pop());
			laux.add(0,stack.pop());
			laux.add(0,stack.pop());
			laux.add(0,stack.pop());
			laux.add(0,stack.pop());
			laux.add(0,stack.pop());
			
			
			if (    laux.get(0).getId()==SintaxElementId.KEYWORD_MATCH
				&&  laux.get(1).getId()==SintaxElementId.EXP
				&&  laux.get(2).getId()==SintaxElementId.KEYWORD_WITH
				&&  laux.get(3).getId()==SintaxElementId.MATCH_VAR
				&&  laux.get(4).getId()==SintaxElementId.KEYWORD_ARROW
				&&  laux.get(5).getId()==SintaxElementId.E){
				
				stack.push(new SintaxElement(SintaxElementId.MATCH, laux));
				return true;
			}
		}
		if(stack.size()>9){// acho que falta alguma coisa
			laux.add(0,stack.pop());
			laux.add(0,stack.pop());
			laux.add(0,stack.pop());
			
			
			if (    laux.get(0).getId()==SintaxElementId.KEYWORD_MATCH
				&&  laux.get(1).getId()==SintaxElementId.EXP
				&&  laux.get(2).getId()==SintaxElementId.KEYWORD_WITH
				&&  laux.get(3).getId()==SintaxElementId.MATCH_VAR
				&&  laux.get(4).getId()==SintaxElementId.KEYWORD_ARROW
				&&  laux.get(5).getId()==SintaxElementId.E
				&&  laux.get(6).getId()==SintaxElementId.KEYWORD_MATCHBAR
				&&  laux.get(5).getId()==SintaxElementId.MATCH_VAR
				&&	laux.get(5).getId()==SintaxElementId.KEYWORD_ARROW
				&&  laux.get(5).getId()==SintaxElementId.E){
				
				stack.push(new SintaxElement(SintaxElementId.MATCH, laux));
				return true;
			}
		
		}
		
		while(!laux.isEmpty()){
			stack.push(laux.remove(0));
		}
		return false;
	}
	private static boolean reconhece_match_var(List<Lexem> list){
		List<SintaxElement> laux=new LinkedList<SintaxElement>();
		if (stack.size()>0){
			
			laux.add(0,stack.pop());
			
			if (    laux.get(0).getId()==SintaxElementId.ID
				||  laux.get(0).getId()==SintaxElementId.CONST
				||  laux.get(0).getId()==SintaxElementId.KEYWORD_JOKER){
				
				stack.push(new SintaxElement(SintaxElementId.MATCH_VAR, laux));
				return true;
		
			}
		}
		if (stack.size()>2){
				
			laux.add(0,stack.pop());
			laux.add(0,stack.pop());
			
			if (   (    laux.get(0).getId()==SintaxElementId.ID
					||  laux.get(0).getId()==SintaxElementId.CONST
					||  laux.get(0).getId()==SintaxElementId.KEYWORD_JOKER)
					&& laux.get(1).getId()==SintaxElementId.KEYWORD_AS
					&& laux.get(2).getId()==SintaxElementId.ID){
						
				stack.push(new SintaxElement(SintaxElementId.MATCH_VAR, laux));
				return true;
			}	
		}
		
		while(!laux.isEmpty()){
			stack.push(laux.remove(0));
		}
		return false;
	}
		
}
