package pama1234.processing.game.duel;

import java.util.ArrayList;

import pama1234.processing.game.duel.util.ObjectPool;

public final class ParticleSet{
  /**
   *
   */
  private final Duel duel;
  final ArrayList<Particle> particleList;
  final ArrayList<Particle> removingParticleList;
  final ObjectPool<Particle> particlePool;
  final ParticleBuilder builder;
  ParticleSet(Duel duel, int capacity) {
    this.duel=duel;
    particlePool=new ObjectPool<Particle>(capacity);
    for(int i=0;i<capacity;i++) {
      particlePool.pool.add(new Particle(this.duel));
    }
    particleList=new ArrayList<Particle>(capacity);
    removingParticleList=new ArrayList<Particle>(capacity);
    builder=new ParticleBuilder(this.duel);
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