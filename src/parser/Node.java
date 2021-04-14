package parser;

import variables.VariableContext;

public class Node {
	
	public String typeName = null;
	public String type() {
		if (typeName != null) {
			return typeName;
		}
		return this.getClass().toString();
	}
	
	public int col;
	public int line;
	
	public Node(int col, int line) {
		this.col = col;
		this.line = line;
	}

	public Object evaluate(VariableContext context) {
		if (this instanceof OpNode) {
			return ((OpNode) this).OpEvaluate(context);
		}
		if (this instanceof UnaryOpNode) {
			return ((UnaryOpNode) this).OpEvaluate(context);
		}
		if (this instanceof ValueNode) {
			return ((ValueNode) this).getValue();
		}
		
		return null;
	}
	
}
