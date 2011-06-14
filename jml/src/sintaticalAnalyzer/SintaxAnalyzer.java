package sintaticalAnalyzer;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import CommonClasses.Lexem;
import CommonClasses.LexemId;
import CommonClasses.SintaxElement;
import CommonClasses.SintaxElementId;
import CommonClasses.Error;
import java.io.IOException;

public class SintaxAnalyzer {


	private static Stack<SintaxElement> stack;
	private static List<Lexem> list;

	public static SintaxElement parseLexems(List<Lexem> l) throws Error {

		if (list==null)list=l;
		if (stack==null)stack=new Stack<SintaxElement>();

                /*enquanto tiver elemento na lista e ninguem reconhecer
                algum lexema um novo elemento da lista � inserido na pilha.
                Mas se algum elemento for reconhecido, os m�todos de
                reconhecimentos sao executados novamente.*/
		while (!list.isEmpty()){

                    if(!reconheceALL()){
			stack.push(new SintaxElement(list.remove(0)));
                    }
		}

                /*Quando acabar os elementos da lista, os que estam na pilha
                ainda podem ser reconhecidos pelos metodos, pricipalemente pelo
                reconhece_defini��o e reconhece_programa.*/
		while (reconheceALL()){}
                
                /*O programa sao reconhecido se tiver apenas um programa na pilha*/
		if (stack.size()==1 && list.size()==0){
			System.out.print("Reconheceu");
			return stack.pop();
		}else{
                        System.out.print("nao reconheceu");
                        lancaExcecao();
		}

		return null;

	}

        private static void lancaExcecao() throws Error{
            List<SintaxElement> l =stack.get(0).getLexems();
            while (l.get(0).getLexems()!=null){
                l=l.get(0).getLexems();
            }
            Error e = new Error(19);
            e.setLine(l.get(0).getLexem().getLine());
            e.setExtra(". Proximo a \"" + l.get(0).getLexem().getLex()+"\"");
            throw e;
        }

        /*Centraliza as chamadas de fun��o ( quest�o de organiza��o s�)*/
        private static boolean reconheceALL() throws Error{

            return reconhece_programa()
                 || reconhece_definicao()
                 || reconhece_def_local()
                 || reconhece_def_global()
                 || reconhece_exp()
                 || reconhece_e()
                 || reconhece_def_funcao()
                 || reconhece_par_formais()
                 || reconhece_par()
                 || reconhece_exp_simples()
                 || reconhece_chamada_funcao()
                 || reconhece_parametros_reais()
                 || reconhece_if()
                 || reconhece_match()
                 || reconhece_match_line()
                 || reconhece_match_var();
           
        }

        /*Os m�todos de reconhecimento comparam os elementos de acordo com o padr�o descrito na gram�tica.
         * Basicamente, eles comparam os elementos que j� estam na pilha e verificam se os que ainda estam
         * na fila s�o os esperados para formar uma determinada defini��o. Ent�o, os elementos s�o removidos
         * e inseridos numa lista auxiliar com a marca/nome da defini��o que ser� reposta na pilha.
         */

	private static boolean reconhece_programa(){
		List<SintaxElement> laux=new LinkedList<SintaxElement>();

                
            if (list.size()==0){
                while( stack.size()>1 && stack.get(stack.size()-2).getId()== SintaxElementId.DEFINICAO
                                      && stack.get(stack.size()-1).getId()== SintaxElementId.SEMICOLON){

                    laux.add(0,stack.pop());
                    laux.add(0,stack.pop());
                }
                
                if(laux.size()>0){
                    stack.push(new SintaxElement(SintaxElementId.PROGRAMA, laux));
                    return true;
                }
            }

		return false;
	}

	private static boolean reconhece_definicao(){
		List<SintaxElement> laux=new LinkedList<SintaxElement>();

		if(stack.size()>1 && (stack.get(stack.size()-2).getId()==SintaxElementId.CHAMADA_FUNCAO
                                      ||stack.get(stack.size()-2).getId()==SintaxElementId.E
                                      ||stack.get(stack.size()-2).getId()==SintaxElementId.DEFINICAO_GLOBAL)
                                  && stack.get(stack.size()-1).getId()==SintaxElementId.SEMICOLON){

                    /*como ele s� reconhece quando o ponto e virgula j� est� na pilha,
                    dai tem que remover e inserir a defini��o na mesma posi��o (entre os ponto e virgula)*/
                    laux.add(0,stack.remove(stack.size()-2));
                    stack.add(stack.size()-1,new SintaxElement(SintaxElementId.DEFINICAO, laux));
                    return true;
		}
		return false;
	}

