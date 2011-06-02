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
				
		if (reconhece_definicao_local());
		return null;
	}
	
	
	private static boolean reconhece_definicao_local(){
		List<SintaxElement> laux=new LinkedList<SintaxElement>();
		
		if (list.size()>5){
		
			if (new SintaxElement(list.get(0)).getId() == SintaxElementId.KEYWORD_LET 
					&& new SintaxElement(list.get(1)).getId() == SintaxElementId.ID
					&& new SintaxElement(list.get(2)).getId() == SintaxElementId.ASSIGNMENT
					&& reconhece_e()
					&& new SintaxElement(list.get(0)).getId() == SintaxElementId.KEYWORD_IN
					&& reconhece_e()){
					
				laux.add(new SintaxElement(list.remove(0)));
				laux.add(new SintaxElement(list.remove(0)));
				laux.add(new SintaxElement(list.remove(0)));
				laux.add(stack.pop());//lista retornada por reconhece_e que esta na pilha
				laux.add(new SintaxElement(list.remove(0)));
				laux.add(stack.pop());//lista retornada por reconhece_e que esta na pilha
				
				stack.push(new SintaxElement(SintaxElementId.DEFINICAO_LOCAL, laux));
				return true;
			}
		}
		
			return false;
	}
	
	private static boolean reconhece_e(){
		List<SintaxElement> laux=new LinkedList<SintaxElement>();
		
		if (new SintaxElement(list.get(0)).getId() == SintaxElementId.ID
				||new SintaxElement(list.get(0)).getId() == SintaxElementId.CONST){//mais possibilidade aqui
			laux.add(new SintaxElement(list.remove(0)));
			while(new SintaxElement(list.get(0)).getId() == SintaxElementId.OP || new SintaxElement(list.get(0)).getId() == SintaxElementId.CONST || new SintaxElement(list.get(0)).getId() == SintaxElementId.ID){//adicionar algo que infira a ordem certa de aparecimento das definições
				laux.add(new SintaxElement(list.remove(0)));
			}
			
			stack.push(new SintaxElement(SintaxElementId.E, laux));
			return true;
		}
		return false;
	}
	
	/*private static boolean reconhece_def_local_global(List<Lexem> list){
		List<SintaxElement> laux=new LinkedList<SintaxElement>();
		laux.add(new SintaxElement(list.get(0)));
		
		if (laux.get(0).getId()==SintaxElementId.KEYWORD_LET){
				list.remove(0);//remove let da lista
				
				//detectou-se um let: enquanto exisistir  <id> e <op> empilhe
				
				while(new SintaxElement(list.get(0)).getId()==SintaxElementId.ID || new SintaxElement(list.get(0)).getId()==SintaxElementId.ASSIGNMENT || reconhece_e(list)){//cuidado tem que ser <id> seguido de "=", <e> , "in" e <e>
					laux.add(new SintaxElement(list.remove(0)));//espera-se um <id> ou <op>
					System.out.println(laux.size()-1+ " "+laux.get(laux.size()-1).getId());
				}
				
				if (new SintaxElement(list.get(0)).getId()==SintaxElementId.KEYWORD_IN){//seguido de um "in" é definição local
					laux.add(new SintaxElement(list.remove(0)));
					if ( reconhece_e(list)){
						stack.push(new SintaxElement(SintaxElementId.DEFINICAO_LOCAL,laux));
						return true;
					}
				}else{
					if ( reconhece_e(list)){
						stack.push(new SintaxElement(SintaxElementId.DEFINICAO_GLOBAL,laux));
						return true;
					}
				}
		}
		return false;
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
	*/	
}
