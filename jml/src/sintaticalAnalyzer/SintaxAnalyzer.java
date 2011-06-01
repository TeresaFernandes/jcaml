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
		
		
		
		return null;
	}
	
	
	
	private static boolean reconhece_expressao(List<Lexem> list){
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
