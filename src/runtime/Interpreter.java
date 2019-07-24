package runtime;

import ast.*;
import token.Token;
import visitors.ExpressionVisitor;
import visitors.StatementVisitor;

import java.util.List;

public class Interpreter implements ExpressionVisitor<Object>, StatementVisitor<Void> {

    private Environment environment = new Environment();

    public void interpret(List<Statement> statements) {
        try {
            for (Statement statement : statements) {
                executeStatement(statement);
            }
        } catch (RuntimeError error) {
            TankRuntime.runtimeError(error);
        }
    }

    @Override
    public Object visit(BinaryExp expr) {
        Object left = evaluate(expr.getLeftExp());
        Object right = evaluate(expr.getRightExp());
        switch (expr.getOperator().type) {
            case PLUS: {
                //Math Plus
                if (left instanceof Double && right instanceof Double) {
                    return (double) left + (double) right;
                }
                //String Addition
                if (left instanceof String && right instanceof String) {
                    return (String) left + right;
                }
                //String + Character || Character + String
                if ((left instanceof Character || left instanceof String) &&
                        (right instanceof Character || right instanceof String)) {
                    return String.valueOf(left) + right;
                }
                throw new RuntimeError(expr.getOperator(), "Operands must be two numbers or two strings.");
            }
            case MINUS: {
                checkNumberOperands(expr.getOperator(), left, right);
                return (double) left - (double) right;
            }
            case STAR: {
                if ((left instanceof String || left instanceof Character)
                        && right instanceof Double) {
                    StringBuilder result = new StringBuilder(left.toString());
                    for (int i = 1; i < (double) right; i++) {
                        result.append(left.toString());
                    }
                    return result.toString();
                }
                checkNumberOperands(expr.getOperator(), left, right);
                return (double) left * (double) right;
            }
            case SLASH: {
                checkNumberOperands(expr.getOperator(), left, right);
                if ((double) right == 0) {
                    throw new RuntimeError(expr.getOperator(), "Can't use slash with zero double.");
                }
                return (double) left / (double) right;
            }
            case GREATER: {
                checkNumberOperands(expr.getOperator(), left, right);
                return (double) left > (double) right;
            }
            case GREATER_EQUAL: {
                checkNumberOperands(expr.getOperator(), left, right);
                return (double) left >= (double) right;
            }
            case LESS: {
                checkNumberOperands(expr.getOperator(), left, right);
                return (double) left < (double) right;
            }
            case LESS_EQUAL: {
                checkNumberOperands(expr.getOperator(), left, right);
                return (double) left <= (double) right;
            }
            case BANG_EQUAL: {
                return !isEqual(left, right);
            }
            case EQUAL_EQUAL: {
                return isEqual(left, right);
            }
        }
        return null;
    }

    @Override
    public Object visit(GroupingExp expr) {
        return evaluate(expr);
    }

    @Override
    public Object visit(LiteralExp expr) {
        return expr.getValue();
    }

    @Override
    public Object visit(AssignExp expr) {
        Token name = expr.getName();
        Object value = evaluate(expr.getValue());
        environment.assign(name, value);
        return value;
    }

    @Override
    public Object visit(UnaryExp expr) {
        Object right = evaluate(expr.getRightExp());
        switch (expr.getOperator().type) {
            case MINUS: {
                checkNumberOperand(expr.getOperator(), right);
                return -(double) right;
            }
            case BANG: {
                return !isTruthy(right);
            }
            //TODO : next time add ++ and --
        }
        // Unreachable.
        return null;
    }

    @Override
    public Object visit(LogicalExp expr) {
        Object left = evaluate(expr.getLeftExp());
        switch (expr.getOperator().type) {
            case AND: {
                Object right = evaluate(expr.getRightExp());
                return isTruthy(left) && isTruthy(right);
            }
            case OR: {
                Object right = evaluate(expr.getRightExp());
                return isTruthy(left) || isTruthy(right);
            }
            case XOR: {
                Object right = evaluate(expr.getRightExp());
                return isTruthy(left) ^ isTruthy(right);
            }
        }
        return false;
    }

    @Override
    public Object visit(Variable expr) {
        return environment.get(expr.getName());
    }

    @Override
    public Void visit(ExpressionStatement statement) {
        evaluate(statement.getExpression());
        return null;
    }

    @Override
    public Void visit(PrintStatement statement) {
        Object value = evaluate(statement.getExpression());
        System.out.println(stringify(value));
        return null;
    }

    @Override
    public Void visit(BlockStatement statement) {
        executeBlock(statement.getStatementList(), new Environment(environment));
        return null;
    }

    @Override
    public Void visit(IfStatement statement) {
        Object conditionResult = evaluate(statement.getCondition());
        if (isTruthy(conditionResult)) {
            executeBlock(statement.getThenBranch(), new Environment(environment));
        } else if (statement.getElseBranch() != null) {
            executeBlock(statement.getElseBranch(), new Environment(environment));
        }
        return null;
    }

    @Override
    public Void visit(WhileStatement statement) {
        Environment whileEnvironment = new Environment(environment);
        Environment previous = this.environment;
        this.environment = whileEnvironment;
        while (isTruthy(evaluate(statement.getCondition()))) {
            for (Statement statementLine : statement.getLoopBody()) {
                if (statementLine instanceof BreakStatement) {
                    this.environment = previous;
                    return null;
                } else if (statementLine instanceof ContinueStatement) {
                    break;
                }
                executeStatement(statementLine);
            }
        }
        this.environment = previous;
        return null;
    }

    @Override
    public Void visit(Var statement) {
        Object value = null;
        if (statement.getInitializer() != null) {
            value = evaluate(statement.getInitializer());
        }
        environment.define(statement.getName().lexeme, value);
        return null;
    }

    @Override
    public Void visit(BreakStatement statement) {
        return null;
    }

    @Override
    public Void visit(ContinueStatement statement) {
        return null;
    }

    private Object evaluate(Expression expr) {
        return expr.accept(this);
    }

    private void executeStatement(Statement stmt) {
        stmt.accept(this);
    }

    private boolean isTruthy(Object object) {
        if (object == null) return false;
        if (object instanceof Boolean) return (boolean) object;
        return true;
    }

    private boolean isEqual(Object a, Object b) {
        // nil is only equal to nil.
        if (a == null && b == null) return true;
        if (a == null) return false;

        return a.equals(b);
    }

    private String stringify(Object object) {
        if (object == null) return "nil";
        // Hack. Work around Java adding ".0" to integer-valued doubles.
        if (object instanceof Double) {
            String text = object.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }

        return object.toString();
    }

    private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double) return;
        throw new RuntimeError(operator, "Operand must be a number.");
    }

    private void checkNumberOperands(Token operator, Object left, Object right) {
        if (left instanceof Double && right instanceof Double) return;
        throw new RuntimeError(operator, "Operands must the same type -> number.");
    }

    private void executeBlock(List<Statement> statementList, Environment localEnviroment) {
        Environment previous = this.environment;
        try {
            //Make current environment is block local not global
            this.environment = localEnviroment;
            //Execute every statement in block
            for (Statement statement : statementList) {
                executeStatement(statement);
            }
        } finally {
            //Same like pop environment from stack
            this.environment = previous;
        }
    }
}
