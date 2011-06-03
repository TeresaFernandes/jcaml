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

	//A ideia que usei foi fazer um encadeamento de chamadas de metodos para poder identificar os elementos da gramatica mediante a lista de lexemas que é recebida pelo construtor.
	//Vou empilhando os elementos que são possiveis reconhecer até que seja possivel formar um elemento da gramatica e esses elementos são desempilhados para formar um elemento de mais alto nivel.
	
	//AINDA NAO ESTA COMPLETO. AINDA FALTA TESTAR OUTROS CASOS, IR PREENCHENDO A TABELA DE SIMBOLOS E INSERIR AS EXCEÇÕES
	
	private static Stack<SintaxElement> stack;
	private static List<Lexem> list;
	
	
	// Retorna um unico elemento, que deve ser do tipo PROGRAMA, exceto se o parse não der certo
	/**
	 * @return (SintaxElement e)
	 * e.getId() == SintaxElementId.PROGRAMA 
	 */
	public static SintaxElement parseLexems(List<Lexem> l) throws Error {
		
		if (stack==null)list=l;
		if (stack==null)stack=new Stack<SintaxElement>();
				
		
		if (reconhece_programa()){
			
			System.out.println("Programa reconhecido");
			return stack.get(0);
			
		}		
		System.out.println("Programa não reconhecido");
		return null;		
	}
		
	private static boolean reconhece_programa(){
		List<SintaxElement> laux=new LinkedList<SintaxElement>();
		
		if ( list.size()==0) return true;
		
		//atenção aqui...pode ser que tenha mais que uma definicao.
		while(reconhece_definicao()&& reconhece_semicolon()){
            laux.add(stack.remove(0));
            laux.add(stack.remove(0));
            
            if (list.size()==0){
                stack.push(new SintaxElement(SintaxElementId.PROGRAMA, laux));
                return true;
            }
		}
		return false;
	}
	
	private static boolean reconhece_definicao(){
		List<SintaxElement> laux=new LinkedList<SintaxElement>();
		
		if (	reconhece_definicao_local()
				||reconhece_definicao_global()
				||reconhece_chamada_funcao()
				||reconhece_exp()){
			
			laux.add(stack.pop());
			stack.push(new SintaxElement(SintaxElementId.DEFINICAO, laux));	
			return true;
		}
		return false;
	}
	
	private static boolean reconhece_definicao_global(){
		List<SintaxElement> laux=new LinkedList<SintaxElement>();
		
		if (list.size()>3){
			if (	reconhece_Let()
					&& reconhece_ID()
					&& reconhece_Assigment()
					&& reconhece_e()){
				
				for (int i=3;i>=0;i--){
					laux.add(stack.remove(stack.size()-1-i));
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
					&& reconhece_In()//nao consigo chegar aqui
					&& reconhece_e()){//precisa mesmo identificar como fim um ponto e virgula?
				
				for (int i=5;i>=0;i--){
					laux.add(stack.remove(stack.size()-1-i));
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
	private static boolean reconhece_arrow(){
		SintaxElement se = new SintaxElement(list.remove(0));
		if (se.getId() == SintaxElementId.KEYWORD_ARROW){
			stack.push(se);
			return true;
		}		
		list.add(0, se.getLexem());
		return false;
	}
	private static boolean reconhece_OP(){
		SintaxElement se = new SintaxElement(list.remove(0));
		if (se.getId() == SintaxElementId.OP){
			stack.push(se);
			return true;
		}		
		list.add(0, se.getLexem());
		return false;
	}
	private static boolean reconhece_tipo(){
		SintaxElement se = new SintaxElement(list.remove(0));
		if (se.getId() == SintaxElementId.TIPO){
			stack.push(se);
			return true;
		}		
		list.add(0, se.getLexem());
		return false;
	}
	private static boolean reconhece_comma(){
		SintaxElement se = new SintaxElement(list.remove(0));
		if (se.getId() == SintaxElementId.COMMA){
			stack.push(se);
			return true;
		}		
		list.add(0, se.getLexem());
		return false;
	}
	
	private static boolean reconhece_semicolon(){
		if(list.size()>0){
			SintaxElement se = new SintaxElement(list.remove(0));
			if (se.getId() == SintaxElementId.SEMICOLON){
				stack.push(se);
				return true;
			}		
			list.add(0, se.getLexem());
		}
		return false;
	}
	
	private static boolean reconhece_colon(){
		SintaxElement se = new SintaxElement(list.remove(0));
		if (se.getId() == SintaxElementId.COLON){
			stack.push(se);
			return true;
		}		
		list.add(0, se.getLexem());
		return false;
	}
	private static boolean reconhece_const(){
		SintaxElement se = new SintaxElement(list.remove(0));
		if (se.getId() == SintaxElementId.CONST){
			
			stack.push(se);
			return true;
		}		
		list.add(0, se.getLexem());
		return false;
	}
	private static boolean reconhece_then(){
		SintaxElement se = new SintaxElement(list.remove(0));
		if (se.getId() == SintaxElementId.KEYWORD_THEN){
			stack.push(se);
			return true;
		}		
		list.add(0, se.getLexem());
		return false;
	}
	private static boolean reconhece_else(){
		SintaxElement se = new SintaxElement(list.remove(0));
		if (se.getId() == SintaxElementId.KEYWORD_ELSE){
			stack.push(se);
			return true;
		}		
		list.add(0, se.getLexem());
		return false;
	}
	private static boolean reconhece__(){
		SintaxElement se = new SintaxElement(list.remove(0));
		if (se.getId() == SintaxElementId.KEYWORD_JOKER){
			stack.push(se);
			return true;
		}		
		list.add(0, se.getLexem());
		return false;
	}
	private static boolean reconhece_as(){
		SintaxElement se = new SintaxElement(list.remove(0));
		if (se.getId() == SintaxElementId.KEYWORD_AS){
			stack.push(se);
			return true;
		}		
		list.add(0, se.getLexem());
		return false;
	}
	private static boolean reconhece_keymatch() {
		SintaxElement se = new SintaxElement(list.remove(0));
		if (se.getId() == SintaxElementId.KEYWORD_MATCH){
			stack.push(se);
			return true;
		}		
		list.add(0, se.getLexem());
		return false;
	}
	private static boolean reconhece_with() {
		SintaxElement se = new SintaxElement(list.remove(0));
		if (se.getId() == SintaxElementId.KEYWORD_WITH){
			stack.push(se);
			return true;
		}		
		list.add(0, se.getLexem());
		return false;
	}
	private static boolean reconhece_match_bar(){
		SintaxElement se = new SintaxElement(list.remove(0));
		if (se.getId() == SintaxElementId.KEYWORD_MATCHBAR){
			stack.push(se);
			return true;
		}		
		list.add(0, se.getLexem());
		return false;
	}
	private static boolean reconhece_par_formais(){
		List<SintaxElement> laux=new LinkedList<SintaxElement>();
		
		if (list.size()>2){
			if (	reconhece_par()
					&& reconhece_comma()
					&& reconhece_par_formais()){
		
				for (int i=2;i>=0;i--){
					laux.add(stack.remove(stack.size()-1-i));
				}
				
				stack.push(new SintaxElement(SintaxElementId.PAR_FORMAIS, laux));
				return true;
				
			//pode ser que tenha reconhecido um <par> ou um comma mas não tenha resonhecido o <par_formal>.Dai tem que colocar de volta o <par> ou comma na lista.
				//nao foi testado
			}else if (stack.lastElement().getId()==SintaxElementId.PAR){
				list.add(0,stack.pop().getLexem());
			}else if (stack.lastElement().getId()==SintaxElementId.COMMA){
				list.add(0,stack.pop().getLexem());
				list.add(0,stack.pop().getLexem());
			}
				
		}
		
		if (list.size()>0){
			if (reconhece_par()){
				
				laux.add(stack.pop());	
								
				stack.push(new SintaxElement(SintaxElementId.PAR_FORMAIS, laux));
				return true;
			}
		}
			return false;
	}

	private static boolean reconhece_par(){
		List<SintaxElement> laux=new LinkedList<SintaxElement>();
		if (list.size()>2){
			if (	reconhece_ID() 
					&& reconhece_colon()
					&& reconhece_tipo()){
				
				for (int i=2;i>=0;i--){
					laux.add(stack.remove(stack.size()-1-i));
				}
				
				stack.push(new SintaxElement(SintaxElementId.PAR, laux));
				return true;
				
			}
		}
		return false;
		
	}
	
	
	private static boolean reconhece_def_funcao(){
		List<SintaxElement> laux=new LinkedList<SintaxElement>();
		if (list.size()>5){
			
			if (	reconhece_fun()
					&& reconhece_bracketOpen()
					&& reconhece_par_formais()
					&& reconhece_bracketClose()
					&& reconhece_arrow()
					&& reconhece_exp()){
				
				for (int i=5;i>=0;i--){
					laux.add(stack.remove(stack.size()-1-i));
				}
				
				stack.push(new SintaxElement(SintaxElementId.DEF_FUNCAO, laux));
				return true;
			}
		}		
			return false;
	}

	private static boolean reconhece_chamada_funcao(){
		List<SintaxElement> laux=new LinkedList<SintaxElement>();
		if (list.size()>3){
			
			if (	reconhece_ID()
					&& reconhece_bracketOpen()
					&& reconhece_par_reais()
					&& reconhece_bracketClose()){
				
				for (int i=3;i>=0;i--){
					laux.add(stack.remove(stack.size()-1-i));
				}
				
				stack.push(new SintaxElement(SintaxElementId.CHAMADA_FUNCAO, laux));
				return true;
				
				//num testei ainda
			}else if (stack.lastElement().getId()==SintaxElementId.ID){
				list.add(0,stack.pop().getLexem());
			}else if (stack.lastElement().getId()==SintaxElementId.BRACKET_OPEN){
				list.add(0,stack.pop().getLexem());
				list.add(0,stack.pop().getLexem());
			}else if (stack.lastElement().getId()==SintaxElementId.PAR_REAIS){
				list.add(0,stack.pop().getLexem());
				list.add(0,stack.pop().getLexem());
				list.add(0,stack.pop().getLexem());
			}
		}		
		
		if (list.size()>2){
			
			if (	reconhece_ID()
					&& reconhece_bracketOpen()
					&& reconhece_bracketClose()){
				
				for (int i=2;i>=0;i--){
					laux.add(stack.remove(stack.size()-1-i));
				}
				
				stack.push(new SintaxElement(SintaxElementId.CHAMADA_FUNCAO, laux));
				return true;
			}
		}	
			return false;
	}
	
	private static boolean reconhece_par_reais(){
		List<SintaxElement> laux=new LinkedList<SintaxElement>();
		if (list.size()>2){
			
			if (	(reconhece_ID()
					|| reconhece_const()
					|| reconhece_chamada_funcao())
					&& reconhece_comma()
					&& reconhece_par_reais()){
				
				for (int i=2;i>=0;i--){
					laux.add(stack.remove(stack.size()-1-i));
				}
				
				stack.push(new SintaxElement(SintaxElementId.PAR_REAIS, laux));
				return true;
				
				//nao foi testado
			}else if (stack.lastElement().getId()==SintaxElementId.ID || stack.lastElement().getId()==SintaxElementId.CONST || stack.lastElement().getId()==SintaxElementId.CHAMADA_FUNCAO){
				list.add(0,stack.pop().getLexem());	
			}else if (stack.lastElement().getId()==SintaxElementId.COMMA){
				list.add(0,stack.pop().getLexem());	
				list.add(0,stack.pop().getLexem());	
			}
		}		
		
		if (list.size()>0){
			
			if (	reconhece_ID()
					|| reconhece_const()
					|| reconhece_chamada_funcao()){
				
					laux.add(stack.pop());
				
				stack.push(new SintaxElement(SintaxElementId.PAR_REAIS, laux));
				return true;
			}
		}	
		return false;		
	}
	private static boolean reconhece_exp_simples(){
		List<SintaxElement> laux=new LinkedList<SintaxElement>();
		
		if (list.size()>2){
			if ((reconhece_ID() || reconhece_chamada_funcao() || reconhece_const())//problema aque..pode ser que tenha uma constante e o posterior nao seja um operador, mas a constante permanece na pilha.para resolver isso no else ha uma instrução que jah forma um elemento <exp_simples> caso o elemento da lista tenha sido um <const>, <id> ou<chamada_funcao>. 
					&& reconhece_OP() && reconhece_exp_simples()){
				
				for (int i=2;i>=0;i--){
					laux.add(stack.remove(stack.size()-1-i));
				}
				
				stack.push(new SintaxElement(SintaxElementId.EXP_SIMPLES, laux));
				return true;
				
				//nao foi testado
			}else if (stack.lastElement().getId()==SintaxElementId.ID || stack.lastElement().getId()==SintaxElementId.CHAMADA_FUNCAO || stack.lastElement().getId()==SintaxElementId.CONST){
				list.add(0,stack.pop().getLexem());
			}else if (stack.lastElement().getId()==SintaxElementId.OP){
				list.add(0,stack.pop().getLexem());
				list.add(0,stack.pop().getLexem());
			}
		}
		if (list.size()>0){
			if (reconhece_const()||reconhece_ID() || reconhece_chamada_funcao()){
				
				laux.add(stack.pop());

				stack.push(new SintaxElement(SintaxElementId.EXP_SIMPLES, laux));
				return true;
			}
		}
		
		return false;
	}
	
	private static boolean reconhece_match(){
		List<SintaxElement> laux=new LinkedList<SintaxElement>();
		
		if (list.size()>3){
			if (	reconhece_keymatch() 
					&& reconhece_exp()
					&& reconhece_with()
					&& reconhece_match_line()){
				
				for (int i=3;i>=0;i--){
					laux.add(stack.remove(stack.size()-1-i));
				}
				
				stack.push(new SintaxElement(SintaxElementId.MATCH, laux));
				return true;
			}
		}
		return false;
	}
	
	private static boolean reconhece_match_line(){
		List<SintaxElement> laux=new LinkedList<SintaxElement>();
		
		if (list.size()>4){
			if (	reconhece_match_var() 
					&& reconhece_arrow()
					&& reconhece_e()
					&& reconhece_match_bar()
					&& reconhece_match_line()){
				
				for (int i=4;i>=0;i--){
					laux.add(stack.remove(stack.size()-1-i));
				}
				
				stack.push(new SintaxElement(SintaxElementId.MATCH_LINE, laux));
				return true;
				
				//nao foi testado
			}else if (stack.lastElement().getId()==SintaxElementId.MATCH_VAR){
				list.add(0,stack.pop().getLexem());
			}else if (stack.lastElement().getId()==SintaxElementId.KEYWORD_ARROW){
				list.add(0,stack.pop().getLexem());
				list.add(0,stack.pop().getLexem());
			}else if (stack.lastElement().getId()==SintaxElementId.E){
				list.add(0,stack.pop().getLexem());
				list.add(0,stack.pop().getLexem());
				list.add(0,stack.pop().getLexem());
			}else if (stack.lastElement().getId()==SintaxElementId.KEYWORD_MATCHBAR){
				list.add(0,stack.pop().getLexem());
				list.add(0,stack.pop().getLexem());
				list.add(0,stack.pop().getLexem());
				list.add(0,stack.pop().getLexem());
			}
		}
		if (list.size()>2){
			if (	reconhece_match_var() 
					&& reconhece_arrow()
					&& reconhece_e()){
				
				for (int i=2;i>=0;i--){
					laux.add(stack.remove(stack.size()-1-i));
				}
				
				stack.push(new SintaxElement(SintaxElementId.MATCH_LINE, laux));
				return true;
			}
		}
		return false;
	}
	private static boolean reconhece_match_var(){
		List<SintaxElement> laux=new LinkedList<SintaxElement>();
		if (list.size()>2){
			if ((reconhece_const()||reconhece_ID()||reconhece__())
					&&reconhece_as()
					&&reconhece_ID()){
				
				for (int i=2;i>=0;i--){
					laux.add(stack.remove(stack.size()-1-i));
				}
				stack.push(new SintaxElement(SintaxElementId.MATCH_VAR, laux));
				return true;
			}
			
			//nao foi testado
		}else if (stack.lastElement().getId()==SintaxElementId.CONST||stack.lastElement().getId()==SintaxElementId.ID||stack.lastElement().getId()==SintaxElementId.KEYWORD_JOKER){
			list.add(0,stack.pop().getLexem());
		}else if (stack.lastElement().getId()==SintaxElementId.KEYWORD_AS){
			list.add(0,stack.pop().getLexem());
			list.add(0,stack.pop().getLexem());
		}
		
		if (list.size()>0){
			if (reconhece_const()||reconhece_ID()||reconhece__()){
				
				laux.add(stack.pop());	
				stack.push(new SintaxElement(SintaxElementId.MATCH_VAR, laux));
				return true;
			}
		}
		return false;
	}
	private static boolean reconhece_if(){
		List<SintaxElement> laux=new LinkedList<SintaxElement>();
		
		if (list.size()>5){
			if ( 	reconhece_if()
					&& reconhece_exp()
					&& reconhece_then()
					&& reconhece_e()
					&& reconhece_else()
					&& reconhece_e()){
				
				for (int i=5;i>=0;i--){
					laux.add(stack.remove(stack.size()-1-i));
				}
				
				stack.push(new SintaxElement(SintaxElementId.IF, laux));
				return true;
			}
		}
		
		return false;
	}
	
	private static boolean reconhece_exp(){
		List<SintaxElement> laux=new LinkedList<SintaxElement>();
			if (	reconhece_exp_simples()
					|| reconhece_match()
					|| reconhece_if()){
				
				laux.add(stack.pop());
				
				stack.push(new SintaxElement(SintaxElementId.EXP, laux));
				return true;
			}
		return false;
	}
	
	
	private static boolean reconhece_e(){
		List<SintaxElement> laux=new LinkedList<SintaxElement>();
		if (reconhece_exp()
			||reconhece_definicao_local()
			|| reconhece_def_funcao() ){
			
			laux.add(stack.pop());
			stack.push(new SintaxElement(SintaxElementId.E, laux));
			return true;	
			
		}
		return false;
	}
}