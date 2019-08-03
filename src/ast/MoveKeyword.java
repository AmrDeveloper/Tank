package ast;


public class MoveKeyword extends RuntimeException {

    public enum ScopeType {
        NONE, LOOP
    }

    public enum MoveType {
        BREAK,
        CONTINUE
    }

    private MoveType moveType;

    public MoveKeyword(MoveType type) {
        super(null, null, false, false);
        this.moveType = type;
    }

    public MoveType getMoveType() {
        return moveType;
    }
}
