package pama1234.processing.game.duel;

import static pama1234.processing.game.duel.Duel.INTERNAL_CANVAS_SIDE_LENGTH;

public final class HorizontalLine extends BackgroundLine{
  public final Duel duel;
  public HorizontalLine(Duel duel) {
    super(duel.random(INTERNAL_CANVAS_SIDE_LENGTH));
    this.duel=duel;
  }
  public void display() {
    duel.line(0.0f,position,INTERNAL_CANVAS_SIDE_LENGTH,position);
  }
  public float getMaxPosition() {
    return INTERNAL_CANVAS_SIDE_LENGTH;
  }
}