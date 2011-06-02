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
	private static List<Lexem> list; // Usem para o parsing
	
	
	//Começar tudo novamente do zero
	//pensar numa estratergia de rastrear a lista de Lexemas para identificar os elementos
	
	// Retorna um unico elemento, que deve ser do tipo PROGRAMA, exceto se o parse não der certo
	/**
	 * @return (SintaxElement e)
	 * e.getId() == SintaxElementId.PROGRAMA 
	 */
	public static SintaxElement parseLexems(List<Lexem> l) throws Error {
		
		if (stack==null)list=l;
		if (stack==null)stack=new Stack<SintaxElement>();
				
		if (reconhece_definicao_local()||reconhece_definicao_global()){};
		return null;
		
		
	}
	
	
	private static boolean reconhece_definicao_global(){
		List<SintaxElement> laux=new LinkedList<SintaxElement>();
				
		if (list.size()>3){
			if (	reconhece_Let()
					&& reconhece_ID()
					&& reconhece_Assigment()
					&& reconhece_e()){
				
				for (int i=0;i<3;i++){
					laux.add(stack.pop());	
				}
				stack.push(new SintaxElement(SintaxElementId.DEFINICAO_GLOBAL, laux));	
				return true;
			}
		}		
		
			return false;
	}
	
	//analise de definicao local deve vir antes da analise de definição global
	private static boolean reconhece_definicao_local(){
		List<SintaxElement> laux=new LinkedList<SintaxElement>();
		if (list.size()>5){
			
			if (	reconhece_Let()
					&& reconhece_ID()
					&& reconhece_Assigment()
					&& reconhece_e()
					&& reconhece_In()
					&& reconhece_e()){
				
				for (int i=0;i<6;i++){
					laux.add(stack.pop());	
				}
				
				stack.push(new SintaxElement(SintaxElementId.DEFINICAO_LOCAL, laux));
				return true;
			}
		}
		
			return false;
	}
	
	//verifica se é um lexema "let" e se for coloca na pilha
	private static boolean reconhece_Let(){
		SintaxElement se = new SintaxElement(list.remove(0));
		if (se.getId() == SintaxElementId.KEYWORD_LET){
			
			stack.push(se);
			return true;
		}
		list.add(0, se.getLexem());
		return false;
	}
	private static boolean reconhece_ID(){
		SintaxElement se = new SintaxElement(list.remove(0));
		if (se.getId() == SintaxElementId.ID){
			stack.push(se);
			return true;
		}
		list.add(0, se.getLexem());
		return false;
	}
	private static boolean reconhece_Assigment(){
		SintaxElement se = new SintaxElement(list.remove(0));
		if (se.getId() == SintaxElementId.ASSIGNMENT){
			stack.push(se);
			return true;
		}
		list.add(0, se.getLexem());
		return false;
	}
	private static boolean reconhece_In(){
		SintaxElement se = new SintaxElement(list.remove(0));
		if (se.getId() == SintaxElementId.KEYWORD_IN){
			stack.push(se);
			return true;
		}		
		list.add(0, se.getLexem());
		return false;
	}
	private static boolean reconhece_fun(){
		SintaxElement se = new SintaxElement(list.remove(0));
		if (se.getId() == SintaxElementId.KEYWORD_FUN){
			stack.push(se);
			return true;
		}		
		list.add(0, se.getLexem());
		return false;
	}
	
	private static boolean reconhece_bracketOpen(){
		SintaxElement se = new SintaxElement(list.remove(0));
		if (se.getId() == SintaxElementId.BRACKET_OPEN){
			stack.push(se);
			return true;
		}		
		list.add(0, se.getLexem());
		return false;
	}
	
	private static boolean reconhece_bracketClose(){
		SintaxElement se = new SintaxElement(list.remove(0));
		if (se.getId() == SintaxElementId.BRACKET_CLOSE){
			stack.push(se);
			return true;
		}		
		list.add(0, se.getLexem());
		return false;
	}
	
	private static boolean reconhece_par_formais(){
		SintaxElement se = new SintaxElement(list.remove(0));
		if (se.getId() == SintaxElementId.BRACKET_CLOSE){
			stack.push(se);
			return true;
		}		
		list.add(0, se.getLexem());
		return false;
	}
	private static boolean reconhece_arrow(){
		SintaxElement se = new SintaxElement(list.remove(0));
		if (se.getId() == SintaxElementId.KEYWORD_ARROW){
			stack.push(se);
			return true;
		}		
		list.add(0, se.getLexem());
		return false;
	}
	
	private static boolean reconhece_def_funcao(){
		List<SintaxElement> laux=new LinkedList<SintaxElement>();
		if (list.size()>5){
			
			if (	reconhece_fun()
					&& reconhece_bracketOpen()
					&& reconhece_par_formais()
					&& reconhece_bracketClose()
					&&reconhece_arrow()
					&& reconhece_exp()){
				
				for (int i=0;i<6;i++){
					laux.add(stack.pop());	
				}
				
				stack.push(new SintaxElement(SintaxElementId.DEF_FUNCAO, laux));
				return true;
			}
		}
		
			return false;
	}
	
	private static boolean reconhece_exp(){
		return false;
	}
	
	//INCOMPLETO
	private static boolean reconhece_e(){
		List<SintaxElement> laux=new LinkedList<SintaxElement>();
		
		if (reconhece_definicao_local()
			|| reconhece_def_funcao()
			|| reconhece_exp()){
			
			laux.add(stack.pop());
			stack.push(new SintaxElement(SintaxElementId.E, laux));
			return true;	
			
		}
			/*if (new SintaxElement(list.get(0)).getId() == SintaxElementId.ID
					||new SintaxElement(list.get(0)).getId() == SintaxElementId.CONST
					||list.get(0).getId()==LexemId.OPERATOR_UNARYMINUS
					||list.get(0).getId()==LexemId.OPERATOR_NOT){//mais possibilidade aqui
				
				laux.add(new SintaxElement(list.remove(0)));
				
				while((!list.isEmpty())&& (new SintaxElement(list.get(0)).getId() == SintaxElementId.OP 
						|| new SintaxElement(list.get(0)).getId() == SintaxElementId.CONST 
						|| new SintaxElement(list.get(0)).getId() == SintaxElementId.ID)){//adicionar algo que infira a ordem certa de aparecimento das definições
				
					laux.add(new SintaxElement(list.remove(0)));
					
				}
				
				stack.push(new SintaxElement(SintaxElementId.E, laux));

				return true;
		}*/
		return false;
	}
}