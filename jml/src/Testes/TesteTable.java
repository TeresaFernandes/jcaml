package Testes;

import CommonClasses.Error;
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
	}
	
}
