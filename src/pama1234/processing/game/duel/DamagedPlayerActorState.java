package pama1234.processing.game.duel;

import pama1234.processing.game.duel.util.PlayerActorState;
import processing.core.PApplet;

public final class DamagedPlayerActorState extends PlayerActorState{
  private final Duel duel;
  DamagedPlayerActorState(Duel duel) {
    this.duel=duel;
  }
  PlayerActorState moveState;
  final int durationFrameCount=PApplet.parseInt(0.75f*Duel.IDEAL_FRAME_RATE);
  public void act(PlayerActor parentActor) {
    parentActor.damageRemainingFrameCount--;
    if(parentActor.damageRemainingFrameCount<=0) parentActor.state=moveState.entryState(parentActor);
  }
  public void displayEffect(PlayerActor parentActor) {
    duel.noFill();
    duel.stroke(192.0f,64.0f,64.0f,255.0f*PApplet.parseFloat(parentActor.damageRemainingFrameCount)/durationFrameCount);
    duel.ellipse(0.0f,0.0f,64.0f,64.0f);
  }
  public PlayerActorState entryState(PlayerActor parentActor) {
    parentActor.damageRemainingFrameCount=durationFrameCount;
    return this;
  }
  public boolean isDamaged() {
    return true;
  }
}