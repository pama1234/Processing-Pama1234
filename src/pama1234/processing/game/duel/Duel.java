package pama1234.processing.game.duel;

import pama1234.processing.game.duel.util.input.KeyInput;
import processing.core.PApplet;
import processing.core.PFont;

/**
 * Title: Duel Author: FAL ( https://www.fal-works.com/ ) Made with Processing 3.3.6
 * </p>
 * </p>
 * 
 * Change log: Ver. 0.1 (30. Sep. 2017) First version. Ver. 0.2 ( 1. Oct. 2017) Bug fix
 * (unintended change of strokeWeight), minor update (enabled to hide instruction window). Ver.
 * 0.3 (10. Feb. 2018) Minor fix (lack of semicolon). Ver. 0.4 (12. Feb. 2018) Enabled scaling.
 * </p>
 * </p>
 * 
 * The font "Unifont" https://unifoundry.com/unifont/ is part of the GNU Project.
 */
public class Duel extends PApplet{
  public static final float IDEAL_FRAME_RATE=60.0f;
  public static final int INTERNAL_CANVAS_SIDE_LENGTH=640;
  private static final boolean USE_NAMED_FONT=true;
  public KeyInput currentKeyInput;
  public GameSystem system;
  public PFont smallFont,largeFont;
  public boolean paused;
  public int canvasSideLength=INTERNAL_CANVAS_SIDE_LENGTH;
  // public float scaleFactor;
  public void settings() {
    size(canvasSideLength,canvasSideLength);
  }
  public void setup() {
    // scaleFactor=(float)canvasSideLength/(float)INTERNAL_CANVAS_SIDE_LENGTH;
    frameRate(IDEAL_FRAME_RATE);
    final String fontFilePath="unifont-13.0.06.ttf";
    final String fontName="Unifont";
    smallFont=createFont(USE_NAMED_FONT?fontName:fontFilePath,20.0f,true);
    largeFont=createFont(USE_NAMED_FONT?fontName:fontFilePath,96.0f,true);
    textFont(largeFont,96.0f);
    textAlign(CENTER,CENTER);
    rectMode(CENTER);
    ellipseMode(CENTER);
    currentKeyInput=new KeyInput();
    newGame(true,true); // demo play (computer vs computer), shows instruction window
  }
  public void draw() {
    background(255.0f);
    // scale(scaleFactor);
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
  static public void main(String[] passedArgs) {
    String[] appletArgs=new String[] {Duel.class.getName()};
    if(passedArgs!=null) {
      PApplet.main(concat(appletArgs,passedArgs));
    }else {
      PApplet.main(appletArgs);
    }
  }
}
