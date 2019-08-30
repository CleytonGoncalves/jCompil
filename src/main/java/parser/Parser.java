package parser;

import model.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("SpellCheckingInspection")
public class Parser {
	
	private GlobalSymbolTable globalSymbolTable = new GlobalSymbolTable();
	private TokenTable tokenTable;
	private Token currToken;
	private SymbolCategory currCategory;
	private Iterator<Token> it;
	public SymbolTable symbolTable;
	private ExpressionStack expressionStack = new ExpressionStack();
	private boolean declarationMode = false;
	private List<Symbol> currVarList = new ArrayList<>();
	
	public Parser(TokenTable tokenTable) {
		this.tokenTable = tokenTable;
	}
	
	public void analyze() throws ParserException {
		it = tokenTable.getTokens().iterator();
		currToken = it.next();
		symbolTable = globalSymbolTable;
		programa();
	}
	
	private void programa() throws ParserException {
		if (currToken.getContent().contentEquals("program")) {
			proximoToken();
			if (currToken.getType() == TokenType.IDENTIFIER) {
				addProgramIdentifier();
				proximoToken();
				corpo();
				if (currToken.getContent().contentEquals(".")) {
					if (it.hasNext()) {
						//Erro
						proximoToken();
						throw new ParserException("Char found after program end", currToken);
					}
				} else {
					//Erro
					throw new ParserException("Program must end with '.'", currToken);
				}
			} else {
				//Erro
				throw new ParserException("Invalid identifier for program", currToken);
			}
		} else {
			//Erro
			throw new ParserException("Program must start with 'program'", currToken);
		}
	}
	
	private void corpo() throws ParserException {
		dc();
		if (currToken.getContent().contentEquals("begin")) {
			declarationMode = false;
			proximoToken();
			comandos();
			if (currToken.getContent().contentEquals("end")) {
				proximoToken();
			} else {
				//Erro
				throw new ParserException("'end' not found", currToken);
			}
		} else {
			//Erro
			if (currToken.getContent().contentEquals("var") || currToken.getContent().contentEquals("procedure")) {
				throw new ParserException("To declare more variables or procedures, put ';' after the type", currToken);
			} else {
				if (currToken.getType() == TokenType.IDENTIFIER) {
					throw new ParserException("Expected 'var' or 'procedure' but found " + currToken.getContent(), currToken);
				} else {
					throw new ParserException("Expected 'begin' but found " + currToken.getContent(), currToken);
				}
			}
		}
	}
	
	private void dc() throws ParserException {
		if (currToken.getContent().contentEquals("var")) {
			startVarDeclaration();
			proximoToken();
			dc_v();
			mais_dc();
		} else {
			if (currToken.getContent().contentEquals("procedure")) {
				declarationMode = true;
				proximoToken();
				dc_p();
				mais_dc();
			}
		}
	}
	
	private void mais_dc() throws ParserException {
		if (currToken.getContent().contentEquals(";")) {
			proximoToken();
			dc();
		}
	}
	
	private void tipo_var() throws ParserException {
		if (currToken.getContent().contentEquals("integer") || currToken.getContent().contentEquals("real")) {
			addSymbolsWithType(currToken.getContent());
			proximoToken();
		} else {
			//Erro
			throw new ParserException("Variables must be integer or real", currToken);
		}
	}
	
	private void dc_v() throws ParserException {
		variaveis();
		if (currToken.getContent().contentEquals(":")) {
			proximoToken();
			tipo_var();
		} else {
			//Erro
			throw new ParserException("Incorrect variable declaration", currToken);
		}
	}
	
	private void variaveis() throws ParserException {
		if (currToken.getType() == TokenType.IDENTIFIER) {
			addVar();
			proximoToken();
			mais_var();
		} else {
			//Erro
			throw new ParserException("Invalid Identifier", currToken);
		}
	}
	
	private void mais_var() throws ParserException {
		if (currToken.getContent().contentEquals(",")) {
			proximoToken();
			variaveis();
		}
	}
	
	private void dc_p() throws ParserException {
		if (currToken.getType() == TokenType.IDENTIFIER) {
			startProcedure();
			proximoToken();
			parametros();
			corpo_p();
			finishProcedure();
		} else {
			//Erro
			throw new ParserException("Invalid identifier for procedure", currToken);
		}
	}
	
