package Testes;

import java.util.LinkedList;
import java.util.List;

import symbolTable.Table;
import symbolTable.VarType;
import symbolTable.Variable;
import interpreter.ExpressionEvaluator;
import CommonClasses.Error;
import CommonClasses.Lexem;
import CommonClasses.SintaxElement;
import CommonClasses.SintaxElementId;

public class TesteSintax {
	public static void main(String[] arg) {

		// Teste de interpretação do tipo CONST	
		Lexem l = new Lexem("[2,7,2]");
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
		
		// Teste de interpretação do tipo ID		
		Variable v = new Variable("x");
		v.setType(VarType.INT_TYPE);
		v.setValue("25.1");
		Table scope = new Table();
		scope.insert(v);
		
		Lexem var = new Lexem("x");
		try {
			var.evalue();
			SintaxElement se = new SintaxElement(var);
			List<SintaxElement> lol = new LinkedList<SintaxElement>();
			lol.add(se);
			SintaxElement see = new SintaxElement(SintaxElementId.E, lol);
			
			Variable x = ExpressionEvaluator.evalue(scope,see);
			System.out.println(x.getValue());
			System.out.println(x.getType());
			
		} catch(Error eee){
			eee.printStackTrace();
		}
	}
}
