package visitors;

import ast.*;

public interface ExpressionVisitor<R> {
    R visit(BinaryExp expr);
    R visit(GroupingExp expr);
    R visit(LiteralExp expr);
    R visit(AssignExp expr);
    R visit(UnaryExp expr);
    R visit(LogicalExp expr);
    R visit(CallExp expr);
    R visit(Variable expr);
}