	private void parametros() throws ParserException {
		if (currToken.getContent().contentEquals("(")) {
			proximoToken();
			lista_par();
			if (currToken.getContent().contentEquals(")")) {
				proximoToken();
			} else {
				//Erro
				throw new ParserException("')' not found for procedure argument closing", currToken);
			}
		}
	}
	
	private void corpo_p() throws ParserException {
		dc_loc();
		if (currToken.getContent().contentEquals("begin")) {
			declarationMode = false;
			proximoToken();
			comandos();
			if (currToken.getContent().contentEquals("end")) {
				proximoToken();
			} else {
				//Erro
				throw new ParserException("Procedure must end with 'end'", currToken);
			}
		} else {
			//Erro
			throw new ParserException("Procedure must start with 'begin'", currToken);
		}
	}
	
	private void dc_loc() throws ParserException {
		if (currToken.getContent().contentEquals("var")) {
			startVarDeclaration();
			proximoToken();
			dc_v();
			mais_dcloc();
		}
	}
	
	private void mais_dcloc() throws ParserException {
		if (currToken.getContent().contentEquals(";")) {
			proximoToken();
			dc_loc();
		}
	}
	
	private void lista_par() throws ParserException {
		variaveis();
		if (currToken.getContent().contentEquals(":")) {
			proximoToken();
			tipo_var();
			mais_par();
		} else {
			//Erro
			throw new ParserException("Invalid argument declaration", currToken);
		}
	}
	
	private void mais_par() throws ParserException {
		if (currToken.getContent().contentEquals(";")) {
			proximoToken();
			lista_par();
		}
	}
	
	private void comandos() throws ParserException {
		comando();
		mais_comandos();
	}
	
	private void comando() throws ParserException {
		if (currToken.getType() == TokenType.IDENTIFIER) {
			findIfTokenExists(true);
			startExpressionStack();
			stackExpressionItem();
			proximoToken();
			resto_ident();
			currVarList.clear();
		} else {
			if (currToken.getContent().contentEquals("read") || currToken.getContent().contentEquals("write")) {
				proximoToken();
				if (currToken.getContent().contentEquals("(")) {
					proximoToken();
					variaveis();
					if (currToken.getContent().contentEquals(")")) {
						findIfTokenExists(false);
						proximoToken();
						currVarList.clear();
					} else {
						//Erro
						throw new ParserException("Expected ')' for procedure closing", currToken);
					}
				} else {
					//Erro
					throw new ParserException("Expected '(' after de " + currToken.getContent(), currToken);
				}
			} else {
				if (currToken.getContent().contentEquals("while")) {
					proximoToken();
					condicao();
					if (currToken.getContent().contentEquals("do")) {
						proximoToken();
						comandos();
						if (currToken.getContent().contentEquals("$")) {
							proximoToken();
						} else {
							//Erro finalizar com $ o while
							throw new ParserException("Expected '$' after while", currToken);
						}
					} else {
						//Erro sintaxe do while é 'while <condicao> do'
						throw new ParserException("Invalid while syntax", currToken);
					}
				} else {
					if (currToken.getContent().contentEquals("if")) {
						proximoToken();
						condicao();
						if (currToken.getContent().contentEquals("then")) {
							proximoToken();
							comandos();
							pfalsa();
							if (currToken.getContent().contentEquals("$")) {
								proximoToken();
							} else {
								//Erro finalizar com $ o if
								throw new ParserException("Expected '$' after if", currToken);
							}
						} else {
							//Erro sintaxe do if é 'if <condicao> then'
							throw new ParserException("Invalid if syntax", currToken);
						}
					} else {
						//Erro esperado algum comando válido
						throw new ParserException("Unknown command: " + currToken.getContent(), currToken);
					}
				}
			}
		}
	}
	
