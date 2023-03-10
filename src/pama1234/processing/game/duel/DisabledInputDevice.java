package pama1234.processing.game.duel;

import pama1234.processing.game.duel.util.input.AbstractInputDevice;

public final class DisabledInputDevice extends AbstractInputDevice{
  public void operateMoveButton(int horizontal,int vertical) {}
  public void operateShotButton(boolean pressed) {}
  public void operateLongShotButton(boolean pressed) {}
}