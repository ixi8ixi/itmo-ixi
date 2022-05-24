package game;

public interface Board {
    void clear();
    void printRules();
    Position getPosition();
    GameResult makeMove(Move move);
}
