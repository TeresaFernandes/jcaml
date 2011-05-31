package lexicalAnalyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

import CommonClasses.Lexem;
import CommonClasses.LexemId;
import CommonClasses.Error;

import symbolTable.Table;

//import javax.swing.JOptionPane;

public class Analyzer {
	public static Table s;
	/*static private int getFirstInvalidNameChar(String s) {
		for (int x=0; x<s.length(); x++) {
			if (Character.isDigit(s.charAt(x)) ||
				Character.isLetter(s.charAt(x)) ||
				s.charAt(x)=='_') continue;
			return x;
		}		
		return -1;
	}*/
	
	/*static private int getFirstSpace(String s){
		int result=-1;
			for (int x=0; x<s.length(); x++) {
				if (s.substring(x, x+1).compareTo("\"")==0){
					for (int y=x+2; y<s.length(); y++) {
						if (s.substring(y, y+1).compareTo("\"")==0){
								result = y+2;
								break;
						}
					}
				}
				if (s.substring(x, x).compareTo("'")==0){
					for (int y=x+1; y<s.length(); y++) {
						if (s.substring(y, y).compareTo("'")==0){
								result = y+1;
								break;
						}
					}
				}
				if(s.substring(x, x+1).compareTo("[|")==0){
					for (int y=x+2; y<s.length(); y++) {
						if (s.substring(y, y+1).compareTo("|]")==0){
								result = y+2;
								break;
						}
					}
				}
				
				
			}
			
		return result;
	}*/

	/**
	 * @returns 
	 * @throws Error if exists any lexical invalid
	 */
	static private int getEndOfLexem(String s) throws Error {
		if (s.startsWith("::")
			|| s.startsWith("~-")
			|| s.startsWith("->")
			|| s.startsWith("+.")
			|| s.startsWith("-.")
			|| s.startsWith("*.")
			|| s.startsWith("/.")
			|| s.startsWith("<=")
			|| s.startsWith(">=")
			|| s.startsWith("<>")
			|| s.startsWith("||")
			|| s.startsWith("&&")
			|| s.startsWith("==")) return 1;
		
		if (s.startsWith("=")
			|| s.startsWith("|")
			|| s.startsWith("+")
			|| s.startsWith("@")
			|| s.startsWith("-")
			|| s.startsWith("*")
			|| s.startsWith("/")
			|| s.startsWith("^")
			|| s.startsWith("%")
			|| s.startsWith("!")
			|| s.startsWith("(")
			|| s.startsWith(")")
			|| s.startsWith(",")
			//|| s.startsWith("[")
			//|| s.startsWith("]")
			|| s.startsWith(";")
			|| s.startsWith(":")
			|| s.startsWith("&")
			|| s.startsWith("<")
			|| s.startsWith(">")) return 0;
		
		if (s.startsWith("[")) { // Reconhece lista
			for (int a=1; a<s.length();a++) {
				if (s.charAt(a)==']') return a;
			}
			throw new Error(8);
		}
		
		else if (s.startsWith("'")) { // Reconhecer char
			Error lerror;
			if (s.startsWith("''") && !s.startsWith("'''")) return 1; // Char vazio
			else if (s.startsWith("'\\")){
				if ((s.charAt(3)=='\'') && (s.length()==4)) return 3;
				else {
					lerror = new Error(3);
					lerror.setExtra(" after "+ s);
					throw lerror;
				}
			}
			else if (s.charAt(2)!='\'') { 
				lerror = new Error(3);
				lerror.setExtra(" after "+ s.charAt(0) + s.charAt(1));
				throw lerror;
			}
			return 2; // Qualquer outro char vai até a posição 2
		}
		//if (s.startsWith("list of")) return 6;
		if (s.startsWith("\"")) { // Reconhecer strings...
			// Parar quando axar outro \"
			int a;
			for (a=1; a<s.length(); a++) { // Mudar esse for
				if (a<s.length()-1 && s.charAt(a)=='\\' && s.charAt(a+1)=='\"') a++;
				else if (s.charAt(a)=='\"') break;
			}
			if (a==s.length()) throw new Error(4);
			return a;
		}
		if (Character.isLetter(s.charAt(0)) || s.charAt(0)=='_') {
			// Procurar um não letra, não digito e não _
			for (int a=0;a<s.length();a++) {
				if (!(Character.isLetter(s.charAt(a)) || Character.isDigit(s.charAt(a)) || s.charAt(a)=='_')) {
					return a-1;
				}
			}
		} 
		if (Character.isDigit(s.charAt(0))) {
			// Procurar terminadores de digitos: " ", ")", e operadores
			for (int a=1; a<s.length(); a++) {
				//if (!(Character.isDigit(s.charAt(a)) || s.charAt(a)=='.')) return a-1;
				if ((s.charAt(a)==' ') ||
						(s.charAt(a)==')') ||
						(s.charAt(a)=='+') ||
						(s.charAt(a)=='-') ||
						(s.charAt(a)=='/') ||
						(s.charAt(a)=='^') ||
						(s.charAt(a)=='%') ||
						(s.charAt(a)==',') ||
						(s.charAt(a)==':')) return a-1; 
			}
		}
		if (s.startsWith(".")) return 0;
		return s.length()-1; // No pior dos casos o resto da string vai ser uma palavra só
	}
	
