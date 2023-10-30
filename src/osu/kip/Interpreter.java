package osu.kip;

public class Interpreter implements Expres.Visitor<Object>{
	@Override
	public Object visitBinaryExpr(Expres.Binary expr) {
		Object right  = evaluate(expr.right);
		Object left = evaluate(expr.left);

		switch (expr.operator.type) {
			case MINUS -> {
				checkNumberOperand(expr.operator, left, right);
				return (double) left - (double) right;
			}
			case PLUS -> {
				if (left instanceof Double && right instanceof Double) {
					return (double) left + (double) right;
				}
				if (left instanceof String && right instanceof String) {
					return left + (String) right;
				}
				throw new RuntimeError(expr.operator, "Operand must me 2 numbers or strings");
			}
			case STAR -> {
				checkNumberOperand(expr.operator, left, right);
				return (double) left * (double) right;
			}
			case SLASH -> {
				checkNumberOperand(expr.operator, left, right);
				return (double) left / (double) right;
			}
			case AND -> {
				checkNumberOperand(expr.operator, left, right);
				return isTruthy(left) && isTruthy(right);
			}
			case OR -> {
				checkNumberOperand(expr.operator, left, right);
				return isTruthy(left) || isTruthy(right);
			}
			case BANG_EQUAL -> {
				checkNumberOperand(expr.operator, left, right);
				return !isEqual(left, right);
			}
			case EQUAL_EQUAL -> {
				checkNumberOperand(expr.operator, left, right);
				return isEqual(left, right);
			}
			case GREATER -> {
				checkNumberOperand(expr.operator, left, right);
				return (double) left > (double) right;
			}
			case GREATER_EQUAL -> {
				checkNumberOperand(expr.operator, left, right);
				return (double) left >= (double) right;
			}
			case LESS -> {
				checkNumberOperand(expr.operator, left, right);
				return (double) left <= (double) right;
			}
			case LESS_EQUAL -> {
				checkNumberOperand(expr.operator, left, right);
				return (double) left < (double) right;
			}
		}
		return null;
	}

	public void interpret(Expres expr) {
		try{
			Object value = evaluate(expr);
			System.out.println(stringify(value));
		}catch(RuntimeError e){
			Main.runtimeError(e);
		}

	}

	private String stringify(Object value) {
		if (value == null) return "nil";

		if (value instanceof Double){
			String text = value.toString();
			if (text.endsWith(".0")){
				return text.substring(0,text.length() - 2);
			}
			return text;
		}
		return value.toString();
	}

	private void checkNumberOperand(Token operator, Object ...operands) {
		for (Object number : operands){
			if  (!(number instanceof Double)){
				throw new RuntimeError(operator, "Operands must be a number");
			}

		}
	}


	private boolean isEqual(Object left, Object right) {
		if (left == null && right == null)return true;
		if (left == null)return false;
		return left.equals(right);

	}

	@Override
	public Object visitUnaryExpr(Expres.Unary expr) {
		Object right = evaluate(expr.expres);
		switch (expr.operator.type){
			case BANG:
				return !isTruthy(right);
			case MINUS:
				checkNumberOperand(expr.operator,right);
				return -(double)right;
		}
		return null;
	}

	private boolean isTruthy(Object right) {
		if (right == null) return false;
		if (right instanceof Boolean) return (boolean)right;
		return true;
	}

	@Override
	public Object visitGroupExpr(Expres.Group expr) {
		return evaluate(expr.expression);
	}

	@Override
	public Object visitComma(Expres.Comma expr) {
		return null;
	}

	@Override
	public Object visitLiteralExpr(Expres.Literal expr) {
		return expr.value;
	}

	@Override
	public Object visitTemaryExpr(Expres.Temary temary) {
		return null;
	}

	private Object evaluate(Expres expr) {
		return expr.accept(this);
	}
}
