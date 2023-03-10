package pama1234.processing.game.duel.util.state;

import pama1234.processing.game.duel.AbstractArrowActor;
import pama1234.processing.game.duel.AbstractPlayerActor;
import pama1234.processing.game.duel.Actor;
import pama1234.processing.game.duel.ActorGroup;
import pama1234.processing.game.duel.Duel;
import pama1234.processing.game.duel.GameSystem;
import pama1234.processing.game.duel.NullPlayerActor;
import pama1234.processing.game.duel.PlayerActor;
import processing.core.PApplet;

public final class PlayGameState extends GameSystemState{
  public PlayGameState(Duel duel) {
    super(duel);
  }
  public int messageDurationFrameCount=PApplet.parseInt(Duel.IDEAL_FRAME_RATE);
  @Override
  public void updateSystem(GameSystem system) {
    system.myGroup.update();
    system.myGroup.act();
    system.otherGroup.update();
    system.otherGroup.act();
    //---
    checkCollision();
    system.commonParticleSet.update();
  }
  @Override
  public void displaySystem(GameSystem system) {
    system.myGroup.displayPlayer();
    system.otherGroup.displayPlayer();
    system.myGroup.displayArrows();
    system.otherGroup.displayArrows();
    system.commonParticleSet.display();
  }
  @Override
  public void displayMessage(GameSystem system) {
    if(properFrameCount>=messageDurationFrameCount) return;
    this.duel.fill(0.0f,255.0f*(1.0f-PApplet.parseFloat(properFrameCount)/messageDurationFrameCount));
    this.duel.text("Go",0.0f,0.0f);
  }
  @Override
  public void checkStateTransition(GameSystem system) {
    if(system.myGroup.player.isNull()) {
      system.currentState=new GameResultState(this.duel,"You lose.");
    }else if(system.otherGroup.player.isNull()) {
      system.currentState=new GameResultState(this.duel,"You win.");
    }
  }
  public void checkCollision() {
    final ActorGroup myGroup=this.duel.system.myGroup;
    final ActorGroup otherGroup=this.duel.system.otherGroup;
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
    this.duel.system.addSquareParticles(player.xPosition,player.yPosition,50,16.0f,2.0f,10.0f,4.0f);
    player.group.player=new NullPlayerActor();
    this.duel.system.screenShakeValue=50.0f;
  }
  public void breakArrow(AbstractArrowActor arrow,ActorGroup group) {
    this.duel.system.addSquareParticles(arrow.xPosition,arrow.yPosition,10,7.0f,1.0f,5.0f,1.0f);
    group.removingArrowList.add(arrow);
  }
  public void thrustPlayerActor(Actor referenceActor,PlayerActor targetPlayerActor) {
    final float relativeAngle=Duel.atan2(targetPlayerActor.yPosition-referenceActor.yPosition,targetPlayerActor.xPosition-referenceActor.xPosition);
    final float thrustAngle=relativeAngle+this.duel.random(-0.5f*Duel.HALF_PI,0.5f*Duel.HALF_PI);
    targetPlayerActor.xVelocity+=20.0f*Duel.cos(thrustAngle);
    targetPlayerActor.yVelocity+=20.0f*Duel.sin(thrustAngle);
    targetPlayerActor.state=this.duel.system.damagedState.entryState(targetPlayerActor);
    this.duel.system.screenShakeValue+=10.0f;
  }
}