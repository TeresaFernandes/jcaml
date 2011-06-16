package symbolTable;

import java.util.LinkedList;

import CommonClasses.Error;


public class Table {
	public LinkedList<Variable> table; // TODO colocar private depois dos testes
	
	public Table() {
		table = new LinkedList<Variable>();
		
		Variable v1;
		
		v1 = new Variable("createList"); v1.setType(VarType.DEFAULTFUNCTION_TYPE); v1.setValue("funcao"); table.add(v1);
		v1 = new Variable("removeFirst"); v1.setType(VarType.DEFAULTFUNCTION_TYPE); v1.setValue("funcao"); table.add(v1);
		v1 = new Variable("set"); v1.setType(VarType.DEFAULTFUNCTION_TYPE); v1.setValue("funcao"); table.add(v1);
		v1 = new Variable("get"); v1.setType(VarType.DEFAULTFUNCTION_TYPE); v1.setValue("funcao"); table.add(v1);
		v1 = new Variable("abs"); v1.setType(VarType.DEFAULTFUNCTION_TYPE); v1.setValue("funcao"); table.add(v1);
		v1 = new Variable("ceil"); v1.setType(VarType.DEFAULTFUNCTION_TYPE); v1.setValue("funcao"); table.add(v1);
		v1 = new Variable("floor"); v1.setType(VarType.DEFAULTFUNCTION_TYPE); v1.setValue("funcao"); table.add(v1);
		v1 = new Variable("sqrt"); v1.setType(VarType.DEFAULTFUNCTION_TYPE); v1.setValue("funcao"); table.add(v1);
		v1 = new Variable("length"); v1.setType(VarType.DEFAULTFUNCTION_TYPE); v1.setValue("funcao"); table.add(v1);
		v1 = new Variable("uppercase"); v1.setType(VarType.DEFAULTFUNCTION_TYPE); v1.setValue("funcao"); table.add(v1);
		v1 = new Variable("lowercase"); v1.setType(VarType.DEFAULTFUNCTION_TYPE); v1.setValue("funcao"); table.add(v1);
		v1 = new Variable("int_of_char"); v1.setType(VarType.DEFAULTFUNCTION_TYPE); v1.setValue("funcao"); table.add(v1);
		v1 = new Variable("int_of_string"); v1.setType(VarType.DEFAULTFUNCTION_TYPE); v1.setValue("funcao"); table.add(v1);
		v1 = new Variable("int_of_float"); v1.setType(VarType.DEFAULTFUNCTION_TYPE); v1.setValue("funcao"); table.add(v1);
		v1 = new Variable("char_of_int"); v1.setType(VarType.DEFAULTFUNCTION_TYPE); v1.setValue("funcao"); table.add(v1);
		v1 = new Variable("float_of_int"); v1.setType(VarType.DEFAULTFUNCTION_TYPE); v1.setValue("funcao"); table.add(v1);
		v1 = new Variable("float_of_string"); v1.setType(VarType.DEFAULTFUNCTION_TYPE); v1.setValue("funcao"); table.add(v1);
		v1 = new Variable("string_of_bool"); v1.setType(VarType.DEFAULTFUNCTION_TYPE); v1.setValue("funcao"); table.add(v1);
		v1 = new Variable("string_of_char"); v1.setType(VarType.DEFAULTFUNCTION_TYPE); v1.setValue("funcao"); table.add(v1);
		v1 = new Variable("string_of_int"); v1.setType(VarType.DEFAULTFUNCTION_TYPE); v1.setValue("funcao"); table.add(v1);
		v1 = new Variable("string_of_float"); v1.setType(VarType.DEFAULTFUNCTION_TYPE); v1.setValue("funcao"); table.add(v1);
		v1 = new Variable("string_of_list"); v1.setType(VarType.DEFAULTFUNCTION_TYPE); v1.setValue("funcao"); table.add(v1);
		v1 = new Variable("read_int"); v1.setType(VarType.DEFAULTFUNCTION_TYPE); v1.setValue("funcao"); table.add(v1);
		v1 = new Variable("read_float"); v1.setType(VarType.DEFAULTFUNCTION_TYPE); v1.setValue("funcao"); table.add(v1);
		v1 = new Variable("read_string"); v1.setType(VarType.DEFAULTFUNCTION_TYPE); v1.setValue("funcao"); table.add(v1);
		v1 = new Variable("read_char"); v1.setType(VarType.DEFAULTFUNCTION_TYPE); v1.setValue("funcao"); table.add(v1);
		v1 = new Variable("print"); v1.setType(VarType.DEFAULTFUNCTION_TYPE); v1.setValue("funcao"); table.add(v1);
	}
	
	/**
	 * Insert name in symbol table
	 * @param name Variable's name
	 */
	public void insertElement(String name) throws Error {
		Variable v = new Variable(name);
		try {
			getElement(name);
		} catch (Error e){
			table.add(v);
		}
	}
	
	public void insert(Variable v) {
		if (v.getName()!="") {
			try {
				Variable x = getElement(v.getName());
				x.setType(v.getType());
				x.setValue(v.getValue());
				x.setAux(v.getAux());
				//System.out.println("Sobrescrito "+x.getName());
			} catch (Error e) { // Se falhar, é pq não existe
				table.add(v);	
			}
		}
	}
	
	/**
	 * Get value from symbol table
	 * @param name Variable's name
	 * @return Object
	 */
	public Variable getElement(String name) throws Error {
		Variable v;
		for (int index=0; index<table.size(); index++) {
			v = table.get(index);
			if (v.getName().compareTo(name)==0) return v;
		}
		Error r = new Error(11);
		r.setExtra(" " + name);
		throw r; 
	}
	
	/**
	 * Update an object
	 * @param updatedObject Updated object
	 */
	// Método totalmente desnecessário
	public void updateObject(Variable updatedObject) throws Error {
		
	}
	
	public void removeLast() {
		// TODO verificar se precisa
		table.removeLast();
	}
	
	public boolean contains(String name) {
		for (int a=0;a<table.size();a++) {
			if (table.get(a).getName().compareTo(name)==0) return true;
		}
		return false;
	}
	
	public Table clone() {
		Table c = new Table();
		Variable v;
		for (int a=0; a<table.size(); a++) {
			v = table.get(a);
			//if (v.getType()!=VarType.LIST_TYPE)	c.insert(v.clone());
			//else c.insert(v);
			c.insert(v.clone());
		}
		
		return c;
		
	}
}
