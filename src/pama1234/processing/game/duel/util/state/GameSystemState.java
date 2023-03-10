package pama1234.processing.game.duel.util.state;

import pama1234.processing.game.duel.Duel;
import pama1234.processing.game.duel.GameSystem;

public abstract class GameSystemState{
  public final Duel duel;
  public GameSystemState(Duel duel) {
    this.duel=duel;
  }
  public int properFrameCount;
  public void run(GameSystem system) {
    properFrameCount++;
    runSystem(system);
    this.duel.translate(Duel.INTERNAL_CANVAS_SIDE_LENGTH*0.5f,Duel.INTERNAL_CANVAS_SIDE_LENGTH*0.5f);
    displayMessage(system);
    checkStateTransition(system);
  }
  public abstract void runSystem(GameSystem system);
  public abstract void displayMessage(GameSystem system);
  public abstract void checkStateTransition(GameSystem system);
}