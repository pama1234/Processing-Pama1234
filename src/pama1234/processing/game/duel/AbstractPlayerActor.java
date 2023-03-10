package pama1234.processing.game.duel;

import pama1234.processing.game.duel.Duel.PlayerEngine;
import pama1234.processing.game.duel.util.PlayerActorState;

public abstract class AbstractPlayerActor extends Actor{
  public final PlayerEngine engine;
  public PlayerActorState state;
  AbstractPlayerActor(float _collisionRadius,PlayerEngine _engine) {
    super(_collisionRadius);
    engine=_engine;
  }
  public boolean isNull() {
    return false;
  }
}