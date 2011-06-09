package Testes;

import java.util.LinkedList;
import java.util.List;

import symbolTable.Table;
import symbolTable.Variable;
import interpreter.ExpressionEvaluator;
import CommonClasses.Error;
import CommonClasses.Lexem;
import CommonClasses.SintaxElement;
import CommonClasses.SintaxElementId;

public class TesteSintax {
	public static void main(String[] arg) {
		Lexem l = new Lexem("[2,7,'o']");
		try {
			l.evalue();
		} catch (Error e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SintaxElement s = new SintaxElement(l);
		List<SintaxElement> list = new LinkedList<SintaxElement>();
		list.add(s);
		SintaxElement e = new SintaxElement(SintaxElementId.E, list);
		//System.out.println(s.getId());
		
		Variable t = null;
		try {
			System.out.println("Começando a treta");
			t = ExpressionEvaluator.evalue(new Table(), e);
			System.out.println(t.getType() + " " + t.getValue());
		} catch (Error h) {
			// TODO Auto-generated catch block
			h.printStackTrace();
		}
		
		
	}
}