	private void mais_comandos() throws ParserException {
		if (currToken.getContent().contentEquals(";")) {
			proximoToken();
			comandos();
		} else {
			if (currToken.getType() == TokenType.IDENTIFIER) {
				throw new ParserException("Insert ';' for more commands", currToken);
			} else {
				String[] sinais = {"=", ":=", "<>", ">=", "<=", "<", ">", "+", "*", "-", "/"};
				for (String rel : sinais) {
					if (currToken.getContent().contentEquals(rel)) {
						throw new ParserException("Expression couldn't be evaluated", currToken);
					}
				}
				String[] reservadas = {"integer", "real", "read", "write", "if", "while"};
				for (String rel : reservadas) {
					if (currToken.getContent().contentEquals(rel)) {
						throw new ParserException("Insert ';' for more commands", currToken);
					}
				}
			}
		}
	}
	
	private void resto_ident() throws ParserException {
		if (currToken.getContent().contentEquals(":=")) {
			Symbol proc = globalSymbolTable.find(expressionStack.peek().getContent());
			if (proc != null) {
				if (proc.getSymbolCategory() == SymbolCategory.PROCEDURE) {
					throw new ParserException("Only variables allowed", expressionStack.peek().getToken());
				}
			}
			stackExpressionItem();
			proximoToken();
			expressao();
			evalExpression();
		} else {
			lista_arg();
		}
	}
	
	private void lista_arg() throws ParserException {
		if (currToken.getContent().contentEquals("(")) {
			proximoToken();
			argumentos();
			checkArguments();
			if (currToken.getContent().contentEquals(")")) {
				proximoToken();
				currVarList.clear();
			} else {
				//Erro
				throw new ParserException("Expected ')' for procedure closing", currToken);
			}
		}
	}
	
	private void argumentos() throws ParserException {
		if (currToken.getType() == TokenType.IDENTIFIER) {
			findIfTokenExists(false);
			addVar();
			proximoToken();
			mais_ident();
		} else {
			//Erro
			throw new ParserException("Expected valid identifier for procedure", currToken);
		}
	}
	
	private void mais_ident() throws ParserException {
		if (currToken.getContent().contentEquals(";")) {
			proximoToken();
			argumentos();
		}
	}
	
	private void expressao() throws ParserException {
		termo();
		outros_termos();
	}
	
	private void termo() throws ParserException {
		op_un();
		fator();
		mais_fatores();
	}
	
	private void op_un() {
		if (currToken.getContent().contentEquals("+") || currToken.getContent().contentEquals("-")) {
			stackExpressionItem();
			proximoToken();
		}
	}
	
	private void fator() throws ParserException {
		if (currToken.getType() == TokenType.IDENTIFIER || currToken.getType() == TokenType.LITERAL_INTEGER || currToken.getType() == TokenType.LITERAL_REAL) {
			stackExpressionItem();
			if (currToken.getType() == TokenType.IDENTIFIER) {
				findIfTokenExists(false);
			}
			proximoToken();
		} else {
			if (currToken.getContent().contentEquals("(")) {
				stackExpressionItem();
				proximoToken();
				expressao();
				if (currToken.getContent().contentEquals(")")) {
					stackExpressionItem();
					proximoToken();
				} else {
					//Erro
					throw new ParserException("Expected ')' to close parenthesis", currToken);
				}
			} else {
				//Erro
				throw new ParserException("Expected identifier, literal integer, literal real, or expression ", currToken);
			}
		}
	}
	
	private void mais_fatores() throws ParserException {
		if (currToken.getContent().contentEquals("*") || currToken.getContent().contentEquals("/")) {
			op_mul();
			fator();
			mais_fatores();
		}
	}
	
	private void op_mul() {
		stackExpressionItem();
		proximoToken();
	}
	
	private void outros_termos() throws ParserException {
		if (currToken.getContent().contentEquals("+") || currToken.getContent().contentEquals("-")) {
			op_ad();
			termo();
			outros_termos();
		}
	}
	
	private void op_ad() {
		stackExpressionItem();
		proximoToken();
	}
	
	private void pfalsa() throws ParserException {
		if (currToken.getContent().contentEquals("else")) {
			proximoToken();
			comandos();
		}
	}
	
