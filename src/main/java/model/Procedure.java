package model;

import java.util.ArrayList;

public class Procedure extends Symbol {
	
	private ArrayList<Symbol> arguments = new ArrayList<>();
	
	Procedure(Symbol s) {
		super(s.getContent(), s.getSymbolCategory(), s.getToken(), s.getType());
	}
	
	public int getAmountArguments() {
		return this.arguments.size();
	}
	
	public ArrayList<Symbol> getArguments() {
		return arguments;
	}
	
	public void setArguments(ArrayList<Symbol> arguments) {
		this.arguments = arguments;
	}
}
