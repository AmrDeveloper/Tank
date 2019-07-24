package interpreter;

import ast.*;
import callable.FuncCallable;
import callable.TankCallable;
import nativefunc.NativePackage;
import runtime.Return;
import runtime.RuntimeError;
import runtime.TankRuntime;
import token.Token;
import visitors.ExpressionVisitor;
import visitors.StatementVisitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Interpreter implements ExpressionVisitor<Object>, StatementVisitor<Void> {

    public final Environment globals = new Environment();
    private Environment environment = globals;
    private final Map<Expression, Integer> locals = new HashMap<>();

    public Interpreter(){

    }

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

        Integer distance = locals.get(expr);
        if (distance != null) {
            environment.assignAt(distance, name, value);
        } else {
            globals.assign(name, value);
        }
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
    public Object visit(CallExp expr) {
        Object callee = evaluate(expr.getCallee());

        List<Object> arguments = new ArrayList<>();
        for (Expression argument : expr.getArguments()) {
            arguments.add(evaluate(argument));
            //Make sure this is callable type
            if (!(callee instanceof TankCallable)) {
                throw new RuntimeError(expr.getClosingParenthesis(), "Can only call functions and classes.");
            }
        }

        TankCallable function = (TankCallable)callee;

        if (arguments.size() != function.arity()) {
            throw new RuntimeError(expr.getClosingParenthesis(), "Expected " +
                    function.arity() + " arguments but got " +
                    arguments.size() + ".");
        }

        return function.call(this,arguments);
    }

    @Override
    public Object visit(Variable expr) {
        return lookUpVariable(expr.getName(), expr);
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

        while(isTruthy(evaluate(statement.getCondition()))){
            executeWhileStatement(statement.getLoopBody(),previous);
        }

        this.environment = previous;
        return null;
    }

    @Override
    public Void visit(DoWhileStatement statement) {
        Environment whileEnvironment = new Environment(environment);
        Environment previous = this.environment;
        this.environment = whileEnvironment;

        //Execute once
        executeWhileStatement(statement.getLoopBody(),previous);

        while(isTruthy(evaluate(statement.getCondition()))){
            executeWhileStatement(statement.getLoopBody(),previous);
        }

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
    public Void visit(ReturnStatement statement) {
        Object value = null;
        if (statement.getValue() != null) {
            value = evaluate(statement.getValue());
        }
        throw new Return(value);
    }

    @Override
    public Void visit(BreakStatement statement) {
        return null;
    }

    @Override
    public Void visit(ContinueStatement statement) {
        return null;
    }

    @Override
    public Void visit(FunctionStatement statement) {
        FuncCallable function = new FuncCallable(statement,environment);
        environment.define(statement.getName().lexeme, function);
        return null;
    }

    private Object evaluate(Expression expr) {
        return expr.accept(this);
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

    private void executeStatement(Statement stmt) {
        stmt.accept(this);
    }

    public void executeBlock(List<Statement> statementList, Environment localEnvironment) {
        Environment previous = this.environment;
        try {
            //Make current environment is block local not global
            this.environment = localEnvironment;
            //Execute every statement in block
            for (Statement statement : statementList) {
                executeStatement(statement);
            }
        } finally {
            //Same like pop environment from stack
            this.environment = previous;
        }
    }

    private void executeWhileStatement(List<Statement> statementList, Environment previous){
            for (Statement statementLine : statementList) {
                if (statementLine instanceof BreakStatement) {
                    this.environment = previous;
                    return;
                } else if (statementLine instanceof ContinueStatement) {
                    break;
                }
                executeStatement(statementLine);
            }
    }

    public void bindNativePackages(NativePackage...packages){
        for(NativePackage nativePackage : packages){
            nativePackage.bindNativeFunction(globals);
        }
    }

    private Object lookUpVariable(Token name, Expression expr) {
        Integer distance = locals.get(expr);
        if (distance != null) {
            //find variable value in locales score
            return environment.getAt(distance, name.lexeme);
        } else {
            //If can't find distance in locales so it must be global variable
            return globals.get(name);
        }
    }

    public void resolve(Expression expr, int depth) {
        locals.put(expr, depth);
    }
}
