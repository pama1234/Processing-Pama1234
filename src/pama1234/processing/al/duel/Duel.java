package pama1234.processing.al.duel;

import static pama1234.processing.al.duel.util.DuelCenter.borderSize;
import static pama1234.processing.al.duel.util.DuelCenter.boxR;

import pama1234.math.physics.MassPoint;
import pama1234.math.physics.PathPoint;
import pama1234.processing.al.duel.app.MainApp;
import pama1234.processing.al.duel.util.DuelCenter;
import pama1234.processing.al.duel.util.Player;
import pama1234.processing.al.duel.util.control.hist.Control;
import pama1234.processing.util.app.UtilApp;
import pama1234.processing.util.element.PointEntity;
import pama1234.processing.util.wrapper.PointCenter;

public class Duel extends PointCenter<PointEntity<PathPoint>>{
  DuelCenter c;
  Player a,b;
  Control<MassPoint> ctrl;
  public Duel(UtilApp p) {
    super(p);
    c=new DuelCenter(p);
    {
      a=new Player(c,c.w/4,c.h/2);
      b=new Player(c,c.w/4*3,c.h/2);
      c.c.add.add(a);
      c.c.add.add(b);
      c.c.refresh();
    }
    add.add(c);
    ctrl=new Control<MassPoint>(p,-boxR-borderSize*2+1,boxR+borderSize*2+1,1,a.point);
    ctrl.player=a;
    ctrl.comp=b;
    add.add(ctrl);
  }
  public static void main(String[] args) {
    System.setProperty("sun.java2d.uiScale","1");
    new MainApp().run();
  }
}
