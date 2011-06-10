package Testes;

import java.util.LinkedList;
import java.util.List;

import lexicalAnalyzer.Analyzer;

import interpreter.ExpressionEvaluator;
import CommonClasses.Error;
import CommonClasses.Lexem;
import symbolTable.Table;
import symbolTable.VarType;
import symbolTable.Variable;

public class TesteTable {
	
	public static void main(String[] args) {
		Table t = new Table();
		
		Variable testVar = new Variable("Teresa");
		Variable testVar2 = new Variable("Teresa2");
		
		t.insert(testVar);
		t.insert(testVar2);
		
		try {
			Variable tzin;
			tzin = t.getElement("Teresa2");
			tzin.setType(VarType.INT_TYPE);
			tzin.setValue(new Integer(3));
			//System.out.println(tzin.getName() + " " + tzin.getValue() + " " + tzin.getType());
		} catch (Error e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		Variable v;
		for (int a=0; a < t.table.size(); a++) {
			v = t.table.get(a);
			System.out.println(v.getName() + " " + v.getValue() + " " + v.getType());
		}
		
		// coloque aqui o coisa que vc quer testar
		String list = "[2.5, 12., 12.]";
		try {
			Lexem lex = new Lexem(list);
			lex.evalue();
			System.out.println(lex.getId());
			System.out.println(ExpressionEvaluator.valueFromLexem(lex));
		} catch (Error e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		String program = "function (x:int) -> x*2";
	}
	
}
