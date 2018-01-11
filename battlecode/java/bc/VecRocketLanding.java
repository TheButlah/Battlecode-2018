/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.12
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package bc;

public class VecRocketLanding {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  protected VecRocketLanding(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(VecRocketLanding obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        bcJNI.delete_VecRocketLanding(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public VecRocketLanding() {
    this(bcJNI.new_VecRocketLanding(), true);
  }

  public String toString() {
    return bcJNI.VecRocketLanding_toString(swigCPtr, this);
  }

  public VecRocketLanding clone() {
    long cPtr = bcJNI.VecRocketLanding_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new VecRocketLanding(cPtr, false);
  }

  public long size() {
    return bcJNI.VecRocketLanding_size(swigCPtr, this);
  }

  public RocketLanding get(long index) {
    long cPtr = bcJNI.VecRocketLanding_get(swigCPtr, this, index);
    return (cPtr == 0) ? null : new RocketLanding(cPtr, false);
  }

}
