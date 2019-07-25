package semantic;

import ast.*;
import interpreter.Interpreter;
import runtime.RuntimeError;
import runtime.TankRuntime;
import token.Token;
import visitors.ExpressionVisitor;
import visitors.StatementVisitor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Class to do Semantic Analysis and type checking if Tank converted to static type language
 */
public class Resolver implements ExpressionVisitor<Void>, StatementVisitor<Void> {

    private final Interpreter interpreter;
    private final Stack<Map<String, Boolean>> scopes = new Stack<>();
    private FunctionType currentFunction = FunctionType.NONE;

    public Resolver(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    private enum FunctionType {
        NONE,
        FUNCTION
    }

    @Override
    public Void visit(BinaryExp expr) {
        resolve(expr.getLeftExp());
        resolve(expr.getRightExp());
        return null;
    }

    @Override
    public Void visit(GroupingExp expr) {
        resolve(expr.getExpression());
        return null;
    }

    @Override
    public Void visit(LiteralExp expr) {
        return null;
    }

    @Override
    public Void visit(AssignExp expr) {
        resolve(expr.getValue());
        resolveLocal(expr, expr.getName());
        return null;
    }

    @Override
    public Void visit(UnaryExp expr) {
        resolve(expr.getRightExp());
        return null;
    }

    @Override
    public Void visit(LogicalExp expr) {
        resolve(expr.getLeftExp());
        resolve(expr.getRightExp());
        return null;
    }

    @Override
    public Void visit(CallExp expr) {
        resolve(expr.getCallee());

        for (Expression argument : expr.getArguments()) {
            resolve(argument);
        }

        return null;
    }

    @Override
    public Void visit(GetExp expr) {
        return null;
    }

    @Override
    public Void visit(SetExp expr) {
        resolve(expr.getValue());
        resolve(expr.getObject());
        return null;
    }

    @Override
    public Void visit(Variable expr) {
        if (!scopes.isEmpty() &&
                scopes.peek().get(expr.getName().lexeme) == Boolean.FALSE) {
            TankRuntime.error(expr.getName(),"Cannot read local variable in its own initializer.");
        }
        resolveLocal(expr, expr.getName());
        return null;
    }

    @Override
    public Void visit(ExpressionStatement statement) {
        resolve(statement.getExpression());
        return null;
    }

    @Override
    public Void visit(PrintStatement statement) {
        resolve(statement.getExpression());
        return null;
    }

    @Override
    public Void visit(BlockStatement statement) {
        beginScope();
        resolve(statement.getStatementList());
        endScope();
        return null;
    }

    @Override
    public Void visit(IfStatement statement) {
        resolve(statement.getCondition());
        resolve(statement.getThenBranch());
        //TODO : in future i will add resolve for every if else statement
        if (statement.getElseBranch() != null) resolve(statement.getElseBranch());
        return null;
    }

    @Override
    public Void visit(WhileStatement statement) {
        resolve(statement.getCondition());
        resolve(statement.getLoopBody());
        return null;
    }

    @Override
    public Void visit(DoWhileStatement statement) {
        resolve(statement.getCondition());
        resolve(statement.getLoopBody());
        return null;
    }

    @Override
    public Void visit(BreakStatement statement) {
        //TODO : add this case inside semantic to make sure it inside loop
        return null;
    }

    @Override
    public Void visit(ContinueStatement statement) {
        //TODO : add this case inside semantic to make sure it inside loop
        return null;
    }

    @Override
    public Void visit(FunctionStatement statement) {
        declare(statement.getName());
        define(statement.getName());

        resolveFunction(statement, FunctionType.FUNCTION);
        return null;
    }

    @Override
    public Void visit(Var statement) {
        //Resolving a variable declaration adds a new entry to the current innermost scope’s map
        declare(statement.getName());
        if (statement.getInitializer() != null) {
            resolve(statement.getInitializer());
        }
        define(statement.getName());
        return null;
    }

    @Override
    public Void visit(ReturnStatement statement) {
        //Make sure return is inside function
        if (currentFunction == FunctionType.NONE) {
            TankRuntime.error(statement.getKeyword(), "Cannot return from top-level code.");
        }
        if (statement.getValue() != null) {
            resolve(statement.getValue());
        }
        return null;
    }

    @Override
    public Void visit(ClassStatement statement) {
        declare(statement.getName());
        define(statement.getName());
        return null;
    }

    private void beginScope() {
        scopes.push(new HashMap<>());
    }

    private void endScope() {
        scopes.pop();
    }

    public void resolve(List<Statement> stmtList) {
        for (Statement statement : stmtList) {
            resolve(statement);
        }
    }

    private void resolve(Statement stmt) {
        stmt.accept(this);
    }

    private void resolve(Expression expr) {
        expr.accept(this);
    }

    private void resolveLocal(Expression expr, Token name) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(name.lexeme)) {
                interpreter.resolve(expr, scopes.size() - 1 - i);
                return;
            }
        }
        // Not found. Assume it is global.
    }

    private void resolveFunction(FunctionStatement func, FunctionType type){
        FunctionType enclosingFunction = currentFunction;
        currentFunction = type;

        beginScope();
        for (Token param : func.getParams()) {
            declare(param);
            define(param);
        }
        resolve(func.getFunctionBody());
        endScope();

        currentFunction = enclosingFunction;
    }

    private void declare(Token name) {
        if (scopes.isEmpty()) return;

        Map<String, Boolean> scope = scopes.peek();

        //Never declare variable twice in same scope
        if (scope.containsKey(name.lexeme)) {
            TankRuntime.error(name,"Variable with this name already declared in this scope.");
        }
        scope.put(name.lexeme, false);
    }

    private void define(Token name) {
        //set the variable’s value in the scope map to true to mark it as fully initialized and available for use
        if (scopes.isEmpty()) return;
        scopes.peek().put(name.lexeme, true);
    }
}
