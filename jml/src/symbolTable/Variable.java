package symbolTable;

import java.util.LinkedList;
import java.util.List;

public class Variable {
	private String name;
	private VarType type;
	private List aux;
	private int adress;
	//private String value;
	private List value;
	
	public Variable(String name) {
		this.name = name;
		type = VarType.UNKNOWN;
		value = new LinkedList();
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
		if (this.type!=VarType.FUNCTION_TYPE) return (this.value.isEmpty()? null : this.value.get(0));
		else return value;
	}
	
	public void setValue(Object value) {
		// TODO cuidado aqui

		// Se não for uma função, seu valor pode variar de tipo, mas têm apenas 1 elemento
		if (this.type!=VarType.FUNCTION_TYPE) {
			if (this.value.isEmpty()) this.value.add(value);
			else {
				this.value.clear();
				this.value.add(value);
			}
		}
		else { // Se for uma função, seu valor É uma lista
			this.value = (List) value;
		}
	}
}
