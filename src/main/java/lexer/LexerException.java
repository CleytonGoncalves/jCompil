package lexer;

public class LexerException extends Exception {

	private static final long serialVersionUID = 1L;

	LexerException(String msg, int line, int column){
		super(String.format("Lexical analysis failed at %d:%d: %s", line, column, msg));
	}
}
