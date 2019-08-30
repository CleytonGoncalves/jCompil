import lexer.Lexer;
import lexer.LexerException;
import parser.Parser;
import parser.ParserException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

	public static void main(String[] args) {
		// test();
        String filename = args[0];

        if (filename == null) {
            System.err.println("Execute as: java -jar jCompil source_code.txt\n");
            return;
        }

        try {
            String sourceFile = new String(Files.readAllBytes(Paths.get(filename)));

            Lexer lexer = new Lexer(sourceFile);
            lexer.analyze();
	        System.out.println(lexer.table.getTokens());
	        System.out.println("\n#################\n");
	        
            Parser parser = new Parser(lexer.table);
            parser.analyze();
	        System.out.println(parser.symbolTable.getSymbols());

            System.out.println("Success");
        } catch (LexerException | ParserException e ) {
            System.err.println(e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static void test() {
	    try {
		    String sourceFile = new String(Files.readAllBytes(Paths.get("source.txt")));
		
		    Lexer lexer = new Lexer(sourceFile);
		    lexer.analyze();
		
		    Parser parser = new Parser(lexer.table);
		    parser.analyze();
		
		    System.out.println("source Success as expected");
	    } catch (LexerException | ParserException e ) {
		    System.err.println(e);
	    } catch (IOException e) {
		    e.printStackTrace();
	    }
	    
	    try {
		    String sourceFile = new String(Files.readAllBytes(Paths.get("source2.txt")));
		
		    Lexer lexer = new Lexer(sourceFile);
		    lexer.analyze();
		
		    Parser parser = new Parser(lexer.table);
		    parser.analyze();
		
		    System.out.println("source2 Success as expected");
	    } catch (LexerException | ParserException e ) {
		    System.err.println(e);
	    } catch (IOException e) {
		    e.printStackTrace();
	    }
	    
	    try {
		    String sourceFile = new String(Files.readAllBytes(Paths.get("source-lexical-fail.txt")));
		
		    Lexer lexer = new Lexer(sourceFile);
		    lexer.analyze();
		
		    Parser parser = new Parser(lexer.table);
		    parser.analyze();
		
		    System.out.println("Lexical unexpected success");
	    } catch (LexerException | ParserException e ) {
		    System.err.println(e);
		    System.out.println("Lexical failed as expected");
	    } catch (IOException e) {
		    e.printStackTrace();
	    }
	    
	    try {
		    String sourceFile = new String(Files.readAllBytes(Paths.get("source-syntactic-fail.txt")));
		
		    Lexer lexer = new Lexer(sourceFile);
		    lexer.analyze();
		
		    Parser parser = new Parser(lexer.table);
		    parser.analyze();
		
		    System.out.println("syntactic Unexpected success");
	    } catch (LexerException | ParserException e ) {
		    System.err.println(e);
		    System.out.println("syntactic Failed as expected");
	    } catch (IOException e) {
		    e.printStackTrace();
	    }
	    
	    try {
		    String sourceFile = new String(Files.readAllBytes(Paths.get("source-semantic-fail.txt")));
		
		    Lexer lexer = new Lexer(sourceFile);
		    lexer.analyze();
		
		    Parser parser = new Parser(lexer.table);
		    parser.analyze();
		
		    System.out.println("semantic Unexpected success");
	    } catch (LexerException | ParserException e ) {
		    System.err.println(e);
		    System.out.println("semantic Failed as expected");
	    } catch (IOException e) {
		    e.printStackTrace();
	    }
    }

}
