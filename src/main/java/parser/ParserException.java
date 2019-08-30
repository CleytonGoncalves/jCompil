package parser;

import model.Token;

public class ParserException extends Exception {
    
    private static final long serialVersionUID = 1L;
    
    public ParserException(String msg, Token token) {
        super(String.format("Syntactical error at %d:%d: '%s' - %s ",
                token.getLine(),
                token.getColumn(),
                token.getContent(),
                msg
        ));
    }
}
