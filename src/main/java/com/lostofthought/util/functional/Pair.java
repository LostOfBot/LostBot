package com.lostofthought.util.functional;

import com.lostofthought.util.Func;

import java.util.Objects;

public class Pair<A, B> {
  public final A _1;
  public final B _2;
  public Pair(A first, B second) {
    this._1 = first;
    this._2 = second;
  }
  public static <A, B> Pair<A, B> from(A _1, B _2){
    return new Pair<>(_1, _2);
  }
  public int hashCode() {
    return Objects.hash(_1, _2);
  }

  public boolean equals(Object other) {
    if (other instanceof Pair) {
      Pair<?, ?> o = (Pair<?,?>) other;
      return _1.equals(o._1) && _2.equals(o._2);
    }

    return false;
  }

  public <B2> Pair<A, B2> map(Func.Func1<B, B2> fun){
    return Pair.from(_1, fun.apply(_2));
  }

  public String toString(){
    return "Pair<" + _1.getClass().getSimpleName() + "," + _2.getClass().getSimpleName() + ">{" + _1 + ", " + _2 + "}";
  }
}