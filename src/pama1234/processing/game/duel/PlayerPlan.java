package pama1234.processing.game.duel;

import pama1234.processing.game.duel.util.input.AbstractInputDevice;

public abstract class PlayerPlan{
  public abstract void execute(PlayerActor player,AbstractInputDevice input);
  public abstract PlayerPlan nextPlan(PlayerActor player);
}