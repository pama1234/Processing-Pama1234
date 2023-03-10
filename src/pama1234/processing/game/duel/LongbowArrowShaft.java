package pama1234.processing.game.duel;

public final class LongbowArrowShaft extends LongbowArrowComponent{
  LongbowArrowShaft(Duel duel) {
    super(duel);
  }
  public void display() {
    this.duel.strokeWeight(5.0f);
    this.duel.stroke(0.0f);
    this.duel.fill(0.0f);
    this.duel.pushMatrix();
    this.duel.translate(xPosition,yPosition);
    this.duel.rotate(rotationAngle);
    this.duel.line(-halfLength,0.0f,halfLength,0.0f);
    this.duel.popMatrix();
    this.duel.strokeWeight(1.0f);
  }
}