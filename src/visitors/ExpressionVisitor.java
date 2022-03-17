package visitors;

import ast.*;

public interface ExpressionVisitor<R> {
    R visit(BinaryExp expr);
    R visit(GroupingExp expr);
    R visit(LiteralExp expr);
    R visit(AssignExp expr);
    R visit(UnaryExp expr);
    R visit(LogicalExp expr);
    R visit(BitwiseExp expr);
    R visit(CallExp expr);
    R visit(GetExp expr);
    R visit(SetExp expr);
    R visit(ThisExp expr);
    R visit(SuperExp expr);
    R visit(Variable expr);
    R visit(ElvisExp expr);
    R visit(TernaryExp expr);
    R visit(ArraySetExp expr);
    R visit(ArrayGetExp expr);
    R visit(ArrayVariable expr);
    R visit(PrefixExpression expr);
    R visit(InfixExpression expr);
}
