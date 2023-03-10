package pama1234.math.physics;

import java.nio.ByteBuffer;

import pama1234.nio.ByteData;

public class MassVar implements ByteData{
  public float pos,vel;
  public float f=0.8f;
  public MassVar(float in) {
    pos=in;
  }
  public void update() {
    pos+=vel;
    if(f!=1) vel*=f;
  }
  @Override
  public void fromData(ByteBuffer in,int offset,int size) {
    f=in.getFloat(offset);
    pos=in.getFloat(offset+=ByteData.FLOAT_SIZE);
    vel=in.getFloat(offset+=ByteData.FLOAT_SIZE);
  }
  @Override
  public ByteBuffer toData(ByteBuffer in,int offset) {
    in.putFloat(offset,f);
    in.putFloat(offset+=ByteData.FLOAT_SIZE,pos);
    in.putFloat(offset+=ByteData.FLOAT_SIZE,vel);
    return in;
  }
  @Override
  public int bufferSize() {
    return ByteData.FLOAT_SIZE*3;
  }
  public boolean toNumber() {
    if(Float.isInfinite(pos)||Float.isNaN(pos)) {
      pos=0;
      return true;
    }
    if(Float.isInfinite(vel)||Float.isNaN(vel)) {
      vel=0;
      return true;
    }
    return false;
  }
}
