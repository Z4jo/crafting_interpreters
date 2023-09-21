package osu.kip;

import java.util.List;

public class Parser {
	private static class ParseError extends RuntimeException{}
	private final List<Token> tokens;
	private int current = 0;

	Parser(List<Token> tokens) {
		this.tokens = tokens;
	}
	public Expres parse() {
		try {
			return expression();
		} catch (ParseError error) {
			return null;
		}
	}


	private void synchronize(){
		advance();
		while(!isEnd()){
			if (previous().type == TokenType.SEMICOLON) return;

			switch (peek().type) {
				case CLASS:
				case FUN:
				case VAR:
				case FOR:
				case IF:
				case WHILE:
				case PRINT:
				case RETURN:
					return;
			}

			advance();


		}

	}

	private Expres expression(){
		Expres ret = equality();
		if(peek().type == TokenType.COMMA){
			Expres.Comma comma = new Expres.Comma();
			comma.expressions.add(ret);
			while(match(TokenType.COMMA)){
				comma.expressions.add(equality());
			}
			return comma;
		}else{
			return ret;
		}

	}

	private Expres equality() {
		//equality       → comparison ( ( "!=" | "==" ) comparison )* ;
		Expres expr = comparison();
		while(match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL,TokenType.MARK)){
			if(previous().type == TokenType.MARK){
				Expres trueExpres = comparison();
				if(match(TokenType.COLUMN)){
					Expres falseExpres= comparison();
					return new Expres.Temary(expr,trueExpres,falseExpres);
				}
				//TODO: ERROR
			}
			Token operator = previous();
			Expres exprRight= comparison();
			expr = new Expres.Binary(expr,operator,exprRight);
		}
		return expr;
	}

	private Token previous() {
		return tokens.get(current-1);
	}

	private Expres comparison() {
		//comparison     → term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
		Expres expr = term();
		while (match(TokenType.GREATER,TokenType.GREATER_EQUAL,TokenType.LESS,TokenType.LESS_EQUAL)){
			Token operator = previous();
			Expres exprRight = term();
			expr = new Expres.Binary(expr,operator, exprRight);
		}
		return expr;
	}

	private Expres term() {
		//term           → factor ( ( "-" | "+" ) factor )* ;
		Expres expr = factor();
		while (match(TokenType.MINUS,TokenType.PLUS)){
			Token operator = previous();
			Expres exprRight = factor();
			expr = new Expres.Binary(expr,operator, exprRight);
		}
		return expr;
	}

	private Expres factor() {
		//factor         → unary ( ( "/" | "*" ) unary )* ;
		Expres expr = unary();
		while (match(TokenType.SLASH,TokenType.STAR)){
			Token operator = previous();
			Expres exprRight = unary();
			expr = new Expres.Binary(expr,operator, exprRight);
		}
		return expr;
	}

	private Expres unary() {
		//unary          → ( "!" | "-" ) unary | primary ;
		if (match(TokenType.BANG,TokenType.MINUS)){
			Token operator = previous();
			Expres nextExpres = unary();
			return new Expres.Unary(operator,nextExpres);
		}
		return primary();
	}

	private Expres primary() {
		//primary        → NUMBER | STRING | "true" | "false" | "nil" | "(" expression ")" | "," expression
		if (match(TokenType.FALSE)) return new Expres.Literal(false);
		if (match(TokenType.TRUE)) return new Expres.Literal(true);
		if (match(TokenType.NIL)) return new Expres.Literal(null);

		if (match(TokenType.NUMBER, TokenType.STRING)) {
			return new Expres.Literal(previous().literal);
		}

		if (match(TokenType.LEFT_PAREN)) {
			Expres expr = expression();
			consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.");
			return new Expres.Group(expr);
		}

		throw error(peek(), "Expected expresion.");
	}

	private void consume(TokenType rightParen, String s) {
		if (check(rightParen)){
			advance();
		}else{
			Main.error(advance().line, s);
		}
	}

	private Token advance(){
		return tokens.get(current++);
	}

	private boolean match(TokenType ...types) {
		for(TokenType type : types){
			if (check(type)==true) {
				advance();
				return true;
			}
		}
		return false;
	}

	private boolean check(TokenType type) {
		if (isEnd()) return false;
		return type == peek().type;
	}

	private boolean isEnd(){
		return current >= tokens.size();
	}

	private Token peek() {
		return tokens.get(current);
	}
	private ParseError error(Token token, String message) {
		Main.error(token, message);
		return new ParseError();
	}

}
