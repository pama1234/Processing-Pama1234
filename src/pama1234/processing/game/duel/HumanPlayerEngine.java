package pama1234.processing.game.duel;

import pama1234.processing.game.duel.util.actor.PlayerActor;
import pama1234.processing.game.duel.util.input.KeyInput;

public final class HumanPlayerEngine extends PlayerEngine{
  final KeyInput currentKeyInput;
  HumanPlayerEngine(KeyInput _keyInput) {
    currentKeyInput=_keyInput;
  }
  public void run(PlayerActor player) {
    final int intUp=currentKeyInput.isUpPressed?-1:0;
    final int intDown=currentKeyInput.isDownPressed?1:0;
    final int intLeft=currentKeyInput.isLeftPressed?-1:0;
    final int intRight=currentKeyInput.isRightPressed?1:0;
    controllingInputDevice.operateMoveButton(intLeft+intRight,intUp+intDown);
    controllingInputDevice.operateShotButton(currentKeyInput.isZPressed);
    controllingInputDevice.operateLongShotButton(currentKeyInput.isXPressed);
  }
}