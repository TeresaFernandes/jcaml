package symbolTable;

import java.util.LinkedList;
import java.util.List;

public class Variable {
	private String name;
	private VarType type;
	private List aux;
	private int adress;
	//private String value;
	private Object value;
	
	public Variable(String name) {
		this.name = name;
		type = VarType.UNKNOWN;
	}
	
	public String getName() {
		return name;
	}

	public VarType getType() {
		return type;
	}
	
	public void setType(VarType type) {
		this.type = type;
	}
	
	public List getAux() {
		return aux;
	}
	
	public void setAux(List aux) {
		this.aux = aux;
	}
	
	public int getAdress() {
		return adress;
	}
	
	public void setAdress(int adress) {
		this.adress = adress;
	}
	
	public Object getValue() {
		return value;
	}
	
	public void setValue(Object value) {
		this.value = value;
	}
	
	public String toString() {
		return name+" ("+type+") value: "+value;
	}
}
