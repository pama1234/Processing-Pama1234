package pama1234.processing.game.duel;

import pama1234.processing.game.duel.util.input.AbstractInputDevice;
import pama1234.processing.game.duel.util.input.InputDevice;

public abstract class PlayerEngine{
  final AbstractInputDevice controllingInputDevice;
  PlayerEngine() {
    controllingInputDevice=new InputDevice();
  }
  public abstract void run(PlayerActor player);
}