	private static boolean reconhece_def_global(){
		List<SintaxElement> laux=new LinkedList<SintaxElement>();

		if (stack.size()>3 && stack.get(stack.size()-1).getId()==SintaxElementId.E
                                   && stack.get(stack.size()-2).getId()==SintaxElementId.ASSIGNMENT
                                   && stack.get(stack.size()-3).getId()==SintaxElementId.ID
                                   && stack.get(stack.size()-4).getId()==SintaxElementId.KEYWORD_LET
		  && list.size()>0 && list.get(0).getId() == LexemId.SEMICOLON){

                    laux.add(0,stack.pop());
                    laux.add(0,stack.pop());
                    laux.add(0,stack.pop());
                    laux.add(0,stack.pop());
                    stack.push(new SintaxElement(SintaxElementId.DEFINICAO_GLOBAL, laux));
                    return true;
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

                    /*situa��es em que o <exp> n�o deve ser "traduzido" como um <e> :
                    quando a express�o vier ap�s a palavra "IF" ou "MATCH" ou ap�s
                    os marcadores ") ->" que fazem parte de uma definicao de uma fun��o*/
                   if(stack.size()>1 && (stack.get(stack.size()-2).getId()==SintaxElementId.KEYWORD_IF
                                        || stack.get(stack.size()-2).getId()==SintaxElementId.KEYWORD_MATCH)){return false;}

                   if(stack.size()>2 && stack.get(stack.size()-2).getId()==SintaxElementId.KEYWORD_ARROW
                                     && stack.get(stack.size()-3).getId()==SintaxElementId.BRACKET_CLOSE){return false;}

                   
                    laux.add(0,stack.pop());
                    stack.push(new SintaxElement(SintaxElementId.E, laux));
                    return true;
		}
		return false;
	}

