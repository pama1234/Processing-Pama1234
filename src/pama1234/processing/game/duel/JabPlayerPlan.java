package pama1234.processing.game.duel;

import pama1234.processing.game.duel.util.input.AbstractInputDevice;

public final class JabPlayerPlan extends DefaultPlayerPlan{
  public JabPlayerPlan(Duel duel) {
    super(duel);
  }
  public void execute(PlayerActor player,AbstractInputDevice input) {
    super.execute(player,input);
    input.operateShotButton(true);
  }
}