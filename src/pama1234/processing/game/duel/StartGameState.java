package pama1234.processing.game.duel;

import pama1234.math.Tools;
import processing.core.PApplet;

public final class StartGameState extends GameSystemState{
  StartGameState(Duel duel) {
    super(duel);
  }
  final int frameCountPerNumber=PApplet.parseInt(Duel.IDEAL_FRAME_RATE);
  final float ringSize=200.0f;
  final int ringColor=Tools.color(0.0f);
  final float ringStrokeWeight=5.0f;
  int displayNumber=4;
  public void runSystem(GameSystem system) {
    system.myGroup.update();
    system.otherGroup.update();
    system.myGroup.displayPlayer();
    system.otherGroup.displayPlayer();
  }
  public void displayMessage(GameSystem system) {
    final int currentNumberFrameCount=properFrameCount%frameCountPerNumber;
    if(currentNumberFrameCount==0) displayNumber--;
    if(displayNumber<=0) return;
    this.duel.fill(ringColor);
    this.duel.text(displayNumber,0.0f,0.0f);
    this.duel.rotate(-Duel.HALF_PI);
    this.duel.strokeWeight(3.0f);
    this.duel.stroke(ringColor);
    this.duel.noFill();
    this.duel.arc(0.0f,0.0f,ringSize,ringSize,0.0f,Duel.TWO_PI*PApplet.parseFloat(properFrameCount%frameCountPerNumber)/frameCountPerNumber);
    this.duel.strokeWeight(1.0f);
  }
  public void checkStateTransition(GameSystem system) {
    if(properFrameCount>=frameCountPerNumber*3) {
      final Particle newParticle=system.commonParticleSet.builder
        .type(3) // Ring
        .position(Duel.INTERNAL_CANVAS_SIDE_LENGTH*0.5f,Duel.INTERNAL_CANVAS_SIDE_LENGTH*0.5f)
        .polarVelocity(0.0f,0.0f)
        .particleSize(ringSize)
        .particleColor(ringColor)
        .weight(ringStrokeWeight)
        .lifespanSecond(1.0f)
        .build();
      system.commonParticleSet.particleList.add(newParticle);
      system.currentState=new PlayGameState(this.duel);
    }
  }
}