	private void condicao() throws ParserException {
		startExpressionStack();
		expressao();
		evalCondition();
		Symbol lval = expressionStack.pop();
		relacao();
		startExpressionStack();
		expressao();
		evalCondition();
		Symbol rval = expressionStack.pop();
		if (!rval.getType().contentEquals(lval.getType())) {
			throw new ParserException("Both sides of the condition must have same type", currToken);
		}
	}
	
	private void relacao() throws ParserException {
		String[] relacoes = {"=", "<>", ">=", "<=", "<", ">"};
		for (String rel : relacoes) {
			if (currToken.getContent().contentEquals(rel)) {
				proximoToken();
				return;
			}
		}
		throw new ParserException("Invalid relational symbol", currToken);
	}
	
	private void proximoToken() {
		if (it.hasNext()) {
			currToken = it.next();
		}
	}
	
	private void addVar() {
		if (declarationMode) {
			currVarList.add(new Symbol(currToken.getContent(), currCategory, currToken, null));
			symbolTable.addCurrAddr();
		} else {
			Symbol s;
			s = symbolTable.find(currToken.getContent());
			if (s == null) {
				s = globalSymbolTable.find(currToken.getContent());
				if (s == null) {
					s = new Symbol(currToken.getContent(), currCategory, currToken, null);
				}
			}
			s.setToken(currToken);
			currVarList.add(s);
		}
	}
	
	private void startVarDeclaration() {
		currCategory = SymbolCategory.VARIABLE;
		declarationMode = true;
	}
	
	private void startProcedure() throws ParserException {
		declarationMode = true;
		Symbol sim = symbolTable.find(currToken.getContent());
		if (sim == null) {
			globalSymbolTable.newProcedure(currToken);
			symbolTable = globalSymbolTable.getSymbolTable(currToken.getContent());
			symbolTable.setCurrAddr(globalSymbolTable.getCurrAddr() + 1);
			currCategory = SymbolCategory.ARGUMENT;
		} else {
			//Erro
			if (sim.getSymbolCategory() == SymbolCategory.PROCEDURE) {
				throw new ParserException(String.format("Procedure name already exists: %s", sim.getSymbolCategory()), sim.getToken());
			} else {
				throw new ParserException(String.format("Procedure name already exists with type :%s - %s", sim.getSymbolCategory(), sim.getType()), sim.getToken());
			}
		}
	}
	
	private void finishProcedure() {
		
		/* Debugar ondem dos parametros do procedimento
		Procedure proc = (Procedure) globalSymbolTable.find(symbolTable.getIdentifier());
		System.out.println(Arrays.toString(proc.getArguments().toArray()));
		*/
		symbolTable = globalSymbolTable;
		currCategory = SymbolCategory.VARIABLE;
		declarationMode = true;
	}
	
	private void findIfTokenExists(boolean doubt) throws ParserException {
		//Uma variável
		if (currVarList.size() == 0) {
			Symbol s;
			//Verifica no escopo atual
			s = symbolTable.find(currToken.getContent());
			if (s == null) {
				//Verifica no escopo global
				s = globalSymbolTable.find(currToken.getContent());
				if (s == null) {
					if (doubt) {
						throw new ParserException(String.format("Variable or procedure %s undeclared", currToken.getContent()), currToken);
					} else {
						throw new ParserException(String.format("Variable %s undeclared", currToken.getContent()), currToken);
					}
				} else {
					if (!doubt) {
						if (s.getSymbolCategory() != SymbolCategory.VARIABLE && s.getSymbolCategory() != SymbolCategory.ARGUMENT) {
							throw new ParserException("Only variables are allowed", s.getToken());
						}
					}
				}
			} else {
				if (!doubt) {
					if (s.getSymbolCategory() != SymbolCategory.VARIABLE && s.getSymbolCategory() != SymbolCategory.ARGUMENT) {
						throw new ParserException("Only variables are allowed", s.getToken());
					}
				}
			}
		} else {
			for (Symbol s : currVarList) {
				Symbol temp;
				//Verifica no escopo atual
				temp = symbolTable.find(s.getContent());
				if (temp == null) {
					//Verifica no escopo global
					temp = globalSymbolTable.find(s.getContent());
					if (temp == null) {
						throw new ParserException(String.format("Variable %s undeclared", s.getContent()), s.getToken());
					} else {
						if (!doubt) {
							if (temp.getSymbolCategory() != SymbolCategory.VARIABLE && temp.getSymbolCategory() != SymbolCategory.ARGUMENT) {
								throw new ParserException("Only variables are allowed", temp.getToken());
							}
						}
					}
				} else {
					if (!doubt) {
						if (temp.getSymbolCategory() != SymbolCategory.VARIABLE && temp.getSymbolCategory() != SymbolCategory.ARGUMENT) {
							throw new ParserException("Only variables are allowed", temp.getToken());
						}
					}
				}
			}
		}
	}
	
