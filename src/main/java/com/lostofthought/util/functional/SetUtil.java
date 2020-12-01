package com.lostofthought.util.functional;

import com.lostofthought.util.Cast;
import com.lostofthought.util.Exceptional;
import com.lostofthought.util.Func;

import java.util.HashSet;
import java.util.Set;

public class SetUtil {
  public static <T, Y> Y reduce(Y acc, Func.Func2<Y, T, Y> fun, Set<T> s){
    for (T t : s) {
      acc = fun.apply(acc, t);
    }
    return acc;
  }
  public static <T, Y> Func.Func3<Y, Func.Func2<Y, T, Y>, Set<T>, Y> reduce(){
    return SetUtil::reduce;
  }
  public static <T, Y> Set<Y> map(Func.Func1<T, Y> f, Set<T> s){
    return SetUtil.reduce(
      Either.<Exception, Set<Y>, Set<Y>>reduce(
        e -> new HashSet<>(),
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
  public static <T, Y> Func.Func2<Func.Func1<T, Y>, Set<T>, Set<Y>> map(){
    return SetUtil::map;
  }
  public static <T> Set<T> filter(Func.Predicate<T> predicate, Set<T> s){
    Set<T> ret = Either.<Exception, Set<T>, Set<T>>reduce(
      e -> new HashSet<>(),
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
  public static <T> Func.Func2<Func.Predicate<T>, Set<T>, Set<T>> filter(){
    return SetUtil::filter;
  }
}
