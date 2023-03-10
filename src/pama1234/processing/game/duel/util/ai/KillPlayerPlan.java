package pama1234.processing.game.duel.util.ai;

import pama1234.processing.game.duel.AbstractPlayerActor;
import pama1234.processing.game.duel.Duel;
import pama1234.processing.game.duel.PlayerActor;
import pama1234.processing.game.duel.util.input.AbstractInputDevice;

public final class KillPlayerPlan extends PlayerPlan{
  private final Duel duel;
  public PlayerPlan movePlan,escapePlan;
  public KillPlayerPlan(Duel duel) {
    this.duel=duel;
  }
  public void execute(PlayerActor player,AbstractInputDevice input) {
    int horizontalMove;
    final float relativeAngle=player.getAngle(player.group.enemyGroup.player)-player.aimAngle;
    if(Duel.abs(relativeAngle)<Duel.radians(1.0f)) horizontalMove=0;
    else {
      if((relativeAngle+Duel.TWO_PI)%Duel.TWO_PI>Duel.PI) horizontalMove=-1;
      else horizontalMove=+1;
    }
    input.operateMoveButton(horizontalMove,0);
    input.operateShotButton(false);
    if(player.state.hasCompletedLongBowCharge(player)&&this.duel.random(1.0f)<0.05f) input.operateLongShotButton(false);
    else input.operateLongShotButton(true);
  }
  public PlayerPlan nextPlan(PlayerActor player) {
    final AbstractPlayerActor enemy=player.group.enemyGroup.player;
    if(Duel.abs(player.getAngle(player.group.enemyGroup.player)-player.aimAngle)>Duel.QUARTER_PI) return movePlan;
    if(player.getDistance(enemy)<400.0f) return movePlan;
    if(player.engine.controllingInputDevice.longShotButtonPressed==false) return movePlan;
    return this;
  }
}