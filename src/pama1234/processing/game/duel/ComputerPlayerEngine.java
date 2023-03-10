package pama1234.processing.game.duel;

import pama1234.processing.game.duel.Duel.KillPlayerPlan;

public final class ComputerPlayerEngine extends PlayerEngine{
  private final Duel duel;
  public final int planUpdateFrameCount=10;
  public PlayerPlan currentPlan;
  ComputerPlayerEngine(Duel duel) {
    this.duel=duel;
    // There shoud be a smarter way!!!
    final MovePlayerPlan move=new MovePlayerPlan(duel);
    final JabPlayerPlan jab=new JabPlayerPlan(duel);
    final KillPlayerPlan kill=this.duel.new KillPlayerPlan();
    move.movePlan=move;
    move.jabPlan=jab;
    move.killPlan=kill;
    jab.movePlan=move;
    jab.jabPlan=jab;
    jab.killPlan=kill;
    kill.movePlan=move;
    currentPlan=move;
  }
  public void run(PlayerActor player) {
    currentPlan.execute(player,controllingInputDevice);
    if(this.duel.frameCount%planUpdateFrameCount==0) currentPlan=currentPlan.nextPlan(player);
  }
}