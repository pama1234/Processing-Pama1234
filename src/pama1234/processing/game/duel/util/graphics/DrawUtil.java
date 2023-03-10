package pama1234.processing.game.duel.util.graphics;

import pama1234.processing.game.duel.Duel;
import processing.core.PConstants;

public class DrawUtil{
  public static void displayDemo(Duel duel) {
    duel.pushStyle();
    duel.stroke(0.0f);
    duel.strokeWeight(2.0f);
    duel.fill(255.0f,240.0f);
    duel.rect(
      Duel.INTERNAL_CANVAS_SIDE_LENGTH*0.5f,
      Duel.INTERNAL_CANVAS_SIDE_LENGTH*0.5f,
      Duel.INTERNAL_CANVAS_SIDE_LENGTH*0.7f,
      Duel.INTERNAL_CANVAS_SIDE_LENGTH*0.6f);
    duel.textFont(duel.smallFont,20.0f);
    duel.textLeading(26.0f);
    duel.textAlign(PConstants.RIGHT,PConstants.BASELINE);
    duel.fill(0.0f);
    duel.text("Z key:",280.0f,180.0f);
    duel.text("X key:",280.0f,250.0f);
    duel.text("Arrow key:",280.0f,345.0f);
    duel.textAlign(PConstants.LEFT);
    duel.text("Weak shot\n (auto aiming)",300.0f,180.0f);
    duel.text("Lethal shot\n (manual aiming,\n  requires charge)",300.0f,250.0f);
    duel.text("Move\n (or aim lethal shot)",300.0f,345.0f);
    duel.textAlign(PConstants.CENTER);
    duel.text("- Press Z key to start -",Duel.INTERNAL_CANVAS_SIDE_LENGTH*0.5f,430.0f);
    duel.text("(Click to hide this window)",Duel.INTERNAL_CANVAS_SIDE_LENGTH*0.5f,475.0f);
    duel.popStyle();
    duel.strokeWeight(1.0f);
  }
}