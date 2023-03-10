package pama1234.processing.game.duel;

public final class NullPlayerActor extends AbstractPlayerActor{
  public NullPlayerActor() {
    super(0.0f,null);
  }
  public void act() {}
  public void display() {}
  public boolean isNull() {
    return true;
  }
}