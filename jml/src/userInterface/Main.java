package userInterface;

//import java.io.BufferedWriter;
//import java.io.FileWriter;
//import java.io.IOException;
import java.util.LinkedList;

import CommonClasses.Lexem;
import CommonClasses.LexicalError;

import symbolTable.Table;

//import javax.swing.JOptionPane;

import lexicalAnalyzer.Analyzer;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String arquivo = "teste.txt";
		
		LinkedList<Lexem> l;
		try {
			l = Analyzer.parseFile(arquivo);
			
			if (l==null) return;
			for (int a=0; a<l.size(); a++) {
				Lexem x = l.get(a);
				System.out.println(x.getLex() + "\t" + x.getId());
			}
		} catch (LexicalError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("\n\tSymbol Table:");
		Table s = Analyzer.s;
		for (int a=0;a<s.table.size();a++) {
			System.out.println(s.table.get(a).getName()+ "\t" + s.table.get(a).getType());
		}
	}

}
