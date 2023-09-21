package osu.kip;

import java.util.ArrayList;
import java.util.Arrays;

public class AstPrinter implements Expres.Visitor<String> {

	public String print(Expres expr) {
		return expr.accept(this);
	}

	private String parenthesize(String name, Expres... exprs) {
		StringBuilder builder = new StringBuilder();

		builder.append("(").append(name);
		for (Expres expr : exprs) {
			builder.append(" ");
			builder.append(expr.accept(this));
		}
		builder.append(")");

		return builder.toString();
	}

	@Override
	public String visitBinaryExpr(Expres.Binary expr) {
		return parenthesize(expr.operator.lexeme, expr.left, expr.right);
	}

	@Override
	public String visitUnaryExpr(Expres.Unary expr) {
		return parenthesize(expr.operator.lexeme,expr.expres);
	}

	@Override
	public String visitGroupExpr(Expres.Group expr) {
		return parenthesize("group", expr.expression);
	}

	@Override
	public String visitComma(Expres.Comma expr) {
		return parenthesize("comma", expr.expressions.toArray(Expres[]::new));
	}

	@Override
	public String visitLiteralExpr(Expres.Literal expr) {
		if (expr.value == null) return "nil";
		return expr.value.toString();
	}

	@Override
	public String visitTemaryExpr(Expres.Temary expr) {
		return parenthesize("?", expr.test, expr.trueExpr,expr.falseExpr);
	}


}
