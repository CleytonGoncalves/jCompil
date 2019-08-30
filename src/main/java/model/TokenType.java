package model;

public enum TokenType {
	SYMBOL, IDENTIFIER, KEYWORD, LITERAL_INTEGER, LITERAL_REAL;
	
	public String getType() {
		switch (this) {
			case LITERAL_INTEGER:
				return "integer";
			case LITERAL_REAL:
				return "real";
			case SYMBOL:
				return "symbol";
		}
		return super.toString();
	}
}
