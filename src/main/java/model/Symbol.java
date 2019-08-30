package model;

public class Symbol {
	
	private String content;
	private SymbolCategory symbolCategory;
	private Token token;
	private String type;
	
	public Symbol(String content, SymbolCategory symbolCategory, Token token, String type) {
		super();
		this.content = content;
		this.symbolCategory = symbolCategory;
		this.token = token;
		this.type = type;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public SymbolCategory getSymbolCategory() {
		return symbolCategory;
	}
	
	public void setSymbolCategory(SymbolCategory symbolCategory) {
		this.symbolCategory = symbolCategory;
	}
	
	public Token getToken() {
		return token;
	}
	
	public void setToken(Token token) {
		this.token = token;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	@Override
	public String toString() {
		return getContent();
	}
}
