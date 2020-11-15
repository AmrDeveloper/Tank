package visitors;

import ast.*;

public interface StatementVisitor<R>{
    R visit(ExpressionStatement statement);
    R visit(PrintStatement statement);
    R visit(BlockStatement statement);
    R visit(IfStatement statement);
    R visit(WhileStatement statement);
    R visit(DoWhileStatement statement);
    R visit(RepeatStatement statement);
    R visit(BreakStatement statement);
    R visit(ContinueStatement statement);
    R visit(FunctionStatement statement);
    R visit(ExtensionStatement statement);
    R visit(Var statement);
    R visit(ReturnStatement statement);
    R visit(ClassStatement statement);
    R visit(NativeFunctionStatement statement);
    R visit(TestStatement statement);
}
