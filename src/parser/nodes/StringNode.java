package parser.nodes;

import main.EntryPoint;
import parser.Node;
import parser.operators.EvaluateOperator;
import parser.operators.ListOperator;
import variables.VariableContext;

public class StringNode extends Node implements EvaluateOperator,ListOperator {

	public boolean equals(Object o) {
		if (o instanceof StringNode) {
			return ((StringNode)o).getValue().equals(this.getValue());
		}
		return false;
	}
	
	public StringNode(int col, int line, String string) {
		super(col, line);
		this.value = string;
	}
	private String value;

	public Object evaluate(VariableContext context) {
		return this;
	}

	public String getValue() {
		return this.value;
	}

	public String toString() {
		return '\''+this.value+'\'';
	}
	
	@Override
	public Object add(Object e) {
		if (e instanceof StringNode) {
			StringNode n = (StringNode) e;
			return new StringNode(n.col, n.line, this.getValue() + n.getValue());
		}
		if (e instanceof NumberNode) {
			NumberNode n = (NumberNode) e;
			return new StringNode(n.col, n.line, this.getValue() + n.getValue());
		}

		if (e instanceof CharNode) {
			return new StringNode(this.col, this.line, this.getValue() + ((CharNode)e).getValue());
		}
		return null;
	}

	@Override
	public Object substract(Object e) {return null;}

	@Override
	public Object multiply(Object e) {return null;}

	@Override
	public Object divide(Object e) {return null;}

	@Override
	public Object power(Object e) {return null;}

	@Override
	public boolean set(NumberNode n, Object o) {return false;}

	@Override
	public boolean set(StringNode n, Object o) {return false;}

	@Override
	public Object get(NumberNode n) {
		if (n.getValue() instanceof Integer) {
			if (this.value.length() < (int) n.getValue() || (int) n.getValue() < 0) {
				System.out.println("Index out of range exception");
				EntryPoint.raiseNode(n);
			}
			return new CharNode(this.col, this.line, this.value.charAt((int) n.getValue()));
		} else {
			System.out.println("Integer Object needed, received Float/Double");
			EntryPoint.raiseNode(n);
		}
		return null;
	}

	@Override
	public Object get(StringNode n) {return null;}

	@Override
	public int length() {
		return this.value.length();
	}
	
}