package pama1234.processing.game.duel.util.actor;

import pama1234.math.Tools;
import pama1234.processing.game.duel.Duel;
import pama1234.processing.game.duel.util.arrow.AbstractArrowActor;
import pama1234.processing.game.duel.util.graphics.Particle;
import processing.core.PApplet;
import processing.core.PConstants;

public class ShortbowArrow extends AbstractArrowActor{
  private final Duel duel;
  public final float terminalSpeed;
  public final float halfHeadLength=8.0f;
  public final float halfHeadWidth=4.0f;
  public final float halfFeatherWidth=4.0f;
  public final float featherLength=8.0f;
  public ShortbowArrow(Duel duel) {
    super(8.0f,20.0f);
    this.duel=duel;
    terminalSpeed=8.0f;
  }
  @Override
  public void update() {
    xVelocity=speed*PApplet.cos(directionAngle);
    yVelocity=speed*PApplet.sin(directionAngle);
    super.update();
    speed+=(terminalSpeed-speed)*0.1f;
  }
  @Override
  public void act() {
    if((this.duel.random(1.0f)>=0.5f)) return;
    final float particleDirectionAngle=this.directionAngle+PConstants.PI+this.duel.random(-PConstants.QUARTER_PI,PConstants.QUARTER_PI);
    for(int i=0;i<3;i++) {
      final float particleSpeed=this.duel.random(0.5f,2.0f);
      final Particle newParticle=this.duel.system.commonParticleSet.builder
        .type(1) // Square
        .position(this.xPosition,this.yPosition)
        .polarVelocity(particleDirectionAngle,particleSpeed)
        .particleSize(2.0f)
        .particleColor(Tools.color(192.0f))
        .lifespanSecond(0.5f)
        .build();
      this.duel.system.commonParticleSet.particleList.add(newParticle);
    }
  }
  @Override
  public void display() {
    this.duel.stroke(0.0f);
    this.duel.fill(0.0f);
    this.duel.pushMatrix();
    this.duel.translate(xPosition,yPosition);
    this.duel.rotate(rotationAngle);
    this.duel.line(-halfLength,0.0f,halfLength,0.0f);
    this.duel.quad(
      halfLength,0.0f,
      halfLength-halfHeadLength,-halfHeadWidth,
      halfLength+halfHeadLength,0.0f,
      halfLength-halfHeadLength,+halfHeadWidth);
    this.duel.line(-halfLength,0.0f,-halfLength-featherLength,-halfFeatherWidth);
    this.duel.line(-halfLength,0.0f,-halfLength-featherLength,+halfFeatherWidth);
    this.duel.line(-halfLength+4.0f,0.0f,-halfLength-featherLength+4.0f,-halfFeatherWidth);
    this.duel.line(-halfLength+4.0f,0.0f,-halfLength-featherLength+4.0f,+halfFeatherWidth);
    this.duel.line(-halfLength+8.0f,0.0f,-halfLength-featherLength+8.0f,-halfFeatherWidth);
    this.duel.line(-halfLength+8.0f,0.0f,-halfLength-featherLength+8.0f,+halfFeatherWidth);
    this.duel.popMatrix();
  }
  @Override
  public boolean isLethal() {
    return false;
  }
}