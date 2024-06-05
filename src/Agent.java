public abstract class Agent {
    public final Token token;

    protected Agent(Token token) {
        this.token = token;
    }

    public abstract int getMove();
}
