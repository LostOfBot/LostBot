package com.lostofthought.util.functional;

import com.lostofthought.util.Cast;
import com.lostofthought.util.Exceptional;
import com.lostofthought.util.Func;

import java.util.ArrayList;
import java.util.List;
import java.util.List;

public class ListUtil {
  public static <T, Y> Y reduce(Y acc, Func.Func2<Y, T, Y> fun, List<T> s){
    for (T t : s) {
      acc = fun.apply(acc, t);
    }
    return acc;
  }
  public static <T, Y> Func.Func3<Y, Func.Func2<Y, T, Y>, List<T>, Y> reduce(){
    return ListUtil::reduce;
  }
  public static <T> T apply(int i, List<T> s){
    return s.get(i);
  }
  public static <T> Func.Func2<Integer, List<T>, T> apply(){
    return ListUtil::apply;
  }
  public static <T, Y> List<Y> map(Func.Func1<T, Y> f, List<T> s){
    return ListUtil.reduce(
      Either.<Exception, List<Y>, List<Y>>reduce(
        e -> new ArrayList<>(),
        o -> o,
        Exceptional.eitherOfExceptional(() -> Cast.unchecked(s.getClass().newInstance()))
      ),
      (acc, t) -> {
        acc.add(f.apply(t));
        return acc;
      },
      s
    );
  }
  public static <T, Y> Func.Func2<Func.Func1<T, Y>, List<T>, List<Y>> map(){
    return ListUtil::map;
  }
  public static <T> List<T> filter(Func.Predicate<T> predicate, List<T> s){
    List<T> ret = Either.<Exception, List<T>, List<T>>reduce(
      e -> new ArrayList<>(),
      o -> o,
      Exceptional.eitherOfExceptional(() -> Cast.unchecked(s.getClass().newInstance()))
    );
    s.forEach(t -> {
      if(predicate.apply(t)){
        ret.add(t);
      }
    });
    return ret;
  }
  public static <T> Func.Func2<Func.Predicate<T>, List<T>, List<T>> filter(){
    return ListUtil::filter;
  }
  public static class WrappedList<T> {
    private final List<T> value;
    protected WrappedList(List<T> value){
      this.value = value;
    }
    public ListUtil.WrappedList<T> filter(Func.Predicate<T> p){
      return ListUtil.asWrapped(ListUtil.filter(p, value));
    }
    public <A> A reduce(A acc, Func.Func2<A, T, A> f){
      return ListUtil.reduce(acc, f, value);
    }
    public <T2> ListUtil.WrappedList<T2> map(Func.Func1<T, T2> f){
      return ListUtil.asWrapped(ListUtil.map(f, value));
    }
    public T apply(int i) {
      return ListUtil.apply(i, this.value);
    }
    public List<T> unwrap() {
      return value;
    }
  }
  public static <T> ListUtil.WrappedList<T> asWrapped(List<T> value){
    return new ListUtil.WrappedList<>(value);
  }
}
