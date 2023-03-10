package pama1234.processing.game.duel;

import pama1234.processing.game.duel.util.input.AbstractInputDevice;
import pama1234.processing.game.duel.util.input.InputDevice;

public abstract class PlayerEngine{
  public final AbstractInputDevice controllingInputDevice;
  public PlayerEngine() {
    controllingInputDevice=new InputDevice();
  }
  public abstract void run(PlayerActor player);
}