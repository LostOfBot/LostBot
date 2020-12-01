package com.lostofthought.util;

import com.lostofthought.util.functional.Optional;

import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class ForEach {
  static <T, R> Optional<R> BreadthFirst(T container, Func.Func1<T, Queue<T>> getNext, Func.Func1<T, Optional<R>> func){
    Queue<T> q = new LinkedList<>();
    Set<T> d = new HashSet<>();
    q.add(container);
    d.add(container);
    while(!q.isEmpty()){
      T current = q.remove();
      Optional<R> ret = func.apply(current);
      if(Optional.is(ret)){
        return ret;
      }
      getNext.apply(current).forEach(t -> {
        if(!d.contains(t)){
          d.add(t);
          q.add(t);
        }
      });
    }
    return Optional.empty();
  }
}
