package pama1234.processing.game.duel;

import pama1234.processing.game.duel.util.input.AbstractInputDevice;
import pama1234.processing.game.duel.util.input.KeyInput;
import processing.core.PApplet;
import processing.core.PFont;

public class Duel extends PApplet{
  // Title: Duel
  // Author: FAL ( https://www.fal-works.com/ )
  // Made with Processing 3.3.6
  /*
   * Change log: Ver. 0.1 (30. Sep. 2017) First version. Ver. 0.2 ( 1. Oct. 2017) Bug fix
   * (unintended change of strokeWeight), minor update (enabled to hide instruction window). Ver.
   * 0.3 (10. Feb. 2018) Minor fix (lack of semicolon). Ver. 0.4 (12. Feb. 2018) Enabled scaling.
   */
  /* @pjs font="Lato-Regular.ttf"; */
  /*
   * The font "Lato" is designed by Łukasz Dziedzic (http://www.latofonts.com/). This font is
   * licensed under the SIL Open Font License 1.1 (http://scripts.sil.org/OFL).
   */
  // CAUTION: spaghetti code!!!
  public static final float IDEAL_FRAME_RATE=60.0f;
  public static final int INTERNAL_CANVAS_SIDE_LENGTH=640;
  private static final boolean USE_WEB_FONT=true;
  public KeyInput currentKeyInput;
  public GameSystem system;
  public PFont smallFont,largeFont;
  public boolean paused;
  public int canvasSideLength=INTERNAL_CANVAS_SIDE_LENGTH;
  public float scaleFactor;
  /*
   * For processing.js const containerRect =
   * window.document.getElementById("Duel").getBoundingClientRect(); canvasSideLength =
   * min(containerRect.width, containerRect.height);
   */
  /* For OpenProcessing */
  //canvasSideLength = min(window.innerWidth, window.innerHeight);
  // For Processing Java mode
  public void settings() {
    size(canvasSideLength,canvasSideLength);
  }
  public void setup() {
    /* For processing.js */
    //size(canvasSideLength, canvasSideLength);
    scaleFactor=(float)canvasSideLength/(float)INTERNAL_CANVAS_SIDE_LENGTH;
    frameRate(IDEAL_FRAME_RATE);
    // Prepare font
    final String fontFilePath="Lato-Regular.ttf";
    final String fontName="Lato";
    smallFont=createFont(USE_WEB_FONT?fontName:fontFilePath,20.0f,true);
    largeFont=createFont(USE_WEB_FONT?fontName:fontFilePath,96.0f,true);
    textFont(largeFont,96.0f);
    textAlign(CENTER,CENTER);
    rectMode(CENTER);
    ellipseMode(CENTER);
    currentKeyInput=new KeyInput();
    newGame(true,true); // demo play (computer vs computer), shows instruction window
  }
  public void draw() {
    background(255.0f);
    scale(scaleFactor);
    system.run();
  }
  public void newGame(boolean demo,boolean instruction) {
    system=new GameSystem(this,demo,instruction);
  }
  public void mousePressed() {
    system.showsInstructionWindow=!system.showsInstructionWindow;
  }
  public void keyPressed() {
    if(key!=CODED) {
      if(key=='z'||key=='Z') {
        currentKeyInput.isZPressed=true;
        return;
      }
      if(key=='x'||key=='X') {
        currentKeyInput.isXPressed=true;
        return;
      }
      if(key=='p') {
        if(paused) loop();
        else noLoop();
        paused=!paused;
      }
      return;
    }
    switch(keyCode) {
      case UP:
        currentKeyInput.isUpPressed=true;
        return;
      case DOWN:
        currentKeyInput.isDownPressed=true;
        return;
      case LEFT:
        currentKeyInput.isLeftPressed=true;
        return;
      case RIGHT:
        currentKeyInput.isRightPressed=true;
        return;
    }
  }
  public void keyReleased() {
    if(key!=CODED) {
      if(key=='z'||key=='Z') {
        currentKeyInput.isZPressed=false;
        return;
      }
      if(key=='x'||key=='X') {
        currentKeyInput.isXPressed=false;
        return;
      }
      return;
    }
    switch(keyCode) {
      case UP:
        currentKeyInput.isUpPressed=false;
        return;
      case DOWN:
        currentKeyInput.isDownPressed=false;
        return;
      case LEFT:
        currentKeyInput.isLeftPressed=false;
        return;
      case RIGHT:
        currentKeyInput.isRightPressed=false;
        return;
    }
  }
  public final class KillPlayerPlan extends PlayerPlan{
    PlayerPlan movePlan,escapePlan;
    public void execute(PlayerActor player,AbstractInputDevice input) {
      int horizontalMove;
      final float relativeAngle=player.getAngle(player.group.enemyGroup.player)-player.aimAngle;
      if(abs(relativeAngle)<radians(1.0f)) horizontalMove=0;
      else {
        if((relativeAngle+TWO_PI)%TWO_PI>PI) horizontalMove=-1;
        else horizontalMove=+1;
      }
      input.operateMoveButton(horizontalMove,0);
      input.operateShotButton(false);
      if(player.state.hasCompletedLongBowCharge(player)&&random(1.0f)<0.05f) input.operateLongShotButton(false);
      else input.operateLongShotButton(true);
    }
    public PlayerPlan nextPlan(PlayerActor player) {
      final AbstractPlayerActor enemy=player.group.enemyGroup.player;
      if(abs(player.getAngle(player.group.enemyGroup.player)-player.aimAngle)>QUARTER_PI) return movePlan;
      if(player.getDistance(enemy)<400.0f) return movePlan;
      if(player.engine.controllingInputDevice.longShotButtonPressed==false) return movePlan;
      return this;
    }
  }
  static public void main(String[] passedArgs) {
    String[] appletArgs=new String[] {Duel.class.getName()};
    if(passedArgs!=null) {
      PApplet.main(concat(appletArgs,passedArgs));
    }else {
      PApplet.main(appletArgs);
    }
  }
}
