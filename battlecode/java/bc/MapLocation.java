/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.12
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package bc;

public class MapLocation {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  protected MapLocation(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(MapLocation obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        bcJNI.delete_MapLocation(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public MapLocation(Planet planet, int x, int y) {
    this(bcJNI.new_MapLocation(planet.swigValue(), x, y), true);
  }

  public MapLocation add(Direction direction) {
    long cPtr = bcJNI.MapLocation_add(swigCPtr, this, direction.swigValue());
    return (cPtr == 0) ? null : new MapLocation(cPtr, true);
  }

  public MapLocation subtract(Direction direction) {
    long cPtr = bcJNI.MapLocation_subtract(swigCPtr, this, direction.swigValue());
    return (cPtr == 0) ? null : new MapLocation(cPtr, true);
  }

  public MapLocation addMultiple(Direction direction, int multiple) {
    long cPtr = bcJNI.MapLocation_addMultiple(swigCPtr, this, direction.swigValue(), multiple);
    return (cPtr == 0) ? null : new MapLocation(cPtr, true);
  }

  public MapLocation translate(int dx, int dy) {
    long cPtr = bcJNI.MapLocation_translate(swigCPtr, this, dx, dy);
    return (cPtr == 0) ? null : new MapLocation(cPtr, true);
  }

  public long distanceSquaredTo(MapLocation o) {
    return bcJNI.MapLocation_distanceSquaredTo(swigCPtr, this, MapLocation.getCPtr(o), o);
  }

  public Direction directionTo(MapLocation o) {
    return Direction.swigToEnum(bcJNI.MapLocation_directionTo(swigCPtr, this, MapLocation.getCPtr(o), o));
  }

  public boolean isAdjacentTo(MapLocation o) {
    return bcJNI.MapLocation_isAdjacentTo(swigCPtr, this, MapLocation.getCPtr(o), o);
  }

  public boolean isWithinRange(long range, MapLocation o) {
    return bcJNI.MapLocation_isWithinRange(swigCPtr, this, range, MapLocation.getCPtr(o), o);
  }

  public String toString() {
    return bcJNI.MapLocation_toString(swigCPtr, this);
  }

  public MapLocation clone() {
    long cPtr = bcJNI.MapLocation_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new MapLocation(cPtr, true);
  }

  public boolean equals(MapLocation other) {
    return bcJNI.MapLocation_equals(swigCPtr, this, MapLocation.getCPtr(other), other);
  }

  public String toJson() {
    return bcJNI.MapLocation_toJson(swigCPtr, this);
  }

  public void setPlanet(Planet value) {
    bcJNI.MapLocation_planet_set(swigCPtr, this, value.swigValue());
  }

  public Planet getPlanet() {
    return Planet.swigToEnum(bcJNI.MapLocation_planet_get(swigCPtr, this));
  }

  public void setX(int value) {
    bcJNI.MapLocation_x_set(swigCPtr, this, value);
  }

  public int getX() {
    return bcJNI.MapLocation_x_get(swigCPtr, this);
  }

  public void setY(int value) {
    bcJNI.MapLocation_y_set(swigCPtr, this, value);
  }

  public int getY() {
    return bcJNI.MapLocation_y_get(swigCPtr, this);
  }

}
