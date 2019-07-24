package visitors;

import ast.*;

public interface StatementVisitor<R>{
    R visit(ExpressionStatement statement);
    R visit(PrintStatement statement);
    R visit(BlockStatement statement);
    R visit(IfStatement statement);
    R visit(WhileStatement statement);
    R visit(Var statement);

    R visit(BreakStatement statement);
    R visit(ContinueStatement statement);
}
