package semantic;

import ast.*;
import interpreter.Interpreter;
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

    public Resolver(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    private enum FunctionType {
        NONE,
        FUNCTION,
        METHOD,
        INITIALIZER
    }

    private enum ClassType {
        NONE,
        CLASS,
        SUBCLASS
    }

    private ClassType currentClass = ClassType.NONE;
    private FunctionType currentFunction = FunctionType.NONE;
    private MoveKeyword.ScopeType currentScopeType = MoveKeyword.ScopeType.NONE;

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
    public Void visit(ThisExp expr) {
        if (currentClass == ClassType.NONE) {
            TankRuntime.error(expr.getKeyword(), "Cannot use 'this' outside of a class.");
            return null;
        }
        resolveLocal(expr, expr.getKeyword());
        return null;
    }

    @Override
    public Void visit(Variable expr) {
        if (!scopes.isEmpty() &&
                scopes.peek().get(expr.getName().lexeme) == Boolean.FALSE) {
            TankRuntime.error(expr.getName(), "Cannot read local variable in its own initializer.");
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
        MoveKeyword.ScopeType enclosingType = currentScopeType;
        currentScopeType =  MoveKeyword.ScopeType.LOOP;
        resolve(statement.getCondition());
        resolve(statement.getLoopBody());
        currentScopeType = enclosingType;
        return null;
    }

    @Override
    public Void visit(DoWhileStatement statement) {
        MoveKeyword.ScopeType enclosingType = currentScopeType;
        currentScopeType =  MoveKeyword.ScopeType.LOOP;
        resolve(statement.getCondition());
        resolve(statement.getLoopBody());
        currentScopeType = enclosingType;
        return null;
    }

    @Override
    public Void visit(RepeatStatement statement) {
        MoveKeyword.ScopeType enclosingType = currentScopeType;
        currentScopeType =  MoveKeyword.ScopeType.LOOP;
        resolve(statement.getValue());
        resolve(statement.getLoopBody());
        currentScopeType = enclosingType;
        return null;
    }

    @Override
    public Void visit(BreakStatement statement) {
        //TODO : add this case inside semantic to make sure it inside loop
        if (currentScopeType == MoveKeyword.ScopeType.NONE) {
            TankRuntime.error(statement.getKeyword(), "Continue can only used be inside loops.");
        }
        return null;
    }

    @Override
    public Void visit(ContinueStatement statement) {
        //TODO : add this case inside semantic to make sure it inside loop
        if (currentScopeType == MoveKeyword.ScopeType.NONE) {
            TankRuntime.error(statement.getKeyword(), "Break can only used be inside loops.");
        }
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
            if (currentFunction == FunctionType.INITIALIZER) {
                TankRuntime.error(statement.getKeyword(), "Cannot return a value from an initializer.");
            }
            resolve(statement.getValue());
        }
        return null;
    }

    @Override
    public Void visit(ClassStatement statement) {
        ClassType enclosingClass = currentClass;
        currentClass = ClassType.CLASS;
        declare(statement.getName());

        //Class must not extends same class
        if (statement.getSuperClass() != null &&
                statement.getName().lexeme.equals(statement.getSuperClass().getName().lexeme)) {
            TankRuntime.error(statement.getSuperClass().getName(), "A class cannot inherit from itself.");
        }

        //for Inheritance
        if (statement.getSuperClass() != null) {
            currentClass = ClassType.SUBCLASS;
            resolve(statement.getSuperClass());
        }

        //Support super keyword
        if (statement.getSuperClass() != null) {
            beginScope();
            scopes.peek().put("super", true);
        }

        beginScope();
        scopes.peek().put("this", true);
        for (FunctionStatement method : statement.getMethods()) {
            FunctionType declaration = FunctionType.METHOD;
            if (method.getName().lexeme.equals("init")) {
                declaration = FunctionType.INITIALIZER;
            }
            resolveFunction(method, declaration);
        }
        define(statement.getName());
        endScope();
        if (statement.getSuperClass() != null) endScope();
        currentClass = enclosingClass;
        return null;
    }

    @Override
    public Void visit(ElvisExp statement) {
        resolve(statement.getCondition());
        resolve(statement.getRightExp());
        return null;
    }

    @Override
    public Void visit(SuperExp expr) {
        if (currentClass == ClassType.NONE) {
            TankRuntime.error(expr.getKeyword(), "Cannot use 'super' outside of a class.");
        } else if (currentClass != ClassType.SUBCLASS) {
            TankRuntime.error(expr.getKeyword(), "Cannot use 'super' in a class with no superclass.");
        }
        resolveLocal(expr, expr.getKeyword());
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

    private void resolveFunction(FunctionStatement func, FunctionType type) {
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
            TankRuntime.error(name, "Variable with this name already declared in this scope.");
        }
        scope.put(name.lexeme, false);
    }

    private void define(Token name) {
        //set the variable’s value in the scope map to true to mark it as fully initialized and available for use
        if (scopes.isEmpty()) return;
        scopes.peek().put(name.lexeme, true);
    }
}
