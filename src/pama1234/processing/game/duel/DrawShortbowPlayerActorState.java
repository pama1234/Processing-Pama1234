package pama1234.processing.game.duel;

import pama1234.processing.game.duel.util.PlayerActorState;
import pama1234.processing.game.duel.util.actor.PlayerActor;
import pama1234.processing.game.duel.util.actor.ShortbowArrow;
import pama1234.processing.game.duel.util.input.AbstractInputDevice;
import processing.core.PApplet;

public final class DrawShortbowPlayerActorState extends DrawBowPlayerActorState{
  /**
   *
   */
  private final Duel duel;
  /**
   * @param duel
   */
  DrawShortbowPlayerActorState(Duel duel) {
    this.duel=duel;
  }
  final int fireIntervalFrameCount=PApplet.parseInt(Duel.IDEAL_FRAME_RATE*0.2f);
  public void aim(PlayerActor parentActor,AbstractInputDevice input) {
    parentActor.aimAngle=getEnemyPlayerActorAngle(parentActor);
  }
  public void fire(PlayerActor parentActor) {
    ShortbowArrow newArrow=new ShortbowArrow(this.duel);
    final float directionAngle=parentActor.aimAngle;
    newArrow.xPosition=parentActor.xPosition+24.0f*Duel.cos(directionAngle);
    newArrow.yPosition=parentActor.yPosition+24.0f*Duel.sin(directionAngle);
    newArrow.rotationAngle=directionAngle;
    newArrow.setVelocity(directionAngle,24.0f);
    parentActor.group.addArrow(newArrow);
  }
  public void displayEffect(PlayerActor parentActor) {
    this.duel.line(0.0f,0.0f,70.0f*Duel.cos(parentActor.aimAngle),70.0f*Duel.sin(parentActor.aimAngle));
    this.duel.noFill();
    this.duel.arc(0.0f,0.0f,100.0f,100.0f,parentActor.aimAngle-Duel.QUARTER_PI,parentActor.aimAngle+Duel.QUARTER_PI);
  }
  public PlayerActorState entryState(PlayerActor parentActor) {
    return this;
  }
  public boolean buttonPressed(AbstractInputDevice input) {
    return input.shotButtonPressed;
  }
  public boolean triggerPulled(PlayerActor parentActor) {
    return this.duel.frameCount%fireIntervalFrameCount==0;
  }
}