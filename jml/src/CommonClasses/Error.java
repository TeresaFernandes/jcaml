package CommonClasses;

@SuppressWarnings("serial")
public class Error extends Exception {
	private int id;
	private int lineId;
	private String extraMsg;
	
	public Error(int id, int lineId) {
		this.id = id;
		this.lineId = lineId;
	}
	public Error(int id) {
		this.id = id;
	}
	
	public void setLine(int line) {
		this.lineId = line;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setExtra(String s) {
		this.extraMsg = s;
	}
	
	public String getMessage() {
		String message;

		switch (id) {
			case 0: message = "File not Found"; break;
			case 1: message = "File Reading Error"; break;
			case 2: message = "Invalid Name"; break;
			case 3: message = "Invalid Char: expected '"; break;
			case 4: message = "Invalid String: expected \" "; break;
			case 5: message = "Missing \')\'"; break;
			case 6: message = "Unexpected \')\' at end of file"; break;
			case 7: message = "Invalid use of 'rec' token"; break;
			case 8: message = "Endless list"; break;
			case 9: message = "] without a ["; break;
			case 10: message = "Invalid Expression"; break;
			case 11: message = "Undefined variable"; break;
			case 12: message = "Invalid list value"; break;
			case 13: message = "Invalid program"; break;
			case 14: message = "Invalid function use"; break;
			case 15: message = "Expected Boolean Expression"; break;
			case 16: message = "Invalid function parameters"; break;
			case 17: message = "Invalid parameter value"; break;
			case 18: message = "Unknown operator"; break;
			case 19: message = "Sintax Error"; break;
			case 20: message = "Invalid use of operator"; break;
			case 21: message = "Expected primary expression"; break;
			case 22: message = "Unexpected operator"; break;
			case 23: message = "Variable "; break;
			case 24: message = "Function "; break;
			case 25: message = "Incompatiple types"; break;
			case 26: message = "Variable of type "; break;
				//...
			default: message = "Unknown error";
		}
		
		return  "[Line " + lineId + "] " + message + (extraMsg==null?"" : extraMsg);
	}
}
