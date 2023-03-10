package pama1234.processing.game.duel;

import pama1234.processing.game.duel.util.input.AbstractInputDevice;

public abstract class DefaultPlayerPlan extends PlayerPlan{
  public final Duel duel;
  public DefaultPlayerPlan(Duel duel) {
    this.duel=duel;
  }
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
      if(this.duel.random(1.0f)<0.3f) return killPlan;
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
      if(playerAngleInArrowFrame-nearestArrow.directionAngle>0.0f) escapeAngle+=Duel.QUARTER_PI+this.duel.random(Duel.QUARTER_PI);
      else escapeAngle-=Duel.QUARTER_PI+this.duel.random(Duel.QUARTER_PI);
      final float escapeTargetX=player.xPosition+100.0f*Duel.cos(escapeAngle);
      final float escapeTargetY=player.yPosition+100.0f*Duel.sin(escapeAngle);
      setMoveDirection(player,escapeTargetX,escapeTargetY,0.0f);
      if(this.duel.random(1.0f)<0.7f) return movePlan;
      else return jabPlan;
    }
    // Away from enemy
    setMoveDirection(player,enemy);
    if(player.getDistancePow2(enemy)<100000.0f) {
      if(this.duel.random(1.0f)<0.7f) return movePlan;
      else return jabPlan;
    }
    // If there is nothing special
    if(this.duel.random(1.0f)<0.2f) return movePlan;
    else return jabPlan;
  }
  public void setMoveDirection(PlayerActor player,AbstractPlayerActor enemy) {
    float targetX,targetY;
    if(enemy.xPosition>Duel.INTERNAL_CANVAS_SIDE_LENGTH*0.5f) targetX=this.duel.random(0.0f,Duel.INTERNAL_CANVAS_SIDE_LENGTH*0.5f);
    else targetX=this.duel.random(Duel.INTERNAL_CANVAS_SIDE_LENGTH*0.5f,Duel.INTERNAL_CANVAS_SIDE_LENGTH);
    if(enemy.yPosition>Duel.INTERNAL_CANVAS_SIDE_LENGTH*0.5f) targetY=this.duel.random(0.0f,Duel.INTERNAL_CANVAS_SIDE_LENGTH*0.5f);
    else targetY=this.duel.random(Duel.INTERNAL_CANVAS_SIDE_LENGTH*0.5f,Duel.INTERNAL_CANVAS_SIDE_LENGTH);
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