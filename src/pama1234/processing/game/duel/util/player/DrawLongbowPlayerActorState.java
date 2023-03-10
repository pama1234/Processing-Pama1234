package pama1234.processing.game.duel.util.player;

import pama1234.math.Tools;
import pama1234.processing.game.duel.Duel;
import pama1234.processing.game.duel.util.actor.PlayerActor;
import pama1234.processing.game.duel.util.arrow.LongbowArrowHead;
import pama1234.processing.game.duel.util.arrow.LongbowArrowShaft;
import pama1234.processing.game.duel.util.graphics.Particle;
import pama1234.processing.game.duel.util.input.AbstractInputDevice;
import processing.core.PApplet;
import processing.core.PConstants;

public final class DrawLongbowPlayerActorState extends DrawBowPlayerActorState{
  private final Duel duel;
  public DrawLongbowPlayerActorState(Duel duel) {
    this.duel=duel;
  }
  final float unitAngleSpeed=0.1f*Duel.TWO_PI/Duel.IDEAL_FRAME_RATE;
  final int chargeRequiredFrameCount=PApplet.parseInt(0.5f*Duel.IDEAL_FRAME_RATE);
  final int effectColor=Tools.color(192.0f,64.0f,64.0f);
  final float ringSize=80.0f;
  final float ringStrokeWeight=5.0f;
  @Override
  public PlayerActorState entryState(PlayerActor parentActor) {
    parentActor.chargedFrameCount=0;
    return this;
  }
  @Override
  public void aim(PlayerActor parentActor,AbstractInputDevice input) {
    parentActor.aimAngle+=input.horizontalMoveButton*unitAngleSpeed;
  }
  @Override
  public void fire(PlayerActor parentActor) {
    final float arrowComponentInterval=24.0f;
    final int arrowShaftNumber=5;
    for(int i=0;i<arrowShaftNumber;i++) {
      LongbowArrowShaft newArrow=new LongbowArrowShaft(this.duel);
      newArrow.xPosition=parentActor.xPosition+i*arrowComponentInterval*PApplet.cos(parentActor.aimAngle);
      newArrow.yPosition=parentActor.yPosition+i*arrowComponentInterval*PApplet.sin(parentActor.aimAngle);
      newArrow.rotationAngle=parentActor.aimAngle;
      newArrow.setVelocity(parentActor.aimAngle,64.0f);
      parentActor.group.addArrow(newArrow);
    }
    LongbowArrowHead newArrow=new LongbowArrowHead(this.duel);
    newArrow.xPosition=parentActor.xPosition+arrowShaftNumber*arrowComponentInterval*PApplet.cos(parentActor.aimAngle);
    newArrow.yPosition=parentActor.yPosition+arrowShaftNumber*arrowComponentInterval*PApplet.sin(parentActor.aimAngle);
    newArrow.rotationAngle=parentActor.aimAngle;
    newArrow.setVelocity(parentActor.aimAngle,64.0f);
    final Particle newParticle=this.duel.system.commonParticleSet.builder
      .type(2) // Line
      .position(parentActor.xPosition,parentActor.yPosition)
      .polarVelocity(0.0f,0.0f)
      .rotation(parentActor.aimAngle)
      .particleColor(Tools.color(192.0f,64.0f,64.0f))
      .lifespanSecond(2.0f)
      .weight(16.0f)
      .build();
    this.duel.system.commonParticleSet.particleList.add(newParticle);
    parentActor.group.addArrow(newArrow);
    this.duel.system.screenShakeValue+=10.0f;
    parentActor.chargedFrameCount=0;
    parentActor.state=moveState.entryState(parentActor);
  }
  @Override
  public void displayEffect(PlayerActor parentActor) {
    this.duel.noFill();
    this.duel.stroke(0.0f);
    this.duel.arc(0.0f,0.0f,100.0f,100.0f,parentActor.aimAngle-PConstants.QUARTER_PI,parentActor.aimAngle+PConstants.QUARTER_PI);
    if(hasCompletedLongBowCharge(parentActor)) this.duel.stroke(effectColor);
    else this.duel.stroke(0.0f,128.0f);
    this.duel.line(0.0f,0.0f,800.0f*PApplet.cos(parentActor.aimAngle),800.0f*PApplet.sin(parentActor.aimAngle));
    this.duel.rotate(-PConstants.HALF_PI);
    this.duel.strokeWeight(ringStrokeWeight);
    this.duel.arc(0.0f,0.0f,ringSize,ringSize,0.0f,PConstants.TWO_PI*PApplet.min(1.0f,PApplet.parseFloat(parentActor.chargedFrameCount)/chargeRequiredFrameCount));
    this.duel.strokeWeight(1.0f);
    this.duel.rotate(+PConstants.HALF_PI);
    parentActor.chargedFrameCount++;
  }
  @Override
  public void act(PlayerActor parentActor) {
    super.act(parentActor);
    if(parentActor.chargedFrameCount!=chargeRequiredFrameCount) return;
    final Particle newParticle=this.duel.system.commonParticleSet.builder
      .type(3) // Ring
      .position(parentActor.xPosition,parentActor.yPosition)
      .polarVelocity(0.0f,0.0f)
      .particleSize(ringSize)
      .particleColor(effectColor)
      .weight(ringStrokeWeight)
      .lifespanSecond(0.5f)
      .build();
    this.duel.system.commonParticleSet.particleList.add(newParticle);
  }
  @Override
  public boolean isDrawingLongBow() {
    return true;
  }
  @Override
  public boolean hasCompletedLongBowCharge(PlayerActor parentActor) {
    return parentActor.chargedFrameCount>=chargeRequiredFrameCount;
  }
  @Override
  public boolean buttonPressed(AbstractInputDevice input) {
    return input.longShotButtonPressed;
  }
  @Override
  public boolean triggerPulled(PlayerActor parentActor) {
    return !buttonPressed(parentActor.engine.controllingInputDevice)&&hasCompletedLongBowCharge(parentActor);
  }
}