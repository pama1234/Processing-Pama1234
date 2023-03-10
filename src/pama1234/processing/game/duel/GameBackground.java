package pama1234.processing.game.duel;

import java.util.ArrayList;

public final class GameBackground{
  public final Duel duel;
  public final ArrayList<BackgroundLine> lineList=new ArrayList<BackgroundLine>();
  public final float maxAccelerationMagnitude;
  public final int lineColor;
  GameBackground(Duel duel,int col,float maxAcc) {
    this.duel=duel;
    lineColor=col;
    maxAccelerationMagnitude=maxAcc;
    for(int i=0;i<10;i++) {
      lineList.add(new HorizontalLine(this.duel));
    }
    for(int i=0;i<10;i++) {
      lineList.add(new VerticalLine(duel));
    }
  }
  public void update() {
    for(BackgroundLine eachLine:lineList) {
      eachLine.update(this.duel.random(-maxAccelerationMagnitude,maxAccelerationMagnitude));
    }
  }
  public void display() {
    this.duel.stroke(lineColor);
    for(BackgroundLine eachLine:lineList) {
      eachLine.display();
    }
  }
}