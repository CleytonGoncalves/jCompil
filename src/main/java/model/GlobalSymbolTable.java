package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GlobalSymbolTable extends SymbolTable {
	
	private Map<String, SymbolTable> procedures;
	
	public GlobalSymbolTable() {
		super("Global");
		procedures = new HashMap<>();
	}
	
	public void newProcedure(Token t) {
		insert(t.getContent(), t, SymbolCategory.PROCEDURE);
		procedures.put(t.getContent(), new SymbolTable(t.getContent()));
	}
	
	public SymbolTable getSymbolTable(String ident) {
		return procedures.get(ident);
	}
	
	public ArrayList<SymbolTable> getSymbolTable() {
		return new ArrayList<>(procedures.values());
	}
}
