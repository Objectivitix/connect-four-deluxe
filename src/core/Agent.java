package core;

// an abstract class for game-playing agents
public abstract class Agent {
    // the token they bear, either X or O
    public final Token token;

    protected Agent(Token token) {
        this.token = token;
    }

    // to be implemented: how exactly the agent makes a move
    // (is it by waiting for GUI signal? is it by calculating?)
    public abstract int getMove();
}
