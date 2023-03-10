package pama1234.processing.game.duel;

abstract class GameSystemState{
  public final Duel duel;
  GameSystemState(Duel duel) {
    this.duel=duel;
  }
  int properFrameCount;
  public void run(GameSystem system) {
    runSystem(system);
    this.duel.translate(Duel.INTERNAL_CANVAS_SIDE_LENGTH*0.5f,Duel.INTERNAL_CANVAS_SIDE_LENGTH*0.5f);
    displayMessage(system);
    checkStateTransition(system);
    properFrameCount++;
  }
  public abstract void runSystem(GameSystem system);
  public abstract void displayMessage(GameSystem system);
  public abstract void checkStateTransition(GameSystem system);
}