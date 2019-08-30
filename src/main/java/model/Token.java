package model;

public class Token {
	private String content;
	private TokenType type;
	private int line;
	private int column;
	
	public Token(String content, TokenType type, int line, int column) {
		super();
		this.content = content;
		this.type = type;
		this.line = line;
		this.column = column - content.length();
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public TokenType getType() {
		return type;
	}
	
	public void setType(TokenType type) {
		this.type = type;
	}
	
	public int getLine() {
		return line;
	}
	
	public void setLine(int line) {
		this.line = line;
	}
	
	public int getColumn() {
		return column;
	}
	
	public void setColumn(int column) {
		this.column = column;
	}
	
	@Override
	public String toString() {
		return this.content + " - " + this.type + " - (" + line + ":" + column + ")";
	}
}
