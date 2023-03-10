package pama1234.processing.game.duel;

import pama1234.processing.game.duel.util.ObjectPool;

public interface Poolable{
  public boolean isAllocated();
  public void setAllocated(boolean indicator);
  public ObjectPool getBelongingPool();
  public void setBelongingPool(ObjectPool pool);
  public int getAllocationIdentifier(); // -1 : not allocated
  public void setAllocationIdentifier(int id);
  public void initialize();
}