	// Remove as linhas em branco no começo do coisa
	static private String removeBlankSpaces(String s) {
		if (s.isEmpty()) return s;
		while (s.startsWith(" ") || s.startsWith("\t")) s = s.substring(1,s.length());
		return s;
	}
	
	static public LinkedList<Lexem> parseFile(String fileName) throws Error {
		if (s==null) s = new Table();
		
		LinkedList<Lexem> l = new LinkedList<Lexem>();
		
		// Ler o arquivo e identificar os lexemas
		FileReader r;
		try {
			r = new FileReader(new File(fileName));
		} catch (FileNotFoundException e) {
			throw new Error(0,0);
		}
		BufferedReader b = new BufferedReader(r);
		String currentLine;
		
		int lineId = 0; // Numero da linha
		int index;
		int bracketCounter=0;
		int andNumber=0;
		
		try {
			while ((currentLine = b.readLine()) != null) {
				lineId++; // Contador do numero da linha
				while (!currentLine.isEmpty()) { // Percorrer todas as linha, ignorando linhas em branco
					currentLine = removeBlankSpaces(currentLine);
					if (currentLine.isEmpty()) break;
					if (currentLine.startsWith("//")) continue; // Ignora comentários
					
					index = -1;
					try {
						index = getEndOfLexem(currentLine)+1;
					} catch (Error le) {
						le.setLine(lineId);
						//JOptionPane.showMessageDialog(null, le.getMessage());
						//return null;
						throw le;
					}
					String nome = currentLine.substring(0,index);
					currentLine = currentLine.substring(nome.length(),currentLine.length());

					//System.out.println(nome + currentLine.isEmpty());
					/*
							String nome = currentLine.substring(0, getFirstSpace(currentLine));
							currentLine.replaceFirst(nome, "");
						*/	
					Lexem lexem = new Lexem(nome);
					lexem.evalue();
					// Contagem de parenteses
					if (lexem.getId()==LexemId.BRACKET_OPEN) bracketCounter++;
					else if (lexem.getId()==LexemId.BRACKET_CLOSE) bracketCounter--;
					lexem.setLine(lineId);
					// Arrumar tabela de simbolos
					if (lexem.getId()==LexemId.NAME) {
						if (l.size()>0) {
							if (l.get(l.size()-1).getId()==LexemId.KEYWORD_LET) {
								s.insertElement(lexem.getLex());
							}
							else if (l.get(l.size()-1).getId()==LexemId.KEYWORD_AND) {
								s.insertElement(lexem.getLex());
								andNumber++;
							}
							else if (l.get(l.size()-1).getId()==LexemId.KEYWORD_REC) {
								if (l.size()>1 && l.get(l.size()-2).getId()==LexemId.KEYWORD_LET) {
									s.insertElement(lexem.getLex());
								}
								else {
									Error lerror = new Error(7);
									lerror.setLine(lineId);
									throw lerror;
								}
							}
						}
					}
					else if (lexem.getId()==LexemId.KEYWORD_IN) {
						while (andNumber>0) { // Remover as multiplas definições feitas pelo and
							s.removeLast();
							andNumber--;
						}
						s.removeLast();
					}
					
					// Atrelar os tipos
					if (lexem.getId()==LexemId.TYPE_INT ||
							lexem.getId()==LexemId.TYPE_FLOAT ||
							lexem.getId()==LexemId.TYPE_STRING ||
							lexem.getId()==LexemId.TYPE_CHAR ||
							lexem.getId()==LexemId.TYPE_BOOL) {
						// TODO fazer isso aque
					}
					
					
					// Adicionar lá
					l.add(lexem);
				}
			}
			if (bracketCounter!=0) {
				Error lerror = new Error(6);
				if (bracketCounter>0) lerror.setId(5);
				lerror.setLine(lineId);
				throw lerror;
			}
			
			return l;
		} catch (IOException e) {
			throw new Error(1,0);
		}
	}
	
}
