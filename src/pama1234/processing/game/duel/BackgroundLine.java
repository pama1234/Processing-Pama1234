package pama1234.processing.game.duel;

abstract class BackgroundLine{
  float position;
  float velocity;
  BackgroundLine(float initialPosition) {
    position=initialPosition;
  }
  public void update(float acceleration) {
    position+=velocity;
    velocity+=acceleration;
    if(position<0.0f||position>getMaxPosition()) velocity=-velocity;
  }
  public abstract void display();
  public abstract float getMaxPosition();
}