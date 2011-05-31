package symbolTable;

import java.util.LinkedList;

import CommonClasses.Error;


public class Table {
	public LinkedList<Variable> table; // TODO colocar private depois dos testes
	
	public Table() {
		table = new LinkedList<Variable>();
	}
	
	/**
	 * Insert name in symbol table
	 * @param name Variable's name
	 */
	public void insertElement(String name) throws Error {
		Variable v = new Variable(name);
		table.add(v);
	}
	
	public void insert(Variable v) {
		table.add(v);
	}
	
	/**
	 * Get value from symbol table
	 * @param name Variable's name
	 * @return Object
	 */
	public Variable getElement(String name) throws Error {
		// TODO tudo
		return null; 
	}
	
	/**
	 * Update an object
	 * @param updatedObject Updated object
	 */
	public void updateObject(Object updatedObject) throws Error {
		// TODO tudo
	}
	
	public void removeLast() {
		// TODO verificar se precisa
		table.removeLast();
	}
	
	public boolean contains(String name) {
		// TODO melhorar (ou não)
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
			c.insert(v);
		}
		
		return c;
		
	}
}