	private static boolean reconhece_def_funcao(){
		List<SintaxElement> laux=new LinkedList<SintaxElement>();

                //com parametros formais
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
		}
                //sem parametros formais
                if (stack.size()>4 && stack.get(stack.size()-1).getId()==SintaxElementId.EXP
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

        if(stack.size()>0 && stack.get(stack.size()-1).getId()==SintaxElementId.PAR){

            if(list.size()>0 && !(list.get(0).getId()==LexemId.BRACKET_CLOSE)){return false;}

            laux.add(0,stack.pop());

        while (stack.size()>1 && stack.get(stack.size()-1).getId()==SintaxElementId.COMMA
                                && stack.get(stack.size()-2).getId()==SintaxElementId.PAR){

            laux.add(0,stack.pop());
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

	private static boolean reconhece_exp(){
		List<SintaxElement> laux=new LinkedList<SintaxElement>();
		if (stack.size()>0 && (stack.peek().getId()==SintaxElementId.EXP_SIMPLES
                                        || stack.peek().getId()==SintaxElementId.MATCH
                                        || stack.peek().getId()==SintaxElementId.IF)){
                    
                    if (list.size()>0 && (list.get(0).getId()==LexemId.COMMA || list.get(0).getId()==LexemId.BRACKET_CLOSE)) {return false;}

                    laux.add(0,stack.pop());
                    stack.push(new SintaxElement(SintaxElementId.EXP, laux));
                    return true;
		}
		return false;
	}

	private static boolean reconhece_exp_simples(){

            List<SintaxElement> laux=new LinkedList<SintaxElement>();
            if(stack.size()>0 && (stack.peek().getId()==SintaxElementId.ID
                                    || stack.peek().getId()==SintaxElementId.CHAMADA_FUNCAO
                                    || stack.peek().getId()==SintaxElementId.CONST)){

                     /*situa��es em que o <id>, <chamada_fun> ou <const> n�o deve ser "traduzidos" como um <exp_simples>:
                    quando a express�o vem antes de um "(" , "," , "LET" , "|" , "WITH" , ") ->" ou o pr�ximo elemento seja um "(" */
                    if(stack.size()>1 && (stack.get(stack.size()-2).getId()==SintaxElementId.BRACKET_OPEN
					|| stack.get(stack.size()-2).getId()==SintaxElementId.COMMA
                                        || stack.get(stack.size()-2).getId()==SintaxElementId.KEYWORD_LET
                                        || stack.get(stack.size()-2).getId()==SintaxElementId.KEYWORD_MATCHBAR
                                        || stack.get(stack.size()-2).getId()==SintaxElementId.KEYWORD_WITH)){return false;}

                    //if(stack.size()>2 && stack.get(stack.size()-2).getId()==SintaxElementId.KEYWORD_ARROW && stack.get(stack.size()-3).getId()==SintaxElementId.BRACKET_CLOSE){return false;}

                     //se ainda tiver mais elementos de express�o na lista espere ate que eles sejam passados para a pilha
                     /* outras situa��es em que o <id>, <chamada_fun> ou <const> n�o deve ser "traduzidos" como um <exp_simples>:
                       quando o proximo elemento na lista eh: "(" , ")" , "as" , "OP" ou "=" */
                    if (list.size()>0 && (list.get(0).getId()==LexemId.BRACKET_OPEN
                                           // ||list.get(0).getId()==LexemId.BRACKET_CLOSE
                                            ||list.get(0).getId()==LexemId.KEYWORD_AS
                                            || new SintaxElement(list.get(0)).getId()==SintaxElementId.OP
                                            || list.get(0).getId()==LexemId.ASSIGNMENT)){return false;}

                      laux.add(0,stack.pop());

                      //lembrar que o "=" tambem eh um operador
                      while(stack.size()>1 && (stack.get(stack.size()-1).getId()==SintaxElementId.OP
                                               || stack.get(stack.size()-1).getId()==SintaxElementId.ASSIGNMENT )
                                           &&(stack.get(stack.size()-2).getId()==SintaxElementId.ID
                                              || stack.get(stack.size()-2).getId()==SintaxElementId.CONST
                                              || stack.get(stack.size()-2).getId()==SintaxElementId.CHAMADA_FUNCAO)){

                          if (stack.get(stack.size()-1).getId()==SintaxElementId.ASSIGNMENT && stack.size()>2 && (stack.get(stack.size()-3).getId()==SintaxElementId.KEYWORD_LET)){break;} //testa se eh uma declara��o
                          
                          laux.add(0,stack.pop());
                          laux.add(0,stack.pop());
                      }

                      if (stack.size()>0 && stack.peek().getId()==SintaxElementId.OP) laux.add(0,stack.pop()); //pedreiragem para deixar passar uma express�o que come�a com um operador
                      
                      stack.push(new SintaxElement(SintaxElementId.EXP_SIMPLES, laux));
                      return true;
                }
          return false;
	}

	private static boolean reconhece_chamada_funcao(){
		List<SintaxElement> laux=new LinkedList<SintaxElement>();

                //com parametros reais
		if(stack.size()>3 && stack.get(stack.size()-1).getId()==SintaxElementId.BRACKET_CLOSE
                                  && stack.get(stack.size()-2).getId()==SintaxElementId.PAR_REAIS
                                  && stack.get(stack.size()-3).getId()==SintaxElementId.BRACKET_OPEN
                                  && stack.get(stack.size()-4).getId()==SintaxElementId.ID){

			laux.add(0,stack.pop());
			laux.add(0,stack.pop());
			laux.add(0,stack.pop());
			laux.add(0,stack.pop());
			stack.push(new SintaxElement(SintaxElementId.CHAMADA_FUNCAO, laux));
			return true;
		}

                //sem parametros reais
		if(stack.size()>2 && stack.get(stack.size()-1).getId()==SintaxElementId.BRACKET_CLOSE
                                  && stack.get(stack.size()-2).getId()==SintaxElementId.BRACKET_OPEN
                                  && stack.get(stack.size()-3).getId()==SintaxElementId.ID){

			laux.add(0,stack.pop());
			laux.add(0,stack.pop());
			laux.add(0,stack.pop());
			stack.push(new SintaxElement(SintaxElementId.CHAMADA_FUNCAO, laux));
			return true;
		}

		return false;
	}

	private static boolean reconhece_parametros_reais(){
		List<SintaxElement> laux=new LinkedList<SintaxElement>();

                if(stack.size()>0 && (stack.peek().getId()==SintaxElementId.ID
                                        || stack.peek().getId()==SintaxElementId.CHAMADA_FUNCAO
                                        || stack.peek().getId()==SintaxElementId.CONST
                                        || stack.peek().getId()==SintaxElementId.EXP_SIMPLES)){
                    
                    //se ainda tiver mais elementos parametro_real na lista espere ate que eles sejam passados para a pilha
                    if (list.size()>0 && (list.get(0).getId()==LexemId.COMMA
                                           /*|| list.get(0).getId()==LexemId.KEYWORD_WITH
                                           || list.get(0).getId()==LexemId.ASSIGNMENT
                                           || new SintaxElement(list.get(0)).getId()==SintaxElementId.OP*/)){return false;} //VER
                    //tem que ter antes um "(" e depois um ")", ou ter depois um ","
                    if (!(stack.size()>1 && list.size()>0 && ((stack.get(stack.size()-2).getId()==SintaxElementId.BRACKET_OPEN && list.get(0).getId()==LexemId.BRACKET_CLOSE)
                                                            ||list.get(0).getId()==LexemId.COMMA
                                                            ||list.get(0).getId()==LexemId.BRACKET_CLOSE ))){return false;}

                    if (stack.size()>1 &&  stack.get(stack.size()-2).getId()==SintaxElementId.OP){return false;}///affff
                    
                    laux.add(0,stack.pop());

                    while(stack.size()>1 && stack.get(stack.size()-1).getId()==SintaxElementId.COMMA
                                         && (stack.get(stack.size()-2).getId()==SintaxElementId.ID
                                              || stack.get(stack.size()-2).getId()==SintaxElementId.CONST
                                              || stack.get(stack.size()-2).getId()==SintaxElementId.CHAMADA_FUNCAO
                                              || stack.get(stack.size()-2).getId()==SintaxElementId.EXP_SIMPLES)){

                              laux.add(0,stack.pop());
                              laux.add(0,stack.pop());
                      }

                    stack.push(new SintaxElement(SintaxElementId.PAR_REAIS, laux));
                    return true;
                }
            return false;
	}

	private static boolean reconhece_if(){
		List<SintaxElement> laux=new LinkedList<SintaxElement>();

                if(stack.size()>5 && stack.get(stack.size()-1).getId()==SintaxElementId.E
                                  && stack.get(stack.size()-2).getId()==SintaxElementId.KEYWORD_ELSE
                                  && stack.get(stack.size()-3).getId()==SintaxElementId.E
                                  && stack.get(stack.size()-4).getId()==SintaxElementId.KEYWORD_THEN
                                  && stack.get(stack.size()-5).getId()==SintaxElementId.EXP
                                  && stack.get(stack.size()-6).getId()==SintaxElementId.KEYWORD_IF){

                    laux.add(0,stack.pop());
                    laux.add(0,stack.pop());
                    laux.add(0,stack.pop());
                    laux.add(0,stack.pop());
                    laux.add(0,stack.pop());
                    laux.add(0,stack.pop());
                    stack.push(new SintaxElement(SintaxElementId.IF, laux));
                    return true;
		}

                if(stack.size()>6 && stack.get(stack.size()-1).getId()==SintaxElementId.BRACKET_CLOSE
                                  && stack.get(stack.size()-2).getId()==SintaxElementId.BRACKET_OPEN
                                  && stack.get(stack.size()-3).getId()==SintaxElementId.KEYWORD_ELSE
                                  && stack.get(stack.size()-4).getId()==SintaxElementId.E
                                  && stack.get(stack.size()-5).getId()==SintaxElementId.KEYWORD_THEN
                                  && stack.get(stack.size()-6).getId()==SintaxElementId.EXP
                                  && stack.get(stack.size()-7).getId()==SintaxElementId.KEYWORD_IF){

                    laux.add(0,stack.pop());
                    laux.add(0,stack.pop());
                    laux.add(0,stack.pop());
                    laux.add(0,stack.pop());
                    laux.add(0,stack.pop());
                    laux.add(0,stack.pop());
                    laux.add(0,stack.pop());
                    stack.push(new SintaxElement(SintaxElementId.IF, laux));
                    return true;
		}
            return false;
	}

	private static boolean reconhece_match(){
		List<SintaxElement> laux=new LinkedList<SintaxElement>();

                if(stack.size()>3 && stack.get(stack.size()-1).getId()==SintaxElementId.MATCH_LINE
                                  && stack.get(stack.size()-2).getId()==SintaxElementId.KEYWORD_WITH
                                  && stack.get(stack.size()-3).getId()==SintaxElementId.EXP
                                  && stack.get(stack.size()-4).getId()==SintaxElementId.KEYWORD_MATCH){

                    laux.add(0,stack.pop());
                    laux.add(0,stack.pop());
                    laux.add(0,stack.pop());
                    laux.add(0,stack.pop());
                    stack.push(new SintaxElement(SintaxElementId.MATCH, laux));
                    return true;
		}
            return false;
	}

	private static boolean reconhece_match_line(){
            List<SintaxElement> laux=new LinkedList<SintaxElement>();

             if(stack.size()>2 && stack.get(stack.size()-1).getId()==SintaxElementId.E
                               && stack.get(stack.size()-2).getId()==SintaxElementId.KEYWORD_ARROW
                               && stack.get(stack.size()-3).getId()==SintaxElementId.MATCH_VAR){

                  //se ainda tiver mais elementos que compoem um match_line na lista espere ate que eles sejam passados para a pilha
                  if (list.size()>0 && list.get(0).getId()==LexemId.KEYWORD_MATCHBAR){return false;}

                  laux.add(0,stack.pop());
                  laux.add(0,stack.pop());
                  laux.add(0,stack.pop());

                  while(stack.size()>3 && stack.get(stack.size()-1).getId()==SintaxElementId.KEYWORD_MATCHBAR
                                       && stack.get(stack.size()-2).getId()==SintaxElementId.E
                                       && stack.get(stack.size()-3).getId()==SintaxElementId.KEYWORD_ARROW
                                       && stack.get(stack.size()-4).getId()==SintaxElementId.MATCH_VAR){//VER

                      laux.add(0,stack.pop());
                      laux.add(0,stack.pop());
                      laux.add(0,stack.pop());
                      laux.add(0,stack.pop());
                  }

                  stack.push(new SintaxElement(SintaxElementId.MATCH_LINE, laux));
                  return true;
                }
            return false;
	}

	private static boolean reconhece_match_var(){
		List<SintaxElement> laux=new LinkedList<SintaxElement>();

                //com a palavra chave "as"
		if(stack.size()>2 && (stack.get(stack.size()-1).getId()==SintaxElementId.CONST
                                        || stack.get(stack.size()-1).getId()==SintaxElementId.ID
                                        || stack.get(stack.size()-1).getId()==SintaxElementId.KEYWORD_JOKER)
                                  && stack.get(stack.size()-2).getId()==SintaxElementId.KEYWORD_AS
                                  && stack.get(stack.size()-3).getId()==SintaxElementId.ID){

                    laux.add(0,stack.pop());
                    laux.add(0,stack.pop());
                    laux.add(0,stack.pop());
                    stack.push(new SintaxElement(SintaxElementId.MATCH_VAR, laux));
                    return true;
		}
                
                //sem a palavra chave "as"
		if(stack.size()>0 && (stack.get(stack.size()-1).getId()==SintaxElementId.CONST
                                        || stack.get(stack.size()-1).getId()==SintaxElementId.ID
                                        || stack.get(stack.size()-1).getId()==SintaxElementId.KEYWORD_JOKER)){

                    //para ser um match_var, ele deve ser precedido de "|" ou "WITH"
                    if (!(stack.size()>1 && (stack.get(stack.size()-2).getId()==SintaxElementId.KEYWORD_MATCHBAR
                                            || stack.get(stack.size()-2).getId()==SintaxElementId.KEYWORD_WITH))){return false;} //para certificar que se tiver mais de um match_var deve ser precedido de "with" ou |

                    laux.add(0,stack.pop());
                    stack.push(new SintaxElement(SintaxElementId.MATCH_VAR, laux));
                    return true;
		}
            return false;
	}
}