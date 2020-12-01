package com.lostofthought.util;

import com.lostofthought.util.functional.Either;
import com.lostofthought.util.functional.Optional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Util {
  static char[] RangeChar(char begin, char end){
    char[] ret = new char[end - begin];
    int k = 0;
    for (char i = begin; i < end; i++) {
      ret[k] = i;
      k++;
    }
    return ret;
  }
  static <T> T ListReduce(Func.Func2<T, T, T> f, List<T> list){
    T acc = list.get(0);
    for (int i = 1; i < list.size(); i++) {
      acc = f.apply(acc, list.get(i));
    }
    return acc;
  }
  static <T, R> List<R> ListMap(Func.Func1<T, R> f, List<T> list){
    List<R> ret = new ArrayList<>();
    list.forEach((T t) -> ret.add(f.apply(t)));
    return ret;
  }
  @SafeVarargs
  static <T> List<T> ListFrom(T... array){
    return Arrays.asList(array);
  }
  static Byte[] Box(byte[] b){
    Byte[] ret = new Byte[b.length];
    for (int i = 0; i < b.length; i++) {
      ret[i] = b[i];
    }
    return ret;
  }
  static Short[] Box(short[] b){
    Short[] ret = new Short[b.length];
    for (int i = 0; i < b.length; i++) {
      ret[i] = b[i];
    }
    return ret;
  }
  static Integer[] Box(int[] b){
    Integer[] ret = new Integer[b.length];
    for (int i = 0; i < b.length; i++) {
      ret[i] = b[i];
    }
    return ret;
  }
  static Long[] Box(long[] b){
    Long[] ret = new Long[b.length];
    for (int i = 0; i < b.length; i++) {
      ret[i] = b[i];
    }
    return ret;
  }
  static Float[] Box(float[] b){
    Float[] ret = new Float[b.length];
    for (int i = 0; i < b.length; i++) {
      ret[i] = b[i];
    }
    return ret;
  }
  static Double[] Box(double[] b){
    Double[] ret = new Double[b.length];
    for (int i = 0; i < b.length; i++) {
      ret[i] = b[i];
    }
    return ret;
  }
  static Boolean[] Box(boolean[] b){
    Boolean[] ret = new Boolean[b.length];
    for (int i = 0; i < b.length; i++) {
      ret[i] = b[i];
    }
    return ret;
  }
  static Character[] Box(char[] b){
    Character[] ret = new Character[b.length];
    for (int i = 0; i < b.length; i++) {
      ret[i] = b[i];
    }
    return ret;
  }
  static Byte Box(byte i){
    return i;
  }
  static Short Box(short i){
    return i;
  }
  static Integer Box(int i){
    return i;
  }
  static Long Box(long i){
    return i;
  }
  static Float Box(float i){
    return i;
  }
  static Double Box(double i){
    return i;
  }
  static Boolean Box(boolean i){
    return i;
  }
  static Character Box(char i){
    return i;
  }
}
