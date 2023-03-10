package pama1234.processing.game.duel.util;

import pama1234.processing.game.duel.AbstractPlayerActor;
import pama1234.processing.game.duel.Duel;
import pama1234.processing.game.duel.PlayerActor;

public abstract class PlayerActorState{
  public abstract void act(PlayerActor parentActor);
  public abstract void displayEffect(PlayerActor parentActor);
  public abstract PlayerActorState entryState(PlayerActor parentActor);
  public float getEnemyPlayerActorAngle(PlayerActor parentActor) {
    final AbstractPlayerActor enemyPlayer=parentActor.group.enemyGroup.player;
    return Duel.atan2(enemyPlayer.yPosition-parentActor.yPosition,enemyPlayer.xPosition-parentActor.xPosition);
  }
  public boolean isDamaged() {
    return false;
  }
  public boolean isDrawingLongBow() {
    return false;
  }
  public boolean hasCompletedLongBowCharge(PlayerActor parentActor) {
    return false;
  }
}