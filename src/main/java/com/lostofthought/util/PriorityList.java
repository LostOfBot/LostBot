package com.lostofthought.util;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class PriorityList<T> implements Iterable<T>{
  private final TreeMap<Integer, ArrayList<T>> lists;
  public PriorityList(){
    lists = new TreeMap<>(Comparator.comparingInt(v -> -1 * v));
  }
  public void add(int priority, T elem){
    AtomicBoolean exit = new AtomicBoolean(false);
    lists.forEach((p, l) -> {
      if(l.contains(elem)){
        exit.set(true);
        //throw new RuntimeException("Element already in list at priority " + p);
      }
    });
    if(exit.get()){
      return;
    }
    ArrayList<T> priorityList
        = lists.computeIfAbsent(priority, k -> new ArrayList<>());
    priorityList.add(elem);
  }
  public void remove(T elem){
    lists.forEach((p, l) -> {
      l.remove(elem);
      if(l.size() == 0){
        lists.remove(p);
      }
    });
  }

  public List<T> asList(){
    return lists.values().stream().flatMap(List::stream).collect(Collectors.toList());
  }

  public int size(){
    return this.asList().size();
  }

  @Override
  public Iterator<T> iterator() {
    PriorityList<T> pl = this;
    return new Iterator<T>() {
      private final List<T> list = pl.asList();
      private int currentIndex = 0;

      @Override
      public boolean hasNext() {
        return currentIndex < list.size();
      }

      @Override
      public T next() {
        return list.get(currentIndex++);
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }

  public void forEach(Consumer<? super T> action) {
    lists.forEach(((priority, sublist) -> {
      sublist.forEach(action);
    }));
  }
}
