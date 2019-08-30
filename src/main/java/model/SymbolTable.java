package model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
	
	private Map<String, Symbol> symbols = new HashMap<>();
	private String identifier;
	private int currAddr = 0;
	
	SymbolTable(String identifier) {
		this.setIdentifier(identifier);
	}
	
	public void insert(Symbol s) {
		if (s.getSymbolCategory() != SymbolCategory.PROCEDURE) {
			symbols.put(s.getContent(), s);
		} else {
			symbols.put(s.getContent(), new Procedure(s));
		}
	}
	
	void insert(String ident, Token t, SymbolCategory c, String type) {
		insert(new Symbol(ident, c, t, type));
	}
	
	public void insert(String ident, Token t, SymbolCategory c) {
		insert(new Symbol(ident, c, t, null));
	}
	
	public Symbol find(String ident) {
		return symbols.get(ident);
	}
	
	public Collection<Symbol> getSymbols() {
		return symbols.values();
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
	public int getCurrAddr() {
		return currAddr;
	}
	
	public void setCurrAddr(int currAddr) {
		this.currAddr = currAddr;
	}
	
	public void addCurrAddr() {
		this.currAddr++;
	}
}
