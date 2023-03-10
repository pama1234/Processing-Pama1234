package pama1234.processing.game.duel.util.actor;

import pama1234.math.Tools;
import pama1234.processing.game.duel.Duel;
import pama1234.processing.game.duel.util.arrow.AbstractArrowActor;
import pama1234.processing.game.duel.util.graphics.Particle;

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
  public void update() {
    xVelocity=speed*Duel.cos(directionAngle);
    yVelocity=speed*Duel.sin(directionAngle);
    super.update();
    speed+=(terminalSpeed-speed)*0.1f;
  }
  public void act() {
    if(this.duel.random(1.0f)<0.5f==false) return;
    final float particleDirectionAngle=this.directionAngle+Duel.PI+this.duel.random(-Duel.QUARTER_PI,Duel.QUARTER_PI);
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
  public boolean isLethal() {
    return false;
  }
}