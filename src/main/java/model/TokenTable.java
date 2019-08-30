package model;

import java.util.ArrayList;
import java.util.List;

public class TokenTable {
	
	private List<Token> tokens = new ArrayList<>();
	
	public void addToken(Token token) {
		this.tokens.add(token);
	}
	
	public List<Token> getTokens() {
		return tokens;
	}
	
	public void setTokens(List<Token> tokens) {
		this.tokens = tokens;
	}
}