	private void addProgramIdentifier() {
		symbolTable.insert(currToken.getContent(), currToken, SymbolCategory.PROGRAM_NAME);
	}
	
	private void addSymbolsWithType(String type) throws ParserException {
		for (Symbol s : currVarList) {
			s.setType(type);
			Symbol sim = symbolTable.find(s.getContent());
			if (sim == null) {
				sim = globalSymbolTable.find(s.getContent());
				if (sim == null) {
					symbolTable.insert(s);
					if (s.getSymbolCategory() == SymbolCategory.ARGUMENT) {
						Procedure proc = (Procedure) globalSymbolTable.find(symbolTable.getIdentifier());
						proc.getArguments().add(s);
					}
				} else {
					if (sim.getSymbolCategory() == SymbolCategory.PROCEDURE) {
						throw new ParserException(String.format("%s already exists with same name ", sim.getSymbolCategory()), sim.getToken());
					} else {
						symbolTable.insert(s);
						if (s.getSymbolCategory() == SymbolCategory.ARGUMENT) {
							Procedure proc = (Procedure) globalSymbolTable.find(symbolTable.getIdentifier());
							proc.getArguments().add(s);
						}
					}
				}
			} else {
				//Erro
				if (sim.getSymbolCategory() == SymbolCategory.PROCEDURE) {
					throw new ParserException(String.format("%s already exists", sim.getSymbolCategory()), sim.getToken());
				} else {
					throw new ParserException(String.format("%s already exists with type %s", sim.getSymbolCategory(), sim.getType()), sim.getToken());
				}
			}
		}
		currVarList.clear();
	}
	
	private void startExpressionStack() {
		expressionStack = new ExpressionStack();
	}
	
	private void stackExpressionItem() {
		if (currToken.getType() == TokenType.LITERAL_INTEGER || currToken.getType() == TokenType.LITERAL_REAL || currToken.getType() == TokenType.SYMBOL) {
			expressionStack.push(new Symbol(currToken.getContent(), null, currToken, currToken.getType().getType()));
		} else {
			Symbol s;
			s = symbolTable.find(currToken.getContent());
			if (s == null) {
				s = globalSymbolTable.find(currToken.getContent());
				if (s == null) {
					s = new Symbol(currToken.getContent(), null, currToken, null);
				}
			}
			s.setToken(currToken);
			expressionStack.push(s);
		}
	}
	
	private void checkArguments() throws ParserException {
		Procedure proc = (Procedure) globalSymbolTable.find(expressionStack.peek().getContent());
		ArrayList<Symbol> arguments = proc.getArguments();
		if (arguments.size() == currVarList.size()) {
			boolean different = false;
			for (int i = 0; i < arguments.size(); i++) {
				if (!arguments.get(i).getType().contentEquals(currVarList.get(i).getType())) {
					different = true;
					break;
				}
			}
			if (different) {
				throw new ParserException("Arguments with incompatible types", expressionStack.peek().getToken());
			}
		} else {
			throw new ParserException("Incorrect number of arguments for procedure call", expressionStack.peek().getToken());
		}
	}
	
	private void evalExpression() throws ParserException {
		if (!expressionStack.evalExprWithAssignment()) {
			Symbol sim = expressionStack.pop();
			throw new ParserException(String.format("Variable assignment of type %s can't have a different type", sim.getType()), sim.getToken());
		}
	}
	
	private void evalCondition() throws ParserException {
		Symbol sim = expressionStack.peek();
		if (!expressionStack.evalExpr()) {
			throw new ParserException("Expression with different types", sim.getToken());
		}
	}
}
