package pama1234.processing.game.duel.util.state;

import pama1234.processing.game.duel.Duel;
import pama1234.processing.game.duel.GameSystem;
import processing.core.PApplet;

public final class GameResultState extends GameSystemState{
  public final String resultMessage;
  public final int durationFrameCount=PApplet.parseInt(Duel.IDEAL_FRAME_RATE);
  public GameResultState(Duel duel,String msg) {
    super(duel);
    resultMessage=msg;
  }
  public void runSystem(GameSystem system) {
    system.myGroup.update();
    system.otherGroup.update();
    system.myGroup.displayPlayer();
    system.otherGroup.displayPlayer();
    system.commonParticleSet.update();
    system.commonParticleSet.display();
  }
  public void displayMessage(GameSystem system) {
    if(system.demoPlay) return;
    this.duel.fill(0.0f);
    this.duel.text(resultMessage,0.0f,0.0f);
    if(properFrameCount>durationFrameCount) {
      this.duel.pushStyle();
      this.duel.textFont(this.duel.smallFont,20.0f);
      this.duel.text("Press X key to reset.",0.0f,80.0f);
      this.duel.popStyle();
    }
  }
  public void checkStateTransition(GameSystem system) {
    if(system.demoPlay) {
      if(properFrameCount>durationFrameCount*3) {
        this.duel.newGame(true,system.showsInstructionWindow);
      }
    }else {
      if(properFrameCount>durationFrameCount&&this.duel.currentKeyInput.isXPressed) {
        this.duel.newGame(true,true); // back to demoplay with instruction window
      }
    }
  }
}