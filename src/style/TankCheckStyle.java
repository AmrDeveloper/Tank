package style;

import ast.*;
import token.Token;
import visitors.ExpressionVisitor;
import visitors.StatementVisitor;

import java.util.List;

public class TankCheckStyle
        implements StatementVisitor<Void>,
        ExpressionVisitor<Void> {

    public void checkCodeStyle(List<Statement> statementList) {
        for(Statement statement : statementList) {
            statement.accept(this);
        }
    }

    @Override
    public Void visit(BinaryExp expr) {
        Expression left = expr.getLeftExp();
        Expression right = expr.getRightExp();

        if(left instanceof LiteralExp && right instanceof LiteralExp) {
            LiteralExp leftExp = (LiteralExp) left;
            LiteralExp rightExp = (LiteralExp) right;

            Object leftValue = leftExp.getValue();
            Object rightValue = rightExp.getValue();

            String leftClassName = leftValue.getClass().getSimpleName();
            String rightClassName = rightValue.getClass().getSimpleName();

            if("Double".equals(leftClassName) && "Double".equals(rightClassName)) {
                double leftVal = (Double) leftValue;
                double rightVal = (Double) rightValue;

                Token operator = expr.getOperator();
                switch (operator.type) {
                    case STAR:
                        if(leftVal == 0.0 || rightVal == 0.0) {
                            System.err.printf("- You can replace %s * %s by zero.\n",
                                    leftValue.toString(),
                                    rightValue.toString());
                        } else {
                            System.err.printf("- Values %s and %s can be folded as constants.\n",
                                    leftValue.toString(),
                                    rightValue.toString());
                        }
                        break;
                    case SLASH:
                        if(rightVal == 0.0) {
                            System.err.printf("- You can't divide %s by zero.\n",
                                    leftValue.toString());
                        } else {
                            System.err.printf("- Values %s and %s can be folded as constants.\n",
                                    leftValue.toString(),
                                    rightValue.toString());
                        }
                        break;
                    default:
                        System.err.printf("- Values %s and %s can be folded as constants.\n",
                                leftValue.toString(),
                                rightValue.toString());
                        break;
                }
            }
            else {
                System.err.printf("- Values %s and %s can be folded as constants.\n",
                        leftValue.toString(),
                        rightValue.toString());
            }
        }
        return null;
    }

    @Override
    public Void visit(GroupingExp expr) {
        return null;
    }

    @Override
    public Void visit(LiteralExp expr) {
        return null;
    }

    @Override
    public Void visit(AssignExp expr) {
        expr.getValue().accept(this);
        return null;
    }

    @Override
    public Void visit(UnaryExp expr) {
        return null;
    }

    @Override
    public Void visit(LogicalExp expr) {
        return null;
    }

    @Override
    public Void visit(BitwiseExp expr) {
        return null;
    }

    @Override
    public Void visit(CallExp expr) {
        return null;
    }

    @Override
    public Void visit(GetExp expr) {
        return null;
    }

    @Override
    public Void visit(SetExp expr) {
        expr.getObject().accept(this);
        expr.getValue().accept(this);
        return null;
    }

    @Override
    public Void visit(ThisExp expr) {
        return null;
    }

    @Override
    public Void visit(SuperExp expr) {
        return null;
    }

    @Override
    public Void visit(Variable expr) {
        return null;
    }

    @Override
    public Void visit(ElvisExp expr) {
        return null;
    }

    @Override
    public Void visit(TernaryExp expr) {
        return null;
    }

    @Override
    public Void visit(ArraySetExp expr) {
        return null;
    }

    @Override
    public Void visit(ArrayGetExp expr) {
        return null;
    }

    @Override
    public Void visit(ArrayVariable expr) {
        return null;
    }

    @Override
    public Void visit(PrefixExpression expr) {
        String name = expr.getPrefixFunName().lexeme;
        boolean isValidName = CheckStyleConfig.functionNamePattern.matcher(name).matches();
        if(!isValidName) {
            System.out.printf("Prefix Function name -> %s is not match your Config.\n", name);
        }
        return null;
    }

    @Override
    public Void visit(ExpressionStatement statement) {
        statement.getExpression().accept(this);
        return null;
    }

    @Override
    public Void visit(PrintStatement statement) {
        statement.getExpression().accept(this);
        return null;
    }

    @Override
    public Void visit(BlockStatement statement) {
        return null;
    }

    @Override
    public Void visit(IfStatement statement) {
        return null;
    }

    @Override
    public Void visit(WhileStatement statement) {
        return null;
    }

    @Override
    public Void visit(DoWhileStatement statement) {
        return null;
    }

    @Override
    public Void visit(RepeatStatement statement) {
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

    @Override
    public Void visit(FunctionStatement statement) {
        String name = statement.getName().lexeme;
        boolean isValidName = CheckStyleConfig.functionNamePattern.matcher(name).matches();
        if(!isValidName) {
            System.out.printf("Function name -> %s is not match your Config.\n", name);
        }

        List<Token> params = statement.getParams();
        for(Token param : params) {
            boolean isValidParamName = CheckStyleConfig.paramNamePattern.matcher(param.lexeme).matches();
            if(!isValidParamName) {
                System.out.printf("Function Parameter name -> %s is not match your Config.\n", param.lexeme);
            }
        }
        return null;
    }

    @Override
    public Void visit(ExtensionStatement statement) {
        String name = statement.getFunctionStatement().getName().lexeme;
        boolean isValidName = CheckStyleConfig.extensionNamePattern.matcher(name).matches();
        if(!isValidName) {
            System.out.printf("Extension name -> %s is not match your Config.\n", name);
        }

        List<Token> params = statement.getFunctionStatement().getParams();
        for(Token param : params) {
            boolean isValidParamName = CheckStyleConfig.paramNamePattern.matcher(param.lexeme).matches();
            if(!isValidParamName) {
                System.out.printf("Extension Parameter name -> %s is not match your Config.\n", param.lexeme);
            }
        }
        return null;
    }

    @Override
    public Void visit(Var statement) {
        String varName = statement.getName().lexeme;
        boolean isValidName = CheckStyleConfig.varNamePattern.matcher(varName).matches();
        if(!isValidName) {
            System.err.printf("- Var name -> %s is not match your config.\n", varName);
        }

        statement.getInitializer().accept(this);
        return null;
    }

    @Override
    public Void visit(ReturnStatement statement) {
        return null;
    }

    @Override
    public Void visit(ClassStatement statement) {
        String className = statement.getName().lexeme;
        boolean isValidClassName = CheckStyleConfig.classNamePattern.matcher(className).matches();
        if(!isValidClassName) {
            System.err.printf("- Class Name -> %s is not match your config.\n", className);
        }

        Variable superClass = statement.getSuperClass();
        if(superClass != null) {
            String superClassName = superClass.getName().lexeme;
            boolean isValidSuperName = CheckStyleConfig.classNamePattern.matcher(superClassName).matches();
            if(!isValidSuperName) {
                System.err.printf("- Super Class Name -> %s is not match your config.\n", superClassName);
            }
        }

        List<FunctionStatement> methods = statement.getMethods();
        for(FunctionStatement method : methods) {
            method.accept(this);
        }
        return null;
    }

    @Override
    public Void visit(NativeFunctionStatement statement) {
        return null;
    }

    @Override
    public Void visit(TestStatement statement) {
        String tag = statement.getName().lexeme;
        boolean isValidTag = CheckStyleConfig.testNamePattern.matcher(tag).matches();
        if(!isValidTag) {
            System.err.printf("- Test tag -> %s is not match your Config.\n", tag);
        }
        return null;
    }
}
