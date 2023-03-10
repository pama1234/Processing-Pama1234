package pama1234.processing.game.duel;

import java.util.ArrayList;

import pama1234.math.Tools;
import pama1234.processing.game.duel.util.Body;
import pama1234.processing.game.duel.util.ObjectPool;
import pama1234.processing.game.duel.util.PlayerActorState;
import pama1234.processing.game.duel.util.input.AbstractInputDevice;
import pama1234.processing.game.duel.util.input.InputDevice;
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
  private static final float IDEAL_FRAME_RATE=60.0f;
  private static final int INTERNAL_CANVAS_SIDE_LENGTH=640;
  private static final boolean USE_WEB_FONT=true;
  KeyInput currentKeyInput;
  GameSystem system;
  PFont smallFont,largeFont;
  boolean paused;
  int canvasSideLength=INTERNAL_CANVAS_SIDE_LENGTH;
  float scaleFactor;
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
    system=new GameSystem(demo,instruction);
  }
  public void mousePressed() {
    system.showsInstructionWindow=!system.showsInstructionWindow;
  }
  public final class NullPlayerActor extends AbstractPlayerActor{
    NullPlayerActor() {
      super(0.0f,null);
    }
    public void act() {}
    public void display() {}
    public boolean isNull() {
      return true;
    }
  }
  public final class PlayerActor extends AbstractPlayerActor{
    final float bodySize=32.0f;
    final float halfBodySize=bodySize*0.5f;
    final int fillColor;
    float aimAngle;
    int chargedFrameCount;
    int damageRemainingFrameCount;
    PlayerActor(PlayerEngine _engine,int col) {
      super(16.0f,_engine);
      fillColor=col;
    }
    public void addVelocity(float xAcceleration,float yAcceleration) {
      xVelocity=constrain(xVelocity+xAcceleration,-10.0f,10.0f);
      yVelocity=constrain(yVelocity+yAcceleration,-7.0f,7.0f);
    }
    public void act() {
      engine.run(this);
      state.act(this);
    }
    public void update() {
      super.update();
      if(xPosition<halfBodySize) {
        xPosition=halfBodySize;
        xVelocity=-0.5f*xVelocity;
      }
      if(xPosition>INTERNAL_CANVAS_SIDE_LENGTH-halfBodySize) {
        xPosition=INTERNAL_CANVAS_SIDE_LENGTH-halfBodySize;
        xVelocity=-0.5f*xVelocity;
      }
      if(yPosition<halfBodySize) {
        yPosition=halfBodySize;
        yVelocity=-0.5f*yVelocity;
      }
      if(yPosition>INTERNAL_CANVAS_SIDE_LENGTH-halfBodySize) {
        yPosition=INTERNAL_CANVAS_SIDE_LENGTH-halfBodySize;
        yVelocity=-0.5f*yVelocity;
      }
      xVelocity=xVelocity*0.92f;
      yVelocity=yVelocity*0.92f;
      rotationAngle+=(0.1f+0.04f*(sq(xVelocity)+sq(yVelocity)))*TWO_PI/IDEAL_FRAME_RATE;
    }
    public void display() {
      stroke(0.0f);
      fill(fillColor);
      pushMatrix();
      translate(xPosition,yPosition);
      pushMatrix();
      rotate(rotationAngle);
      rect(0.0f,0.0f,32.0f,32.0f);
      popMatrix();
      state.displayEffect(this);
      popMatrix();
    }
  }
  public abstract class AbstractArrowActor extends Actor{
    final float halfLength;
    AbstractArrowActor(float _collisionRadius,float _halfLength) {
      super(_collisionRadius);
      halfLength=_halfLength;
    }
    public void update() {
      super.update();
      if(xPosition<-halfLength||
        xPosition>INTERNAL_CANVAS_SIDE_LENGTH+halfLength||
        yPosition<-halfLength||
        yPosition>INTERNAL_CANVAS_SIDE_LENGTH+halfLength) {
        group.removingArrowList.add(this);
      }
    }
    public abstract boolean isLethal();
  }
  public class ShortbowArrow extends AbstractArrowActor{
    final float terminalSpeed;
    final float halfHeadLength=8.0f;
    final float halfHeadWidth=4.0f;
    final float halfFeatherWidth=4.0f;
    final float featherLength=8.0f;
    ShortbowArrow() {
      super(8.0f,20.0f);
      terminalSpeed=8.0f;
    }
    public void update() {
      xVelocity=speed*cos(directionAngle);
      yVelocity=speed*sin(directionAngle);
      super.update();
      speed+=(terminalSpeed-speed)*0.1f;
    }
    public void act() {
      if(random(1.0f)<0.5f==false) return;
      final float particleDirectionAngle=this.directionAngle+PI+random(-QUARTER_PI,QUARTER_PI);
      for(int i=0;i<3;i++) {
        final float particleSpeed=random(0.5f,2.0f);
        final Particle newParticle=system.commonParticleSet.builder
          .type(1) // Square
          .position(this.xPosition,this.yPosition)
          .polarVelocity(particleDirectionAngle,particleSpeed)
          .particleSize(2.0f)
          .particleColor(Tools.color(192.0f))
          .lifespanSecond(0.5f)
          .build();
        system.commonParticleSet.particleList.add(newParticle);
      }
    }
    public void display() {
      stroke(0.0f);
      fill(0.0f);
      pushMatrix();
      translate(xPosition,yPosition);
      rotate(rotationAngle);
      line(-halfLength,0.0f,halfLength,0.0f);
      quad(
        halfLength,0.0f,
        halfLength-halfHeadLength,-halfHeadWidth,
        halfLength+halfHeadLength,0.0f,
        halfLength-halfHeadLength,+halfHeadWidth);
      line(-halfLength,0.0f,-halfLength-featherLength,-halfFeatherWidth);
      line(-halfLength,0.0f,-halfLength-featherLength,+halfFeatherWidth);
      line(-halfLength+4.0f,0.0f,-halfLength-featherLength+4.0f,-halfFeatherWidth);
      line(-halfLength+4.0f,0.0f,-halfLength-featherLength+4.0f,+halfFeatherWidth);
      line(-halfLength+8.0f,0.0f,-halfLength-featherLength+8.0f,-halfFeatherWidth);
      line(-halfLength+8.0f,0.0f,-halfLength-featherLength+8.0f,+halfFeatherWidth);
      popMatrix();
    }
    public boolean isLethal() {
      return false;
    }
  }
  abstract class LongbowArrowComponent extends AbstractArrowActor{
    LongbowArrowComponent() {
      super(16.0f,16.0f);
    }
    public void act() {
      final float particleDirectionAngle=this.directionAngle+PI+random(-HALF_PI,HALF_PI);
      for(int i=0;i<5;i++) {
        final float particleSpeed=random(2.0f,4.0f);
        final Particle newParticle=system.commonParticleSet.builder
          .type(1) // Square  
          .position(this.xPosition,this.yPosition)
          .polarVelocity(particleDirectionAngle,particleSpeed)
          .particleSize(4.0f)
          .particleColor(Tools.color(64.0f))
          .lifespanSecond(1.0f)
          .build();
        system.commonParticleSet.particleList.add(newParticle);
      }
    }
    public boolean isLethal() {
      return true;
    }
  }
  public final class LongbowArrowHead extends LongbowArrowComponent{
    final float halfHeadLength=24.0f;
    final float halfHeadWidth=24.0f;
    LongbowArrowHead() {
      super();
    }
    public void display() {
      strokeWeight(5.0f);
      stroke(0.0f);
      fill(0.0f);
      pushMatrix();
      translate(xPosition,yPosition);
      rotate(rotationAngle);
      line(-halfLength,0.0f,0.0f,0.0f);
      quad(
        0.0f,0.0f,
        -halfHeadLength,-halfHeadWidth,
        +halfHeadLength,0.0f,
        -halfHeadLength,+halfHeadWidth);
      popMatrix();
      strokeWeight(1.0f);
    }
  }
  public final class LongbowArrowShaft extends LongbowArrowComponent{
    LongbowArrowShaft() {
      super();
    }
    public void display() {
      strokeWeight(5.0f);
      stroke(0.0f);
      fill(0.0f);
      pushMatrix();
      translate(xPosition,yPosition);
      rotate(rotationAngle);
      line(-halfLength,0.0f,halfLength,0.0f);
      popMatrix();
      strokeWeight(1.0f);
    }
  }
  public final class Particle extends Body implements Poolable{
    // fields for Poolable
    boolean allocatedIndicator;
    ObjectPool belongingPool;
    int allocationIdentifier;
    float rotationAngle;
    int displayColor;
    float strokeWeightValue;
    float displaySize;
    int lifespanFrameCount;
    int properFrameCount;
    int particleTypeNumber;
    // override methods of Poolable
    public boolean isAllocated() {
      return allocatedIndicator;
    }
    public void setAllocated(boolean indicator) {
      allocatedIndicator=indicator;
    }
    public ObjectPool getBelongingPool() {
      return belongingPool;
    }
    public void setBelongingPool(ObjectPool pool) {
      belongingPool=pool;
    }
    public int getAllocationIdentifier() {
      return allocationIdentifier;
    }
    public void setAllocationIdentifier(int id) {
      allocationIdentifier=id;
    }
    public void initialize() {
      xPosition=0.0f;
      yPosition=0.0f;
      xVelocity=0.0f;
      yVelocity=0.0f;
      directionAngle=0.0f;
      speed=0.0f;
      rotationAngle=0.0f;
      displayColor=Tools.color(0.0f);
      strokeWeightValue=1.0f;
      displaySize=10.0f;
      lifespanFrameCount=0;
      properFrameCount=0;
      particleTypeNumber=0;
    }
    public void update() {
      super.update();
      xVelocity=xVelocity*0.98f;
      yVelocity=yVelocity*0.98f;
      properFrameCount++;
      if(properFrameCount>lifespanFrameCount) system.commonParticleSet.removingParticleList.add(this);
      switch(particleTypeNumber) {
        case 1: // Square
          rotationAngle+=1.5f*TWO_PI/IDEAL_FRAME_RATE;
          break;
        default:
          break;
      }
    }
    public float getProgressRatio() {
      return min(1.0f,PApplet.parseFloat(properFrameCount)/lifespanFrameCount);
    }
    public float getFadeRatio() {
      return 1.0f-getProgressRatio();
    }
    public void display() {
      switch(particleTypeNumber) {
        case 0: // Dot
          set(PApplet.parseInt(xPosition),PApplet.parseInt(yPosition),Tools.color(128.0f+127.0f*getProgressRatio()));
          break;
        case 1: // Square
          noFill();
          stroke(displayColor,255.0f*getFadeRatio());
          pushMatrix();
          translate(xPosition,yPosition);
          rotate(rotationAngle);
          rect(0.0f,0.0f,displaySize,displaySize);
          popMatrix();
          break;
        case 2: // Line
          stroke(displayColor,128.0f*getFadeRatio());
          strokeWeight(strokeWeightValue*pow(getFadeRatio(),4.0f));
          line(xPosition,yPosition,xPosition+800.0f*cos(rotationAngle),yPosition+800.0f*sin(rotationAngle));
          strokeWeight(1.0f);
          break;
        case 3: // Ring
          final float ringSizeExpandRatio=2.0f*(pow(getProgressRatio()-1.0f,5.0f)+1.0f);
          noFill();
          stroke(displayColor,255.0f*getFadeRatio());
          strokeWeight(strokeWeightValue*getFadeRatio());
          ellipse(xPosition,yPosition,displaySize*(1.0f+ringSizeExpandRatio),displaySize*(1.0f+ringSizeExpandRatio));
          strokeWeight(1.0f);
          break;
        default:
          break;
      }
    }
  }
  public final class GameSystem{
    final ActorGroup myGroup,otherGroup;
    final ParticleSet commonParticleSet;
    GameSystemState currentState;
    float screenShakeValue;
    final DamagedPlayerActorState damagedState;
    final GameBackground currentBackground;
    final boolean demoPlay;
    boolean showsInstructionWindow;
    GameSystem(boolean demo,boolean instruction) {
      // prepare ActorGroup
      myGroup=new ActorGroup();
      otherGroup=new ActorGroup();
      myGroup.enemyGroup=otherGroup;
      otherGroup.enemyGroup=myGroup;
      // prepare PlayerActorState
      final MovePlayerActorState moveState=new MovePlayerActorState();
      final DrawBowPlayerActorState drawShortbowState=new DrawShortbowPlayerActorState();
      final DrawBowPlayerActorState drawLongbowState=new DrawLongbowPlayerActorState();
      damagedState=new DamagedPlayerActorState();
      moveState.drawShortbowState=drawShortbowState;
      moveState.drawLongbowState=drawLongbowState;
      drawShortbowState.moveState=moveState;
      drawLongbowState.moveState=moveState;
      damagedState.moveState=moveState;
      // prepare PlayerActor
      PlayerEngine myEngine;
      if(demo) myEngine=new ComputerPlayerEngine();
      else myEngine=new HumanPlayerEngine(currentKeyInput);
      PlayerActor myPlayer=new PlayerActor(myEngine,Tools.color(255.0f));
      myPlayer.xPosition=INTERNAL_CANVAS_SIDE_LENGTH*0.5f;
      myPlayer.yPosition=INTERNAL_CANVAS_SIDE_LENGTH-100.0f;
      myPlayer.state=moveState;
      myGroup.setPlayer(myPlayer);
      PlayerEngine otherEngine=new ComputerPlayerEngine();
      PlayerActor otherPlayer=new PlayerActor(otherEngine,Tools.color(0.0f));
      otherPlayer.xPosition=INTERNAL_CANVAS_SIDE_LENGTH*0.5f;
      otherPlayer.yPosition=100.0f;
      otherPlayer.state=moveState;
      otherGroup.setPlayer(otherPlayer);
      // other
      commonParticleSet=new ParticleSet(2048);
      currentState=new StartGameState();
      currentBackground=new GameBackground(Tools.color(224.0f),0.1f);
      demoPlay=demo;
      showsInstructionWindow=instruction;
    }
    GameSystem() {
      this(false,false);
    }
    public void run() {
      if(demoPlay) {
        if(currentKeyInput.isZPressed) {
          system=new GameSystem(); // stop demo and start game
          return;
        }
      }
      pushMatrix();
      if(screenShakeValue>0.0f) {
        translate(random(-screenShakeValue,screenShakeValue),random(-screenShakeValue,screenShakeValue));
        screenShakeValue-=50.0f/IDEAL_FRAME_RATE;
      }
      currentBackground.update();
      currentBackground.display();
      currentState.run(this);
      popMatrix();
      if(demoPlay&&showsInstructionWindow) displayDemo();
    }
    public void displayDemo() {
      pushStyle();
      stroke(0.0f);
      strokeWeight(2.0f);
      fill(255.0f,240.0f);
      rect(
        INTERNAL_CANVAS_SIDE_LENGTH*0.5f,
        INTERNAL_CANVAS_SIDE_LENGTH*0.5f,
        INTERNAL_CANVAS_SIDE_LENGTH*0.7f,
        INTERNAL_CANVAS_SIDE_LENGTH*0.6f);
      textFont(smallFont,20.0f);
      textLeading(26.0f);
      textAlign(RIGHT,BASELINE);
      fill(0.0f);
      text("Z key:",280.0f,180.0f);
      text("X key:",280.0f,250.0f);
      text("Arrow key:",280.0f,345.0f);
      textAlign(LEFT);
      text("Weak shot\n (auto aiming)",300.0f,180.0f);
      text("Lethal shot\n (manual aiming,\n  requires charge)",300.0f,250.0f);
      text("Move\n (or aim lethal shot)",300.0f,345.0f);
      textAlign(CENTER);
      text("- Press Z key to start -",INTERNAL_CANVAS_SIDE_LENGTH*0.5f,430.0f);
      text("(Click to hide this window)",INTERNAL_CANVAS_SIDE_LENGTH*0.5f,475.0f);
      popStyle();
      strokeWeight(1.0f);
    }
    public void addSquareParticles(float x,float y,int particleCount,float particleSize,float minSpeed,float maxSpeed,float lifespanSecondValue) {
      final ParticleBuilder builder=system.commonParticleSet.builder
        .type(1) // Square  
        .position(x,y)
        .particleSize(particleSize)
        .particleColor(Tools.color(0.0f))
        .lifespanSecond(lifespanSecondValue);
      for(int i=0;i<particleCount;i++) {
        final Particle newParticle=builder
          .polarVelocity(random(TWO_PI),random(minSpeed,maxSpeed))
          .build();
        system.commonParticleSet.particleList.add(newParticle);
      }
    }
  }
  public final class GameBackground{
    final ArrayList<BackgroundLine> lineList=new ArrayList<BackgroundLine>();
    final float maxAccelerationMagnitude;
    final int lineColor;
    GameBackground(int col,float maxAcc) {
      lineColor=col;
      maxAccelerationMagnitude=maxAcc;
      for(int i=0;i<10;i++) {
        lineList.add(new HorizontalLine());
      }
      for(int i=0;i<10;i++) {
        lineList.add(new VerticalLine());
      }
    }
    public void update() {
      for(BackgroundLine eachLine:lineList) {
        eachLine.update(random(-maxAccelerationMagnitude,maxAccelerationMagnitude));
      }
    }
    public void display() {
      stroke(lineColor);
      for(BackgroundLine eachLine:lineList) {
        eachLine.display();
      }
    }
  }
  abstract class BackgroundLine{
    float position;
    float velocity;
    BackgroundLine(float initialPosition) {
      position=initialPosition;
    }
    public void update(float acceleration) {
      position+=velocity;
      velocity+=acceleration;
      if(position<0.0f||position>getMaxPosition()) velocity=-velocity;
    }
    public abstract void display();
    public abstract float getMaxPosition();
  }
  public final class HorizontalLine extends BackgroundLine{
    HorizontalLine() {
      super(random(INTERNAL_CANVAS_SIDE_LENGTH));
    }
    public void display() {
      line(0.0f,position,INTERNAL_CANVAS_SIDE_LENGTH,position);
    }
    public float getMaxPosition() {
      return INTERNAL_CANVAS_SIDE_LENGTH;
    }
  }
  public final class VerticalLine extends BackgroundLine{
    VerticalLine() {
      super(random(INTERNAL_CANVAS_SIDE_LENGTH));
    }
    public void display() {
      line(position,0.0f,position,INTERNAL_CANVAS_SIDE_LENGTH);
    }
    public float getMaxPosition() {
      return INTERNAL_CANVAS_SIDE_LENGTH;
    }
  }
  abstract class GameSystemState{
    int properFrameCount;
    public void run(GameSystem system) {
      runSystem(system);
      translate(INTERNAL_CANVAS_SIDE_LENGTH*0.5f,INTERNAL_CANVAS_SIDE_LENGTH*0.5f);
      displayMessage(system);
      checkStateTransition(system);
      properFrameCount++;
    }
    public abstract void runSystem(GameSystem system);
    public abstract void displayMessage(GameSystem system);
    public abstract void checkStateTransition(GameSystem system);
  }
  public final class StartGameState extends GameSystemState{
    final int frameCountPerNumber=PApplet.parseInt(IDEAL_FRAME_RATE);
    final float ringSize=200.0f;
    final int ringColor=Tools.color(0.0f);
    final float ringStrokeWeight=5.0f;
    int displayNumber=4;
    public void runSystem(GameSystem system) {
      system.myGroup.update();
      system.otherGroup.update();
      system.myGroup.displayPlayer();
      system.otherGroup.displayPlayer();
    }
    public void displayMessage(GameSystem system) {
      final int currentNumberFrameCount=properFrameCount%frameCountPerNumber;
      if(currentNumberFrameCount==0) displayNumber--;
      if(displayNumber<=0) return;
      fill(ringColor);
      text(displayNumber,0.0f,0.0f);
      rotate(-HALF_PI);
      strokeWeight(3.0f);
      stroke(ringColor);
      noFill();
      arc(0.0f,0.0f,ringSize,ringSize,0.0f,TWO_PI*PApplet.parseFloat(properFrameCount%frameCountPerNumber)/frameCountPerNumber);
      strokeWeight(1.0f);
    }
    public void checkStateTransition(GameSystem system) {
      if(properFrameCount>=frameCountPerNumber*3) {
        final Particle newParticle=system.commonParticleSet.builder
          .type(3) // Ring
          .position(INTERNAL_CANVAS_SIDE_LENGTH*0.5f,INTERNAL_CANVAS_SIDE_LENGTH*0.5f)
          .polarVelocity(0.0f,0.0f)
          .particleSize(ringSize)
          .particleColor(ringColor)
          .weight(ringStrokeWeight)
          .lifespanSecond(1.0f)
          .build();
        system.commonParticleSet.particleList.add(newParticle);
        system.currentState=new PlayGameState();
      }
    }
  }
  public final class PlayGameState extends GameSystemState{
    int messageDurationFrameCount=PApplet.parseInt(IDEAL_FRAME_RATE);
    public void runSystem(GameSystem system) {
      system.myGroup.update();
      system.myGroup.act();
      system.otherGroup.update();
      system.otherGroup.act();
      system.myGroup.displayPlayer();
      system.otherGroup.displayPlayer();
      system.myGroup.displayArrows();
      system.otherGroup.displayArrows();
      checkCollision();
      system.commonParticleSet.update();
      system.commonParticleSet.display();
    }
    public void displayMessage(GameSystem system) {
      if(properFrameCount>=messageDurationFrameCount) return;
      fill(0.0f,255.0f*(1.0f-PApplet.parseFloat(properFrameCount)/messageDurationFrameCount));
      text("Go",0.0f,0.0f);
    }
    public void checkStateTransition(GameSystem system) {
      if(system.myGroup.player.isNull()) {
        system.currentState=new GameResultState("You lose.");
      }else if(system.otherGroup.player.isNull()) {
        system.currentState=new GameResultState("You win.");
      }
    }
    public void checkCollision() {
      final ActorGroup myGroup=system.myGroup;
      final ActorGroup otherGroup=system.otherGroup;
      for(AbstractArrowActor eachMyArrow:myGroup.arrowList) {
        for(AbstractArrowActor eachEnemyArrow:otherGroup.arrowList) {
          if(eachMyArrow.isCollided(eachEnemyArrow)==false) continue;
          breakArrow(eachMyArrow,myGroup);
          breakArrow(eachEnemyArrow,otherGroup);
        }
      }
      if(otherGroup.player.isNull()==false) {
        for(AbstractArrowActor eachMyArrow:myGroup.arrowList) {
          AbstractPlayerActor enemyPlayer=otherGroup.player;
          if(eachMyArrow.isCollided(enemyPlayer)==false) continue;
          if(eachMyArrow.isLethal()) killPlayer(otherGroup.player);
          else thrustPlayerActor(eachMyArrow,(PlayerActor)enemyPlayer);
          breakArrow(eachMyArrow,myGroup);
        }
      }
      if(myGroup.player.isNull()==false) {
        for(AbstractArrowActor eachEnemyArrow:otherGroup.arrowList) {
          if(eachEnemyArrow.isCollided(myGroup.player)==false) continue;
          if(eachEnemyArrow.isLethal()) killPlayer(myGroup.player);
          else thrustPlayerActor(eachEnemyArrow,(PlayerActor)myGroup.player);
          breakArrow(eachEnemyArrow,otherGroup);
        }
      }
    }
    public void killPlayer(AbstractPlayerActor player) {
      system.addSquareParticles(player.xPosition,player.yPosition,50,16.0f,2.0f,10.0f,4.0f);
      player.group.player=new NullPlayerActor();
      system.screenShakeValue=50.0f;
    }
    public void breakArrow(AbstractArrowActor arrow,ActorGroup group) {
      system.addSquareParticles(arrow.xPosition,arrow.yPosition,10,7.0f,1.0f,5.0f,1.0f);
      group.removingArrowList.add(arrow);
    }
    public void thrustPlayerActor(Actor referenceActor,PlayerActor targetPlayerActor) {
      final float relativeAngle=atan2(targetPlayerActor.yPosition-referenceActor.yPosition,targetPlayerActor.xPosition-referenceActor.xPosition);
      final float thrustAngle=relativeAngle+random(-0.5f*HALF_PI,0.5f*HALF_PI);
      targetPlayerActor.xVelocity+=20.0f*cos(thrustAngle);
      targetPlayerActor.yVelocity+=20.0f*sin(thrustAngle);
      targetPlayerActor.state=system.damagedState.entryState(targetPlayerActor);
      system.screenShakeValue+=10.0f;
    }
  }
  public final class GameResultState extends GameSystemState{
    final String resultMessage;
    final int durationFrameCount=PApplet.parseInt(IDEAL_FRAME_RATE);
    GameResultState(String msg) {
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
      fill(0.0f);
      text(resultMessage,0.0f,0.0f);
      if(properFrameCount>durationFrameCount) {
        pushStyle();
        textFont(smallFont,20.0f);
        text("Press X key to reset.",0.0f,80.0f);
        popStyle();
      }
    }
    public void checkStateTransition(GameSystem system) {
      if(system.demoPlay) {
        if(properFrameCount>durationFrameCount*3) {
          newGame(true,system.showsInstructionWindow);
        }
      }else {
        if(properFrameCount>durationFrameCount&&currentKeyInput.isXPressed) {
          newGame(true,true); // back to demoplay with instruction window
        }
      }
    }
  }
  public final class ParticleSet{
    final ArrayList<Particle> particleList;
    final ArrayList<Particle> removingParticleList;
    final ObjectPool<Particle> particlePool;
    final ParticleBuilder builder;
    ParticleSet(int capacity) {
      particlePool=new ObjectPool<Particle>(capacity);
      for(int i=0;i<capacity;i++) {
        particlePool.pool.add(new Particle());
      }
      particleList=new ArrayList<Particle>(capacity);
      removingParticleList=new ArrayList<Particle>(capacity);
      builder=new ParticleBuilder();
    }
    public void update() {
      particlePool.update();
      for(Particle eachParticle:particleList) {
        eachParticle.update();
      }
      if(removingParticleList.size()>=1) {
        for(Particle eachInstance:removingParticleList) {
          particlePool.deallocate(eachInstance);
        }
        particleList.removeAll(removingParticleList);
        removingParticleList.clear();
      }
    }
    public void display() {
      for(Particle eachParticle:particleList) {
        eachParticle.display();
      }
    }
    public Particle allocate() {
      return particlePool.allocate();
    }
  }
  public final class ParticleBuilder{
    int particleTypeNumber;
    float xPosition,yPosition;
    float xVelocity,yVelocity;
    float directionAngle,speed;
    float rotationAngle;
    int displayColor;
    float strokeWeightValue;
    float displaySize;
    int lifespanFrameCount;
    public ParticleBuilder initialize() {
      particleTypeNumber=0;
      xPosition=0.0f;
      yPosition=0.0f;
      xVelocity=0.0f;
      yVelocity=0.0f;
      directionAngle=0.0f;
      speed=0.0f;
      rotationAngle=0.0f;
      displayColor=Tools.color(0.0f);
      strokeWeightValue=1.0f;
      displaySize=10.0f;
      lifespanFrameCount=60;
      return this;
    }
    public ParticleBuilder type(int v) {
      particleTypeNumber=v;
      return this;
    }
    public ParticleBuilder position(float x,float y) {
      xPosition=x;
      yPosition=y;
      return this;
    }
    public ParticleBuilder polarVelocity(float dir,float spd) {
      directionAngle=dir;
      speed=spd;
      xVelocity=spd*cos(dir);
      yVelocity=spd*sin(dir);
      return this;
    }
    public ParticleBuilder rotation(float v) {
      rotationAngle=v;
      return this;
    }
    public ParticleBuilder particleColor(int c) {
      displayColor=c;
      return this;
    }
    public ParticleBuilder weight(float v) {
      strokeWeightValue=v;
      return this;
    }
    public ParticleBuilder particleSize(float v) {
      displaySize=v;
      return this;
    }
    public ParticleBuilder lifespan(int v) {
      lifespanFrameCount=v;
      return this;
    }
    public ParticleBuilder lifespanSecond(float v) {
      lifespan(PApplet.parseInt(v*IDEAL_FRAME_RATE));
      return this;
    }
    public Particle build() {
      final Particle newParticle=system.commonParticleSet.allocate();
      newParticle.particleTypeNumber=this.particleTypeNumber;
      newParticle.xPosition=this.xPosition;
      newParticle.yPosition=this.yPosition;
      newParticle.xVelocity=this.xVelocity;
      newParticle.yVelocity=this.yVelocity;
      newParticle.directionAngle=this.directionAngle;
      newParticle.speed=this.speed;
      newParticle.rotationAngle=this.rotationAngle;
      newParticle.displayColor=this.displayColor;
      newParticle.strokeWeightValue=this.strokeWeightValue;
      newParticle.displaySize=this.displaySize;
      newParticle.lifespanFrameCount=this.lifespanFrameCount;
      return newParticle;
    }
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
  public final class DamagedPlayerActorState extends PlayerActorState{
    PlayerActorState moveState;
    final int durationFrameCount=PApplet.parseInt(0.75f*IDEAL_FRAME_RATE);
    public void act(PlayerActor parentActor) {
      parentActor.damageRemainingFrameCount--;
      if(parentActor.damageRemainingFrameCount<=0) parentActor.state=moveState.entryState(parentActor);
    }
    public void displayEffect(PlayerActor parentActor) {
      noFill();
      stroke(192.0f,64.0f,64.0f,255.0f*PApplet.parseFloat(parentActor.damageRemainingFrameCount)/durationFrameCount);
      ellipse(0.0f,0.0f,64.0f,64.0f);
    }
    public PlayerActorState entryState(PlayerActor parentActor) {
      parentActor.damageRemainingFrameCount=durationFrameCount;
      return this;
    }
    public boolean isDamaged() {
      return true;
    }
  }
  public final class MovePlayerActorState extends PlayerActorState{
    PlayerActorState drawShortbowState,drawLongbowState;
    public void act(PlayerActor parentActor) {
      final AbstractInputDevice input=parentActor.engine.controllingInputDevice;
      parentActor.addVelocity(1.0f*input.horizontalMoveButton,1.0f*input.verticalMoveButton);
      if(input.shotButtonPressed) {
        parentActor.state=drawShortbowState.entryState(parentActor);
        parentActor.aimAngle=getEnemyPlayerActorAngle(parentActor);
        return;
      }
      if(input.longShotButtonPressed) {
        parentActor.state=drawLongbowState.entryState(parentActor);
        parentActor.aimAngle=getEnemyPlayerActorAngle(parentActor);
        return;
      }
    }
    public void displayEffect(PlayerActor parentActor) {}
    public PlayerActorState entryState(PlayerActor parentActor) {
      return this;
    }
  }
  abstract class DrawBowPlayerActorState extends PlayerActorState{
    PlayerActorState moveState;
    public void act(PlayerActor parentActor) {
      final AbstractInputDevice input=parentActor.engine.controllingInputDevice;
      aim(parentActor,input);
      parentActor.addVelocity(0.25f*input.horizontalMoveButton,0.25f*input.verticalMoveButton);
      if(triggerPulled(parentActor)) fire(parentActor);
      if(buttonPressed(input)==false) {
        parentActor.state=moveState.entryState(parentActor);
      }
    }
    public abstract void aim(PlayerActor parentActor,AbstractInputDevice input);
    public abstract void fire(PlayerActor parentActor);
    public abstract boolean buttonPressed(AbstractInputDevice input);
    public abstract boolean triggerPulled(PlayerActor parentActor);
  }
  public final class DrawShortbowPlayerActorState extends DrawBowPlayerActorState{
    final int fireIntervalFrameCount=PApplet.parseInt(IDEAL_FRAME_RATE*0.2f);
    public void aim(PlayerActor parentActor,AbstractInputDevice input) {
      parentActor.aimAngle=getEnemyPlayerActorAngle(parentActor);
    }
    public void fire(PlayerActor parentActor) {
      ShortbowArrow newArrow=new ShortbowArrow();
      final float directionAngle=parentActor.aimAngle;
      newArrow.xPosition=parentActor.xPosition+24.0f*cos(directionAngle);
      newArrow.yPosition=parentActor.yPosition+24.0f*sin(directionAngle);
      newArrow.rotationAngle=directionAngle;
      newArrow.setVelocity(directionAngle,24.0f);
      parentActor.group.addArrow(newArrow);
    }
    public void displayEffect(PlayerActor parentActor) {
      line(0.0f,0.0f,70.0f*cos(parentActor.aimAngle),70.0f*sin(parentActor.aimAngle));
      noFill();
      arc(0.0f,0.0f,100.0f,100.0f,parentActor.aimAngle-QUARTER_PI,parentActor.aimAngle+QUARTER_PI);
    }
    public PlayerActorState entryState(PlayerActor parentActor) {
      return this;
    }
    public boolean buttonPressed(AbstractInputDevice input) {
      return input.shotButtonPressed;
    }
    public boolean triggerPulled(PlayerActor parentActor) {
      return frameCount%fireIntervalFrameCount==0;
    }
  }
  public final class DrawLongbowPlayerActorState extends DrawBowPlayerActorState{
    final float unitAngleSpeed=0.1f*TWO_PI/IDEAL_FRAME_RATE;
    final int chargeRequiredFrameCount=PApplet.parseInt(0.5f*IDEAL_FRAME_RATE);
    final int effectColor=Tools.color(192.0f,64.0f,64.0f);
    final float ringSize=80.0f;
    final float ringStrokeWeight=5.0f;
    public PlayerActorState entryState(PlayerActor parentActor) {
      parentActor.chargedFrameCount=0;
      return this;
    }
    public void aim(PlayerActor parentActor,AbstractInputDevice input) {
      parentActor.aimAngle+=input.horizontalMoveButton*unitAngleSpeed;
    }
    public void fire(PlayerActor parentActor) {
      final float arrowComponentInterval=24.0f;
      final int arrowShaftNumber=5;
      for(int i=0;i<arrowShaftNumber;i++) {
        LongbowArrowShaft newArrow=new LongbowArrowShaft();
        newArrow.xPosition=parentActor.xPosition+i*arrowComponentInterval*cos(parentActor.aimAngle);
        newArrow.yPosition=parentActor.yPosition+i*arrowComponentInterval*sin(parentActor.aimAngle);
        newArrow.rotationAngle=parentActor.aimAngle;
        newArrow.setVelocity(parentActor.aimAngle,64.0f);
        parentActor.group.addArrow(newArrow);
      }
      LongbowArrowHead newArrow=new LongbowArrowHead();
      newArrow.xPosition=parentActor.xPosition+arrowShaftNumber*arrowComponentInterval*cos(parentActor.aimAngle);
      newArrow.yPosition=parentActor.yPosition+arrowShaftNumber*arrowComponentInterval*sin(parentActor.aimAngle);
      newArrow.rotationAngle=parentActor.aimAngle;
      newArrow.setVelocity(parentActor.aimAngle,64.0f);
      final Particle newParticle=system.commonParticleSet.builder
        .type(2) // Line
        .position(parentActor.xPosition,parentActor.yPosition)
        .polarVelocity(0.0f,0.0f)
        .rotation(parentActor.aimAngle)
        .particleColor(Tools.color(192.0f,64.0f,64.0f))
        .lifespanSecond(2.0f)
        .weight(16.0f)
        .build();
      system.commonParticleSet.particleList.add(newParticle);
      parentActor.group.addArrow(newArrow);
      system.screenShakeValue+=10.0f;
      parentActor.chargedFrameCount=0;
      parentActor.state=moveState.entryState(parentActor);
    }
    public void displayEffect(PlayerActor parentActor) {
      noFill();
      stroke(0.0f);
      arc(0.0f,0.0f,100.0f,100.0f,parentActor.aimAngle-QUARTER_PI,parentActor.aimAngle+QUARTER_PI);
      if(hasCompletedLongBowCharge(parentActor)) stroke(effectColor);
      else stroke(0.0f,128.0f);
      line(0.0f,0.0f,800.0f*cos(parentActor.aimAngle),800.0f*sin(parentActor.aimAngle));
      rotate(-HALF_PI);
      strokeWeight(ringStrokeWeight);
      arc(0.0f,0.0f,ringSize,ringSize,0.0f,TWO_PI*min(1.0f,PApplet.parseFloat(parentActor.chargedFrameCount)/chargeRequiredFrameCount));
      strokeWeight(1.0f);
      rotate(+HALF_PI);
      parentActor.chargedFrameCount++;
    }
    public void act(PlayerActor parentActor) {
      super.act(parentActor);
      if(parentActor.chargedFrameCount!=chargeRequiredFrameCount) return;
      final Particle newParticle=system.commonParticleSet.builder
        .type(3) // Ring
        .position(parentActor.xPosition,parentActor.yPosition)
        .polarVelocity(0.0f,0.0f)
        .particleSize(ringSize)
        .particleColor(effectColor)
        .weight(ringStrokeWeight)
        .lifespanSecond(0.5f)
        .build();
      system.commonParticleSet.particleList.add(newParticle);
    }
    public boolean isDrawingLongBow() {
      return true;
    }
    public boolean hasCompletedLongBowCharge(PlayerActor parentActor) {
      return parentActor.chargedFrameCount>=chargeRequiredFrameCount;
    }
    public boolean buttonPressed(AbstractInputDevice input) {
      return input.longShotButtonPressed;
    }
    public boolean triggerPulled(PlayerActor parentActor) {
      return buttonPressed(parentActor.engine.controllingInputDevice)==false&&hasCompletedLongBowCharge(parentActor);
    }
  }
  public final class ShotDisabledInputDevice extends AbstractInputDevice{
    public void operateShotButton(boolean pressed) {}
    public void operateLongShotButton(boolean pressed) {}
  }
  public final class DisabledInputDevice extends AbstractInputDevice{
    public void operateMoveButton(int horizontal,int vertical) {}
    public void operateShotButton(boolean pressed) {}
    public void operateLongShotButton(boolean pressed) {}
  }
  abstract class PlayerEngine{
    final AbstractInputDevice controllingInputDevice;
    PlayerEngine() {
      controllingInputDevice=new InputDevice();
    }
    public abstract void run(PlayerActor player);
  }
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
  public final class ComputerPlayerEngine extends PlayerEngine{
    final int planUpdateFrameCount=10;
    PlayerPlan currentPlan;
    ComputerPlayerEngine() {
      // There shoud be a smarter way!!!
      final MovePlayerPlan move=new MovePlayerPlan();
      final JabPlayerPlan jab=new JabPlayerPlan();
      final KillPlayerPlan kill=new KillPlayerPlan();
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
      if(frameCount%planUpdateFrameCount==0) currentPlan=currentPlan.nextPlan(player);
    }
  }
  abstract class PlayerPlan{
    public abstract void execute(PlayerActor player,AbstractInputDevice input);
    public abstract PlayerPlan nextPlan(PlayerActor player);
  }
  abstract class DefaultPlayerPlan extends PlayerPlan{
    PlayerPlan movePlan,jabPlan,escapePlan,killPlan;
    int horizontalMove,verticalMove;
    boolean shoot;
    public void execute(PlayerActor player,AbstractInputDevice input) {
      input.operateMoveButton(horizontalMove,verticalMove);
      input.operateLongShotButton(false);
    }
    public PlayerPlan nextPlan(PlayerActor player) {
      final AbstractPlayerActor enemy=player.group.enemyGroup.player;
      // Draw longbow if enemy is damaged
      if(enemy.state.isDamaged()) {
        if(random(1.0f)<0.3f) return killPlan;
      }
      // Avoid the nearest arrow
      AbstractArrowActor nearestArrow=null;
      float tmpMinDistancePow2=999999999.0f;
      for(AbstractArrowActor eachArrow:enemy.group.arrowList) {
        final float distancePow2=player.getDistancePow2(eachArrow);
        if(distancePow2<tmpMinDistancePow2) {
          nearestArrow=eachArrow;
          tmpMinDistancePow2=distancePow2;
        }
      }
      if(tmpMinDistancePow2<40000.0f) {
        final float playerAngleInArrowFrame=nearestArrow.getAngle(player);
        float escapeAngle=nearestArrow.directionAngle;
        if(playerAngleInArrowFrame-nearestArrow.directionAngle>0.0f) escapeAngle+=QUARTER_PI+random(QUARTER_PI);
        else escapeAngle-=QUARTER_PI+random(QUARTER_PI);
        final float escapeTargetX=player.xPosition+100.0f*cos(escapeAngle);
        final float escapeTargetY=player.yPosition+100.0f*sin(escapeAngle);
        setMoveDirection(player,escapeTargetX,escapeTargetY,0.0f);
        if(random(1.0f)<0.7f) return movePlan;
        else return jabPlan;
      }
      // Away from enemy
      setMoveDirection(player,enemy);
      if(player.getDistancePow2(enemy)<100000.0f) {
        if(random(1.0f)<0.7f) return movePlan;
        else return jabPlan;
      }
      // If there is nothing special
      if(random(1.0f)<0.2f) return movePlan;
      else return jabPlan;
    }
    public void setMoveDirection(PlayerActor player,AbstractPlayerActor enemy) {
      float targetX,targetY;
      if(enemy.xPosition>INTERNAL_CANVAS_SIDE_LENGTH*0.5f) targetX=random(0.0f,INTERNAL_CANVAS_SIDE_LENGTH*0.5f);
      else targetX=random(INTERNAL_CANVAS_SIDE_LENGTH*0.5f,INTERNAL_CANVAS_SIDE_LENGTH);
      if(enemy.yPosition>INTERNAL_CANVAS_SIDE_LENGTH*0.5f) targetY=random(0.0f,INTERNAL_CANVAS_SIDE_LENGTH*0.5f);
      else targetY=random(INTERNAL_CANVAS_SIDE_LENGTH*0.5f,INTERNAL_CANVAS_SIDE_LENGTH);
      setMoveDirection(player,targetX,targetY,100.0f);
    }
    public void setMoveDirection(PlayerActor player,float targetX,float targetY,float allowance) {
      if(targetX>player.xPosition+allowance) horizontalMove=1;
      else if(targetX<player.xPosition-allowance) horizontalMove=-1;
      else horizontalMove=0;
      if(targetY>player.yPosition+allowance) verticalMove=1;
      else if(targetY<player.yPosition-allowance) verticalMove=-1;
      else verticalMove=0;
    }
  }
  public final class MovePlayerPlan extends DefaultPlayerPlan{
    public void execute(PlayerActor player,AbstractInputDevice input) {
      super.execute(player,input);
      input.operateShotButton(false);
    }
  }
  public final class JabPlayerPlan extends DefaultPlayerPlan{
    public void execute(PlayerActor player,AbstractInputDevice input) {
      super.execute(player,input);
      input.operateShotButton(true);
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
