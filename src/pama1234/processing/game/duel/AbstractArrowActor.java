package pama1234.processing.game.duel;

public abstract class AbstractArrowActor extends Actor{
  public final float halfLength;
  AbstractArrowActor(float _collisionRadius,float _halfLength) {
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