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
	
	
	//Começar tudo novamente do zero
	//pensar numa estratergia de rastrear a lista de Lexemas para identificar os elementos
	
	// Retorna um unico elemento, que deve ser do tipo PROGRAMA, exceto se o parse não der certo
	/**
	 * @return (SintaxElement e)
	 * e.getId() == SintaxElementId.PROGRAMA 
	 */
	public static SintaxElement parseLexems(List<Lexem> list) throws Error {
		
		if (stack==null)stack=new Stack<SintaxElement>();
		reconhece_def_local_global(list);
		
		return null;
	}
	
	
	
	private static boolean reconhece_def_local_global(List<Lexem> list){
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
	
	//tive a ideia de ir empilhando enquanto nao for nenhuma definição da gramatica e qnd chegar numa monta um metodo que reconhce o que tem na lista
	private static boolean reconhece_e(List<Lexem> list){
		
		//if 
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
	
		
}
