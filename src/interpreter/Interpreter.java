package interpreter;

import ast.*;
import callable.*;
import runtime.Return;
import runtime.RuntimeError;
import runtime.TankRuntime;
import token.Token;
import token.TokenType;
import visitors.ExpressionVisitor;
import visitors.StatementVisitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Interpreter implements
        ExpressionVisitor<Object>,
        StatementVisitor<Void> {

    private final Environment globals = new Environment();
    private Environment environment = globals;
    private final Map<Expression, Integer> locals = new HashMap<>();

    public Interpreter() {

    }

    public void interpret(List<Statement> statements) {
        try {
            for (Statement statement : statements) {
                execute(statement);
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
                if (left instanceof String || right instanceof String) {
                    return left.toString() + right.toString();
                }
                //Character + Character
                if ((left instanceof Character && right instanceof Character)) {
                    return String.valueOf(left) + right;
                }
                //TODO : add Character with number
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
                return Double.parseDouble(left.toString()) > Double.parseDouble(right.toString());
            }
            case GREATER_EQUAL: {
                checkNumberOperands(expr.getOperator(), left, right);
                return Double.parseDouble(left.toString()) >= Double.parseDouble(right.toString());
            }
            case LESS: {
                checkNumberOperands(expr.getOperator(), left, right);
                return Double.parseDouble(left.toString()) < Double.parseDouble(right.toString());
            }
            case LESS_EQUAL: {
                checkNumberOperands(expr.getOperator(), left, right);
                return Double.parseDouble(left.toString()) <= Double.parseDouble(right.toString());
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
            case PLUS_PLUS:{
                return (double)right + 1;
            }
            case MINUS_MINUS: {
                return (double) right - 1;
            }
        }
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
    public Object visit(ElvisExp expr) {
        Object condition = evaluate(expr.getCondition());
        if(isTruthy(condition)){
            return condition;
        }
        return evaluate(expr.getRightExp());
    }

    @Override
    public Object visit(TernaryExp expr) {
        Object condition = evaluate(expr.getCondition());
        if(isTruthy(condition)){
            return evaluate(expr.getFirstExp());
        }
        return evaluate(expr.getSecondExp());
    }

    @Override
    public Object visit(ArraySetExp expr) {
        Array array = (Array) environment.get(expr.getName());
        Double indexValue = (Double) evaluate(expr.getIndex());
        int index = indexValue.intValue();

        if(index < 0 || array.getLength() < index){
            throw new ArrayIndexOutOfBoundsException("Size can't be negative or bigger than array size");
        }

        Object value = evaluate(expr.getValue());
        array.setValue(value, index);
        return value;
    }

    @Override
    public Object visit(ArrayGetExp expr) {
        Double value = (Double) evaluate(expr.getSize());
        int length = value.intValue();
        if(length < 0){
            throw new ArrayIndexOutOfBoundsException("Size can't be negative");
        }
        return new Array(length);
    }

    @Override
    public Object visit(ArrayVariable expr) {
        Array array = (Array) environment.get(expr.getName());
        Double indexValue = (Double) evaluate(expr.getIndex());
        int index = indexValue.intValue();

        if(index < 0 || array.getLength() < index){
            throw new ArrayIndexOutOfBoundsException("Size can't be negative or bigger than array size");
        }

        return array.getValue(index);
    }

    @Override
    public Object visit(CallExp expr) {
        Object callee = evaluate(expr.getCallee());

        List<Object> arguments = new ArrayList<>();
        for (Expression argument : expr.getArguments()) {
            arguments.add(evaluate(argument));
        }

        //Make sure this is callable type
        if (!(callee instanceof TankCallable)) {
            throw new RuntimeError(expr.getClosingParenthesis(), "Can only call functions and classes.");
        }

        TankCallable function = (TankCallable) callee;

        if (arguments.size() != function.arity()) {
            throw new RuntimeError(expr.getClosingParenthesis(), "Expected " +
                    function.arity() + " arguments but got " +
                    arguments.size() + ".");
        }

        return function.call(this, arguments);
    }

    @Override
    public Object visit(GetExp expr) {
        Object object = evaluate(expr.getObject());
        if (object instanceof TankInstance) {
            return ((TankInstance) object).get(expr.getName());
        }
        throw new RuntimeError(expr.getName(), "Only instances have properties.");
    }

    @Override
    public Object visit(SetExp expr) {
        Object object = evaluate(expr.getObject());

        if (!(object instanceof TankInstance)) {
            throw new RuntimeError(expr.getName(), "Only instances have fields.");
        }

        Object value = evaluate(expr.getValue());
        ((TankInstance) object).set(expr.getName(), value);
        return value;
    }

    @Override
    public Object visit(ThisExp expr) {
        return lookUpVariable(expr.getKeyword(), expr);
    }

    @Override
    public Object visit(Variable expr) {
        return lookUpVariable(expr.getName(), expr);
    }

    @Override
    public Object visit(SuperExp expr) {
        int distance = locals.get(expr);
        TankClass superclass = (TankClass) environment.getAt(distance, "super");
        TankInstance object = (TankInstance) environment.getAt(distance - 1, "this");
        TankFunction method = superclass.findMethod(expr.getMethod().lexeme);

        //Can't find this property in super class so throw Runtime Exception
        if (method == null) {
            throw new RuntimeError(expr.getMethod()
                    , "Undefined property '" + expr.getMethod().lexeme + "'.");
        }

        return method.bind(object);
    }

    @Override
    public Void visit(ExpressionStatement statement) {
        evaluate(statement.getExpression());
        return null;
    }

    @Override
    public Void visit(PrintStatement statement) {
        Object value = evaluate(statement.getExpression());
        System.out.print(stringify(value));
        return null;
    }

    @Override
    public Void visit(BlockStatement statement) {
        execute(statement.getStatementList(), new Environment(environment));
        return null;
    }

    @Override
    public Void visit(IfStatement statement) {
        Object conditionResult = evaluate(statement.getCondition());
        if (isTruthy(conditionResult)) {
            execute(statement.getThenBranch(), environment);
        } else if (statement.getElseBranch() != null) {
            execute(statement.getElseBranch(), environment);
        }
        return null;
    }

    @Override
    public Void visit(WhileStatement statement) {
        Environment whileEnvironment = new Environment(environment);
        Environment previous = this.environment;
        this.environment = whileEnvironment;

        while (isTruthy(evaluate(statement.getCondition()))) {
            try {
                execute(statement.getLoopBody());
            } catch (MoveKeyword moveKeyword) {
                //Break;
                if (moveKeyword.getMoveType() == MoveKeyword.MoveType.BREAK) {
                    break;
                }
            }
        }
        this.environment = previous;
        return null;
    }

    @Override
    public Void visit(DoWhileStatement statement) {
        Environment whileEnvironment = new Environment(environment);
        Environment previous = this.environment;
        this.environment = whileEnvironment;

        do {
            try {
                execute(statement.getLoopBody());
            } catch (MoveKeyword moveKeyword) {
                //Break;
                if (moveKeyword.getMoveType() == MoveKeyword.MoveType.BREAK) {
                    break;
                }
            }
        } while (isTruthy(evaluate(statement.getCondition())));

        this.environment = previous;
        return null;
    }

    @Override
    public Void visit(RepeatStatement statement) {
        Environment repeatEnvironment = new Environment(environment);
        Environment previous = this.environment;
        this.environment = repeatEnvironment;

        Object value = evaluate(statement.getValue());

        boolean isNotNumber = !(value instanceof Number);

        if (isNotNumber) {
            throw new RuntimeException("Repeat Counter must be number");
        }

        int counter = (int) Double.parseDouble(value.toString());
        for (int i = 0; i < counter; i++) {
            try {
                execute(statement.getLoopBody());
            } catch (MoveKeyword moveKeyword) {
                //Break;
                if (moveKeyword.getMoveType() == MoveKeyword.MoveType.BREAK) {
                    break;
                }
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
    public Void visit(ReturnStatement statement) {
        Object value = null;
        if (statement.getValue() != null) {
            value = evaluate(statement.getValue());
        }
        throw new Return(value);
    }

    @Override
    public Void visit(ClassStatement statement) {
        Object superclass = null;
        //Because superclass is variable expression assert it class not any other knid of expressions
        if (statement.getSuperClass() != null) {
            superclass = evaluate(statement.getSuperClass());
            if (!(superclass instanceof TankClass)) {
                throw new RuntimeError(statement.getSuperClass().getName(), "Superclass must be a class.");
            }
        }

        environment.define(statement.getName().lexeme, null);

        if (statement.getSuperClass() != null) {
            environment = new Environment(environment);
            environment.define("super", superclass);
        }

        Map<String, TankFunction> methods = new HashMap<>();

        //Bind all method into the class to call them with this leter
        for (FunctionStatement method : statement.getMethods()) {
            TankFunction function = new TankFunction(method, environment,
                    method.getName().lexeme.equals("init"));
            methods.put(method.getName().lexeme, function);
        }

        TankClass tankClass = new TankClass(statement.getName().lexeme, (TankClass) superclass, environment, methods);

        if (superclass != null) {
            environment = environment.enclosing;
        }

        environment.assign(statement.getName(), tankClass);
        return null;
    }

    @Override
    public Void visit(NativeFunctionStatement statement) {
        TankLibrary tankLibrary = new TankLibrary(statement);
        environment.define(statement.getName().lexeme, tankLibrary);
        return null;
    }

    @Override
    public Void visit(BreakStatement statement) {
        throw new MoveKeyword(MoveKeyword.MoveType.BREAK);
    }

    @Override
    public Void visit(ContinueStatement statement) {
        throw new MoveKeyword(MoveKeyword.MoveType.CONTINUE);
    }

    @Override
    public Void visit(FunctionStatement statement) {
        TankFunction function = new TankFunction(statement, environment, false);
        environment.define(statement.getName().lexeme, function);
        return null;
    }

    @Override
    public Void visit(ExtensionStatement statement) {
        TankClass extensionClass = (TankClass) environment.get(statement.getClassName());
        if(extensionClass == null){
            throw new RuntimeException("Can't find this extension class");
        }
        FunctionStatement functionStatement = statement.getFunctionStatement();
        TankFunction extension = new TankFunction(functionStatement, extensionClass.getEnvironment(), false);
        if(extensionClass.findMethod(functionStatement.getName().lexeme) != null){
            throw new RuntimeException(extensionClass.getName() + " class already have method with same name = " + functionStatement.getName().lexeme);
        }
        extensionClass.addMethod(functionStatement.getName().lexeme ,extension);
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

        return object.toString()
                .replaceAll("\\\\n", "\n")
                .replaceAll("\\\\t","\t");
    }

    private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double) return;
        throw new RuntimeError(operator, "Operand must be a number.");
    }

    private void checkNumberOperands(Token operator, Object left, Object right) {
        if (left instanceof Number && right instanceof Number) return;
        throw new RuntimeError(operator, "Operands must the same type -> number.");
    }

    private void execute(Statement stmt) {
        stmt.accept(this);
    }

    public void execute(List<Statement> statementList, Environment localEnvironment) {
        Environment previous = this.environment;
        try {
            //Make current environment is block local not global
            this.environment = localEnvironment;
            //Execute every statement in block
            for (Statement statement : statementList) {
                try {
                    execute(statement);
                } catch (MoveKeyword type) {
                    if (type.getMoveType() == MoveKeyword.MoveType.CONTINUE) {
                        break;
                    }
                }
            }
        } finally {
            //Same like pop environment from stack
            this.environment = previous;
        }
    }

    private Object lookUpVariable(Token name, Expression expr) {
        //TODO : Fix it should return distance 1 without check
        if(name.type == TokenType.THIS){
            return environment.getAt(1, name.lexeme);
        }

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

    public Environment getEnvironment() {
        return environment;
    }
}
