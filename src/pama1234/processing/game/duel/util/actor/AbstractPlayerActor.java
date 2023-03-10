package pama1234.processing.game.duel.util.actor;

import pama1234.processing.game.duel.PlayerEngine;
import pama1234.processing.game.duel.util.PlayerActorState;

public abstract class AbstractPlayerActor extends Actor{
  public final PlayerEngine engine;
  public PlayerActorState state;
  public AbstractPlayerActor(float _collisionRadius,PlayerEngine _engine) {
    super(_collisionRadius);
    engine=_engine;
  }
  public boolean isNull() {
    return false;
  }
}