package osu.kip;

import java.util.ArrayList;

abstract class Expres {
	interface Visitor<R> {
		R visitBinaryExpr(Binary expr);

		R visitUnaryExpr(Unary expr);

		R visitGroupExpr(Group expr);

		R visitComma(Comma expr);

		R visitLiteralExpr(Literal expr);

		R visitTemaryExpr(Temary temary);
	}

	abstract <R> R accept(Visitor<R> visitor);

	static class Temary extends Expres{
		final Expres test;
		final Expres trueExpr;
		final Expres falseExpr;

		Temary(Expres test, Expres trueExpr, Expres falseExpr) {
			this.test = test;
			this.trueExpr = trueExpr;
			this.falseExpr = falseExpr;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitTemaryExpr(this);
		}
	}
	static class Binary extends Expres {
		final Expres right;
		final Expres left;
		final Token operator;

		Binary(Expres left, Token operator, Expres right) {
			this.left = left;
			this.right = right;
			this.operator = operator;

		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitBinaryExpr(this);
		}
	}

	static class Unary extends Expres {
		final Token operator;
		final Expres expres;

		public Unary(Token operator, Expres expres) {
			this.operator = operator;
			this.expres = expres;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitUnaryExpr(this);
		}
	}

	static class Group extends Expres {
		final Expres expression;

		Group(Expres expression) {
			this.expression = expression;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitGroupExpr(this);
		}
	}

	static class Comma extends Expres{
		final ArrayList<Expres> expressions = new ArrayList<>();


		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitComma(this);
		}
	}

	static class Literal extends Expres {
		final Object value;

		Literal(Object value) {
			this.value = value;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitLiteralExpr(this);
		}
	}



}











































