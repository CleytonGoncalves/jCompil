package model;

import java.util.Collections;
import java.util.Stack;

public class ExpressionStack extends Stack<Symbol> {
	
	public boolean evalExprWithAssignment() {
		Collections.reverse(this);
		//Pega a variável do lado esquerdo
		Symbol var = this.pop();
		String type = var.getType();
		//Joga fora o simbolo de atribuição
		this.pop();
		boolean eval = evalExprWithType(type);
		this.push(var);
		return eval;
	}
	
	public boolean evalExpr() {
		//Pega a variável do lado esquerdo
		Symbol var = this.peek();
		//Retira parenteses ou -/+
		if (var.getToken().getType() == TokenType.SYMBOL) {
			for (Symbol s : this) {
				if (s.getToken().getType() != TokenType.SYMBOL) {
					var = s;
					break;
				}
			}
		}
		String type = var.getType();
		Collections.reverse(this);
		boolean eval = evalExprWithType(type);
		this.push(var);
		return eval;
	}
	
	private boolean evalExprWithType(String type) {
		boolean differentTypes = false;
		while (!this.isEmpty()) {
			Symbol s = this.pop();
			if (s.getSymbolCategory() == null && s.getToken().getType() != TokenType.SYMBOL) {
				//é um numero
				if (!s.getType().contentEquals(type)) {
					differentTypes = true;
					break;
				}
			} else if (s.getSymbolCategory() == SymbolCategory.VARIABLE || s.getSymbolCategory() == SymbolCategory.ARGUMENT) {
				//Verificar se tem tipos iguais ao da variável
				if (!s.getType().contentEquals(type)) {
					differentTypes = true;
					break;
				}
			}
		}
		return !differentTypes;
	}
}
