package pama1234.processing.game.duel.util.graphics;

import pama1234.math.Tools;
import pama1234.processing.game.duel.Duel;
import pama1234.processing.game.duel.util.Body;
import pama1234.processing.game.duel.util.ObjectPool;
import pama1234.processing.game.duel.util.Poolable;
import processing.core.PApplet;

public final class Particle extends Body implements Poolable<Particle>{
  private final Duel duel;
  public Particle(Duel duel) {
    this.duel=duel;
  }
  // fields for Poolable
  public boolean allocatedIndicator;
  public ObjectPool<Particle> belongingPool;
  public int allocationIdentifier;
  public float rotationAngle;
  public int displayColor;
  public float strokeWeightValue;
  public float displaySize;
  public int lifespanFrameCount;
  public int properFrameCount;
  public int particleTypeNumber;
  // override methods of Poolable
  public boolean isAllocated() {
    return allocatedIndicator;
  }
  public void setAllocated(boolean indicator) {
    allocatedIndicator=indicator;
  }
  public ObjectPool<Particle> getBelongingPool() {
    return belongingPool;
  }
  public void setBelongingPool(ObjectPool<?> pool) {
    belongingPool=(ObjectPool<Particle>)pool;
  }
  public int getAllocationIdentifier() {
    return allocationIdentifier;
  }
  public void setAllocationIdentifier(int id) {
    allocationIdentifier=id;
  }
  public void initialize() {
    xPosition=0.0f;
    yPosition=0.0f;
    xVelocity=0.0f;
    yVelocity=0.0f;
    directionAngle=0.0f;
    speed=0.0f;
    rotationAngle=0.0f;
    displayColor=Tools.color(0.0f);
    strokeWeightValue=1.0f;
    displaySize=10.0f;
    lifespanFrameCount=0;
    properFrameCount=0;
    particleTypeNumber=0;
  }
  public void update() {
    super.update();
    xVelocity=xVelocity*0.98f;
    yVelocity=yVelocity*0.98f;
    properFrameCount++;
    if(properFrameCount>lifespanFrameCount) this.duel.system.commonParticleSet.removingParticleList.add(this);
    switch(particleTypeNumber) {
      case 1: // Square
        rotationAngle+=1.5f*Duel.TWO_PI/Duel.IDEAL_FRAME_RATE;
        break;
      default:
        break;
    }
  }
  public float getProgressRatio() {
    return Duel.min(1.0f,PApplet.parseFloat(properFrameCount)/lifespanFrameCount);
  }
  public float getFadeRatio() {
    return 1.0f-getProgressRatio();
  }
  public void display() {
    switch(particleTypeNumber) {
      case 0: // Dot
        this.duel.set(PApplet.parseInt(xPosition),PApplet.parseInt(yPosition),Tools.color(128.0f+127.0f*getProgressRatio()));
        break;
      case 1: // Square
        this.duel.noFill();
        this.duel.stroke(displayColor,255.0f*getFadeRatio());
        this.duel.pushMatrix();
        this.duel.translate(xPosition,yPosition);
        this.duel.rotate(rotationAngle);
        this.duel.rect(0.0f,0.0f,displaySize,displaySize);
        this.duel.popMatrix();
        break;
      case 2: // Line
        this.duel.stroke(displayColor,128.0f*getFadeRatio());
        this.duel.strokeWeight(strokeWeightValue*Duel.pow(getFadeRatio(),4.0f));
        this.duel.line(xPosition,yPosition,xPosition+800.0f*Duel.cos(rotationAngle),yPosition+800.0f*Duel.sin(rotationAngle));
        this.duel.strokeWeight(1.0f);
        break;
      case 3: // Ring
        final float ringSizeExpandRatio=2.0f*(Duel.pow(getProgressRatio()-1.0f,5.0f)+1.0f);
        this.duel.noFill();
        this.duel.stroke(displayColor,255.0f*getFadeRatio());
        this.duel.strokeWeight(strokeWeightValue*getFadeRatio());
        this.duel.ellipse(xPosition,yPosition,displaySize*(1.0f+ringSizeExpandRatio),displaySize*(1.0f+ringSizeExpandRatio));
        this.duel.strokeWeight(1.0f);
        break;
      default:
        break;
    }
  }
}