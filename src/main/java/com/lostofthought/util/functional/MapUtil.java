package com.lostofthought.util.functional;

import com.lostofthought.util.Cast;
import com.lostofthought.util.Exceptional;
import com.lostofthought.util.Func;

import java.util.*;

public class MapUtil {
  public static <K, V, Y> Y reduce(Y acc, Func.Func2<Y, Pair<K, V>, Y> fun, Map<K, V> m){
    for (Map.Entry<K, V> entry : m.entrySet()) {
      K k = entry.getKey();
      V v = entry.getValue();
      acc = fun.apply(acc, Pair.from(k, v));
    }
    return acc;
  }
  public static <K, V, Y> Func.Func3<Y, Func.Func2<Y, Pair<K, V>, Y>, Map<K, V>, Y> reduce(){
    return MapUtil::reduce;
  }
  public static <K, V, V2> Map<K, V2> map(Func.Func1<Pair<K, V>, Pair<K, V2>> f, Map<K, V> m){
    return MapUtil.reduce(
        Either.<Exception, Map<K, V2>, Map<K, V2>>reduce(
            e -> new HashMap<K, V2>(),
            o -> o,
            Exceptional.eitherOfExceptional(() -> Cast.unchecked(m.getClass().newInstance()))
        ),
        (Map<K, V2> acc, Pair<K, V> t) -> {
          Pair<K, V2> temp = f.apply(t);
          acc.put(temp._1, temp._2);
          return acc;
        },
        m
    );
  }
  public static <K, V, V2> Map<K, V2> map(Func.Func2<K, V, V2> f, Map<K, V> m){
    return MapUtil.reduce(
        Either.<Exception, Map<K, V2>, Map<K, V2>>reduce(
            e -> new HashMap<K, V2>(),
            o -> o,
            Exceptional.eitherOfExceptional(() -> Cast.unchecked(m.getClass().newInstance()))
        ),
        (Map<K, V2> acc, Pair<K, V> t) -> {
          V2 temp = f.apply(t._1, t._2);
          acc.put(t._1, temp);
          return acc;
        },
        m
    );
  }
  public static <K, V, V2> Func.Func2<Func.Func1<Pair<K, V>, Pair<K, V2>>, Map<K, V>, Map<K, V2>> map(){
    return MapUtil::map;
  }
  public static <K, V> Map<K, V> filter(Func.Predicate<Pair<K, V>> predicate, Map<K, V> s){
    Map<K, V> ret = Either.<Exception, Map<K, V>, Map<K, V>>reduce(
      e -> new HashMap<K, V>(),
      o -> o,
      Exceptional.eitherOfExceptional(() -> Cast.unchecked(s.getClass().newInstance()))
    );
    s.forEach((k, v) -> {
      if(predicate.apply(Pair.from(k, v))){
        ret.put(k, v);
      }
    });
    return ret;
  }
  public static <K, V> Func.Func2<Func.Predicate<Pair<K, V>>, Map<K, V>, Map<K, V>> filter(){
    return MapUtil::filter;
  }
  public static <K, V> V apply(K key, Map<K, V> map){
    return map.get(key);
  }
  public static class WrappedMap<K, V> {
    private final Map<K, V> value;
    protected WrappedMap(Map<K, V> value){
      this.value = value;
    }
    public WrappedMap<K, V> filter(Func.Predicate<Pair<K, V>> p){
      return MapUtil.asWrapped(MapUtil.filter(p, value));
    }
    public <A> A reduce(A acc, Func.Func2<A, Pair<K, V>, A> f){
      return MapUtil.reduce(acc, f, value);
    }
    public <V2> WrappedMap<K, V2> map(Func.Func1<Pair<K, V>, Pair<K, V2>> f){
      return MapUtil.asWrapped(MapUtil.map(f, value));
    }
    public <V2> WrappedMap<K, V2> map(Func.Func2<K, V, V2> f){
      return MapUtil.asWrapped(MapUtil.map(f, value));
    }
    public V apply(K key) {
      return MapUtil.apply(key, value);
    }
    public Map<K, V> unwrap() {
      return value;
    }
  }
  public static <K, V> MapUtil.WrappedMap<K, V> asWrapped(Map<K, V> value){
    return new MapUtil.WrappedMap<>(value);
  }
}
