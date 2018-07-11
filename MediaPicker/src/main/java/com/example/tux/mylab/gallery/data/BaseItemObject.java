package com.example.tux.mylab.gallery.data;

import android.support.annotation.Keep;

/**
 * Created by dev22 on 5/16/17.
 */
@Keep
public class BaseItemObject {

  public static final int TYPE_HEADER = 0;
  static final int TYPE_ITEM = 1;
  /**
   * values: {@link #TYPE_HEADER}, {@link #TYPE_ITEM}
   */
  private final int type;

  protected BaseItemObject(int type) {
    this.type = type;
  }

  public int getType() {
    return type;
  }
}
