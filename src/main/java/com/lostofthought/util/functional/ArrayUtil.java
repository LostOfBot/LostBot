package com.lostofthought.util.functional;

import com.lostofthought.util.Cast;
import com.lostofthought.util.Func;

import java.util.*;

public class ArrayUtil {
  public static <T> Set<T> asSet(T[] array){
    Set<T> ret = new HashSet<>();
    Collections.addAll(ret, array);
    return ret;
  }
  public static <T> List<T> asList(T[] array){
    List<T> ret = new ArrayList<T>();
    Collections.addAll(ret, array);
    return ret;
  }
  public static <T> T[] filter(Func.Predicate<T> p, T[] array){
    List<T> ret = new ArrayList<>();
    for (T t : array) {
      if(p.apply(t)){
        ret.add(t);
      }
    }
    return Cast.unchecked(ret.toArray());
  }
  public static <T> Func.Func2<Func.Predicate<T>, T[], T[]> filter(){
    return ArrayUtil::filter;
  }
  public static <T, A> A reduce(A acc, Func.Func2<A, T, A> f, T[] s){
    for (T t : s) {
      acc = f.apply(acc, t);
    }
    return acc;
  }
  public static <T, A> Func.Func3<A, Func.Func2<A, T, A>, T[], A> reduce() {
    return ArrayUtil::reduce;
  }
  public static <T, Y> Y[] map(Func.Func1<T, Y> f, T[] s){
    return Func.pipe(
      reduce(new ArrayList<Y>(), (acc, t) -> {
        acc.add(f.apply(t));
        return acc;
      }, s).toArray(),
      Cast::unchecked
    );
  }
  public static <T, Y> Func.Func2<Func.Func1<T, Y>, T[], Y[]> map() {
    return ArrayUtil::map;
  }
  public static <T> T apply(int i, T[] array){
    return array[i];
  }
  public static <T> Func.Func2<Integer, T[], T> apply(){
    return ArrayUtil::apply;
  }
  public static <T> T from(T _default, int i, T[] array){
    return (i < array.length)
      ? array[i]
      : _default;
  }
  public static <T> Func.Func3<T, Integer, T[], T> from(){
    return ArrayUtil::from;
  }
  public static <T> Optional<T> optionalFrom(int i, T[] array){
    return ArrayUtil.hasIndex(i, array)
      ? Optional.of(array[i])
      : Optional.empty();
  }
  public static <T> Func.Func2<Integer, T[], Optional<T>> optionalFrom(){
    return ArrayUtil::optionalFrom;
  }
  public static <T> boolean hasIndex(int i, T[] array){
    return i < array.length && i > 0;
  }
  public static <T> boolean hasntIndex(int i, T[] array){
    return i >= array.length || i < 0;
  }
  public static class WrappedArray<T> extends ArrayUtil implements Func.Func1<Integer, T> {
    private final T[] value;
    protected WrappedArray(T[] value){
      this.value = value;
    }
    public Set<T> asSet(){
      return ArrayUtil.asSet(value);
    }
    public List<T> asList(){
      return ArrayUtil.asList(value);
    }
    public WrappedArray<T> filter(Func.Predicate<T> p){
      return ArrayUtil.asWrapped(ArrayUtil.filter(p, value));
    }
    public <A> A reduce(A acc, Func.Func2<A, T, A> f){
      return ArrayUtil.reduce(acc, f, value);
    }
    public <Y> WrappedArray<Y> map(Func.Func1<T, Y> f){
      return ArrayUtil.asWrapped(ArrayUtil.map(f, value));
    }
    public T apply(int i) {
      return ArrayUtil.apply(i, value);
    }
    @Override
    public T apply(Integer i) {
      return ArrayUtil.apply(i, value);
    }
    public T[] unwrap() {
      return value;
    }
  }
  public static <T> WrappedArray<T> asWrapped(T[] value){
    return new WrappedArray<>(value);
  }
}
