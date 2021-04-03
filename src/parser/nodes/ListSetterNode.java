package parser.nodes;

import main.EntryPoint;
import parser.Node;
import parser.operators.ListOperator;
import variables.VariableContext;

public class ListSetterNode extends Node {

	private Node left;
	private Node index;
	public Node expr;
	
	public ListSetterNode(int col, int line, Node left, Node index) {
		super(col, line);
		this.left = left;
		this.index = index;
	}

	public Object evaluate(VariableContext context) {
		
		Object index = this.index.evaluate(context);
		Object evaluated = this.left.evaluate(context);
		
		if ( expr == null ) {
			
			if (evaluated instanceof ListOperator) {
				ListOperator op = (ListOperator)evaluated;
				if(index instanceof NumberNode) {
					if(((NumberNode) index).getValue() instanceof Integer &&
							(Integer)((NumberNode) index).getValue() >= 0 && (Integer)((NumberNode) index).getValue() < op.length()) {
						return op.get((NumberNode) index);
					} else {
						if(!(((NumberNode) index).getValue() instanceof Integer)) {
							System.out.println("Integer Object needed, received Float/Double");
							EntryPoint.raiseNode(this);
							return null;
						}
						System.out.println("Index out of range exception");
						EntryPoint.raiseNode(this);
						return null;
					}
				}else if (index instanceof StringNode) {
					return op.get((StringNode) index);
				}
				System.out.println("List only support usage with numbers or strings");
				EntryPoint.raiseNode(this.left);
			}
		} else {
			Object expr = this.expr.evaluate(context);
			if (evaluated instanceof ListOperator) {
				ListOperator op = (ListOperator)evaluated;
				if(index instanceof NumberNode) {
					if(((NumberNode) index).getValue() instanceof Integer &&
							(Integer)((NumberNode) index).getValue() >= 0 && (Integer)((NumberNode) index).getValue() < op.length()) {
						op.set((NumberNode) index, expr);
						return expr;
					} else {
						if(!(((NumberNode) index).getValue() instanceof Integer)) {
							System.out.println("Integer Object needed, received Float/Double");
							EntryPoint.raiseNode(this);
							return null;
						}
						System.out.println("Index out of range exception");
						EntryPoint.raiseNode(this);
						return null;
					}
				}else if (index instanceof StringNode) {
					op.set((StringNode) index, expr);
					return expr;
				}
				System.out.println("List only support usage with numbers or strings");
				EntryPoint.raiseNode(this.left);
			}
		}
		
		return null;
		
	}
	
}