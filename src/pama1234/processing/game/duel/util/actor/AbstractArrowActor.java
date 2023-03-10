package pama1234.processing.game.duel.util.actor;

import pama1234.processing.game.duel.Duel;

public abstract class AbstractArrowActor extends Actor{
  public final float halfLength;
  public AbstractArrowActor(float _collisionRadius,float _halfLength) {
    super(_collisionRadius);
    halfLength=_halfLength;
  }
  public void update() {
    super.update();
    if(xPosition<-halfLength||
      xPosition>Duel.INTERNAL_CANVAS_SIDE_LENGTH+halfLength||
      yPosition<-halfLength||
      yPosition>Duel.INTERNAL_CANVAS_SIDE_LENGTH+halfLength) {
      group.removingArrowList.add(this);
    }
  }
  public abstract boolean isLethal();
}