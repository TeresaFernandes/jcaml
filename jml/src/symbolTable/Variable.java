package symbolTable;

import java.util.LinkedList;
import java.util.List;

public class Variable {
	private String name;
	private VarType type;
	private List aux;
	private String realType;
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
	
	public String getRealType() {
		return realType;
	}
	
	public void setRealType(String r) {
		this.realType = r;
	}
	
	public Object getValue() {
		return value;
	}
	
	public void setValue(Object value) {
		this.value = value;
	}
	
	public String toString() {
		return name+" ("+ (realType!=null? realType: type) +") value: "+value;
	}
	
	public Variable clone() {
		Variable v = new Variable(this.name);
		v.setAux(this.aux); 
		// Arrumar isso
		v.setValue(this.value);
		if (this.value!=null) {
			if (this.value.getClass().toString().compareTo("class java.lang.String")==0) {
				v.setValue("" + this.value);
				//System.out.println("weee");
			}
			else {
				v.setValue(this.value);
			}
		}
		v.setType(this.type);
		return v;
	}
}
