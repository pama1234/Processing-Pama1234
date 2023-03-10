package pama1234.processing.game.duel;

import pama1234.math.Tools;

public final class GameSystem{
  private final Duel duel;
  public final ActorGroup myGroup,otherGroup;
  public final ParticleSet commonParticleSet;
  public GameSystemState currentState;
  public float screenShakeValue;
  public final DamagedPlayerActorState damagedState;
  public final GameBackground currentBackground;
  public final boolean demoPlay;
  public boolean showsInstructionWindow;
  GameSystem(Duel duel,boolean demo,boolean instruction) {
    this.duel=duel;
    // prepare ActorGroup
    myGroup=new ActorGroup();
    otherGroup=new ActorGroup();
    myGroup.enemyGroup=otherGroup;
    otherGroup.enemyGroup=myGroup;
    // prepare PlayerActorState
    final MovePlayerActorState moveState=new MovePlayerActorState();
    final DrawBowPlayerActorState drawShortbowState=new DrawShortbowPlayerActorState(this.duel);
    final DrawBowPlayerActorState drawLongbowState=new DrawLongbowPlayerActorState(this.duel);
    damagedState=new DamagedPlayerActorState(this.duel);
    moveState.drawShortbowState=drawShortbowState;
    moveState.drawLongbowState=drawLongbowState;
    drawShortbowState.moveState=moveState;
    drawLongbowState.moveState=moveState;
    damagedState.moveState=moveState;
    // prepare PlayerActor
    PlayerEngine myEngine;
    if(demo) myEngine=new ComputerPlayerEngine(this.duel);
    else myEngine=new HumanPlayerEngine(this.duel.currentKeyInput);
    PlayerActor myPlayer=new PlayerActor(this.duel,myEngine,Tools.color(255.0f));
    myPlayer.xPosition=Duel.INTERNAL_CANVAS_SIDE_LENGTH*0.5f;
    myPlayer.yPosition=Duel.INTERNAL_CANVAS_SIDE_LENGTH-100.0f;
    myPlayer.state=moveState;
    myGroup.setPlayer(myPlayer);
    PlayerEngine otherEngine=new ComputerPlayerEngine(this.duel);
    PlayerActor otherPlayer=new PlayerActor(this.duel,otherEngine,Tools.color(0.0f));
    otherPlayer.xPosition=Duel.INTERNAL_CANVAS_SIDE_LENGTH*0.5f;
    otherPlayer.yPosition=100.0f;
    otherPlayer.state=moveState;
    otherGroup.setPlayer(otherPlayer);
    // other
    commonParticleSet=new ParticleSet(this.duel, 2048);
    currentState=new StartGameState(this.duel);
    currentBackground=new GameBackground(this.duel,Tools.color(224.0f),0.1f);
    demoPlay=demo;
    showsInstructionWindow=instruction;
  }
  GameSystem(Duel duel) {
    this(duel,false,false);
  }
  public void run() {
    if(demoPlay) {
      if(this.duel.currentKeyInput.isZPressed) {
        this.duel.system=new GameSystem(this.duel); // stop demo and start game
        return;
      }
    }
    this.duel.pushMatrix();
    if(screenShakeValue>0.0f) {
      this.duel.translate(this.duel.random(-screenShakeValue,screenShakeValue),this.duel.random(-screenShakeValue,screenShakeValue));
      screenShakeValue-=50.0f/Duel.IDEAL_FRAME_RATE;
    }
    currentBackground.update();
    currentBackground.display();
    currentState.run(this);
    this.duel.popMatrix();
    if(demoPlay&&showsInstructionWindow) displayDemo();
  }
  public void displayDemo() {
    this.duel.pushStyle();
    this.duel.stroke(0.0f);
    this.duel.strokeWeight(2.0f);
    this.duel.fill(255.0f,240.0f);
    this.duel.rect(
      Duel.INTERNAL_CANVAS_SIDE_LENGTH*0.5f,
      Duel.INTERNAL_CANVAS_SIDE_LENGTH*0.5f,
      Duel.INTERNAL_CANVAS_SIDE_LENGTH*0.7f,
      Duel.INTERNAL_CANVAS_SIDE_LENGTH*0.6f);
    this.duel.textFont(this.duel.smallFont,20.0f);
    this.duel.textLeading(26.0f);
    this.duel.textAlign(Duel.RIGHT,Duel.BASELINE);
    this.duel.fill(0.0f);
    this.duel.text("Z key:",280.0f,180.0f);
    this.duel.text("X key:",280.0f,250.0f);
    this.duel.text("Arrow key:",280.0f,345.0f);
    this.duel.textAlign(Duel.LEFT);
    this.duel.text("Weak shot\n (auto aiming)",300.0f,180.0f);
    this.duel.text("Lethal shot\n (manual aiming,\n  requires charge)",300.0f,250.0f);
    this.duel.text("Move\n (or aim lethal shot)",300.0f,345.0f);
    this.duel.textAlign(Duel.CENTER);
    this.duel.text("- Press Z key to start -",Duel.INTERNAL_CANVAS_SIDE_LENGTH*0.5f,430.0f);
    this.duel.text("(Click to hide this window)",Duel.INTERNAL_CANVAS_SIDE_LENGTH*0.5f,475.0f);
    this.duel.popStyle();
    this.duel.strokeWeight(1.0f);
  }
  public void addSquareParticles(float x,float y,int particleCount,float particleSize,float minSpeed,float maxSpeed,float lifespanSecondValue) {
    final ParticleBuilder builder=this.duel.system.commonParticleSet.builder
      .type(1) // Square  
      .position(x,y)
      .particleSize(particleSize)
      .particleColor(Tools.color(0.0f))
      .lifespanSecond(lifespanSecondValue);
    for(int i=0;i<particleCount;i++) {
      final Particle newParticle=builder
        .polarVelocity(this.duel.random(Duel.TWO_PI),this.duel.random(minSpeed,maxSpeed))
        .build();
      this.duel.system.commonParticleSet.particleList.add(newParticle);
    }
  }
}