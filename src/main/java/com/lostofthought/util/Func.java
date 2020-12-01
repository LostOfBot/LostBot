package com.lostofthought.util;

// Pipeline
// let (|>) x f = f x
// Composition
// let (>>) f g x = g ( f(x) )
public class Func {
  public interface Predicate<A1>{
    static <T> Func1<T, Boolean> from(Func1<T, Boolean> f){
      return f;
    }
    boolean apply(A1 a1);
    default Pipeable<Predicate<A1>> asPipeable(){
      return new Pipeable<>(this);
    }
  }
  public interface PredicateThrows<A1, E extends Exception>{
    static <T, E extends Exception> Func1Throws<T, Boolean, E> from(Func1Throws<T, Boolean, E> f){
      return f;
    }
    boolean apply(A1 a1);
    default Predicate<A1> asRuntime(){
      return a1 -> {
        try {
          return this.apply(a1);
        } catch (Exception e){
          throw new RuntimeException(e);
        }
      };
    }
    default Pipeable<PredicateThrows<A1, E>> asPipeable(){
      return new Pipeable<>(this);
    }
  }

  public interface Func0 <R> {
    static <R> Func0<R> from(Func0<R> f){
      return f;
    }
    R apply();
    default Pipeable<Func0<R>> asPipeable(){
      return new Pipeable<>(this);
    }
  }
  public interface Func0Throws <R, E extends Exception> {
    static <R, E extends Exception> Func0Throws<R, E> from(Func0Throws<R, E> f){
      return f;
    }
    R apply() throws E;
    default Func0<R> asRuntime(){
      return () -> {
        try {
          return this.apply();
        } catch (Exception e){
          throw new RuntimeException(e);
        }
      };
    }
    default Pipeable<Func0Throws<R, E>> asPipeable() {
      return new Pipeable<>(this);
    }
  }
  public interface Func1 <A1, R> {
    static <A1, R> Func1<A1, R> from(Func1<A1, R> f){
      return f;
    }
    R apply(A1 a1);
    static <T> Func1<T, T> id(){
      return (T o) -> o;
    }
    default Func0<R> nullary(A1 a1){
      return () -> this.apply(a1);
    }
    default Pipeable<Func1<A1, R>> asPipeable(){
      return new Pipeable<>(this);
    }
  }
  public interface Func1Throws <A1, R, E extends Exception> {
    static <A1, R, E extends Exception> Func1Throws<A1, R, E> from(Func1Throws<A1, R, E> f){
      return f;
    }
    R apply(A1 a1);
    default Func1<A1, R> asRuntime(){
      return a1 -> {
        try {
          return this.apply(a1);
        } catch (Exception e){
          throw new RuntimeException(e);
        }
      };
    }
    static <T, E extends Exception> Func1Throws<T, T, E> id(){
      return (T o) -> o;
    }
    default Func0Throws<R, E> nullary(A1 a1){
      return () -> this.apply(a1);
    }
    default Pipeable<Func1Throws<A1, R, E>> asPipeable(){
      return new Pipeable<>(this);
    }
  }
  public interface Func2 <A1, A2, R> {
    static <A1, A2, R> Func2<A1, A2, R> from(Func2<A1, A2, R> f){
      return f;
    }
    R apply(A1 a1, A2 a2);
    default Func1<A2, R> apply(A1 a1){
      return (A2 a2) -> this.apply(a1, a2);
    }
    default Func1<A1, R> applyR(A2 a2){
      return (A1 a1) -> this.apply(a1, a2);
    }
    default Func0<R> nullary(A1 a1, A2 a2){
      return () -> this.apply(a1, a2);
    }
    default Func1<A1, Func1<A2, R>> curry(){
      return (A1 a1) -> ((A2 a2) -> this.apply(a1, a2));
    }
    default Pipeable<Func2<A1, A2, R>> asPipeable(){
      return new Pipeable<>(this);
    }
  }
  public interface Func2Throws <A1, A2, R, E extends Exception> {
    static <A1, A2, R, E extends Exception> Func2Throws<A1, A2, R, E> from(Func2Throws<A1, A2, R, E> f){
      return f;
    }
    R apply(A1 a1, A2 a2);
    default Func1Throws<A2, R, E> apply(A1 a1){
      return (A2 a2) -> this.apply(a1, a2);
    }
    default Func1Throws<A1, R, E> applyR(A2 a2){
      return (A1 a1) -> this.apply(a1, a2);
    }
    default Func0Throws<R, E> nullary(A1 a1, A2 a2){
      return () -> this.apply(a1, a2);
    }
    default Func1Throws<A1, Func1Throws<A2, R, E>, E> curry(){
      return (A1 a1) -> ((A2 a2) -> this.apply(a1, a2));
    }
    default Pipeable<Func2Throws<A1, A2, R, E>> asPipeable(){
      return new Pipeable<>(this);
    }
    default Func2<A1, A2, R> asRuntime(){
      return (a1, a2) -> {
        try {
          return this.apply(a1, a2);
        } catch (Exception e){
          throw new RuntimeException(e);
        }
      };
    }
  }
  public interface Func3 <A1, A2, A3, R> {
    static <A1, A2, A3, R> Func3<A1, A2, A3, R> from(Func3<A1, A2, A3, R> f){
      return f;
    }
    R apply(A1 a1, A2 a2, A3 a3);
    default Func2<A2, A3, R> apply(A1 a1){
      return (A2 a2, A3 a3) -> this.apply(a1, a2, a3);
    }
    default Func2<A1, A2, R> applyR(A3 a3){
      return (A1 a1, A2 a2) -> this.apply(a1, a2, a3);
    }
    default Func1<A3, R> apply(A1 a1, A2 a2){
      return (A3 a3) -> this.apply(a1, a2, a3);
    }
    default Func1<A1, R> applyR(A3 a3, A2 a2){
      return (A1 a1) -> this.apply(a1, a2, a3);
    }
    default Func0<R> nullary(A1 a1, A2 a2, A3 a3){
      return () -> this.apply(a1, a2, a3);
    }
    default Func1<A1, Func1<A2, Func1<A3, R>>> curry(){
      return (A1 a1) -> (A2 a2) -> (A3 a3) -> this.apply(a1, a2, a3);
    }
    default Pipeable<Func3<A1, A2, A3, R>> asPipeable(){
      return new Pipeable<>(this);
    }
  }

  public interface Func3Throws <A1, A2, A3, R, E extends Exception> {
    static <A1, A2, A3, R, E extends Exception> Func3Throws<A1, A2, A3, R, E> from(Func3Throws<A1, A2, A3, R, E> f){
      return f;
    }
    R apply(A1 a1, A2 a2, A3 a3);
    default Func2Throws<A2, A3, R, E> apply(A1 a1){
      return (A2 a2, A3 a3) -> this.apply(a1, a2, a3);
    }
    default Func2Throws<A1, A2, R, E> applyR(A3 a3){
      return (A1 a1, A2 a2) -> this.apply(a1, a2, a3);
    }
    default Func1Throws<A3, R, E> apply(A1 a1, A2 a2){
      return (A3 a3) -> this.apply(a1, a2, a3);
    }
    default Func1Throws<A1, R, E> applyR(A3 a3, A2 a2){
      return (A1 a1) -> this.apply(a1, a2, a3);
    }
    default Func0Throws<R, E> nullary(A1 a1, A2 a2, A3 a3){
      return () -> this.apply(a1, a2, a3);
    }
    default Func1Throws<A1, Func1Throws<A2, Func1Throws<A3, R, E>, E>, E> curry(){
      return (A1 a1) -> (A2 a2) -> (A3 a3) -> this.apply(a1, a2, a3);
    }
    default Pipeable<Func3Throws<A1, A2, A3, R, E>> asPipeable(){
      return new Pipeable<>(this);
    }
    default Func3<A1, A2, A3, R> asRuntime(){
      return (a1, a2, a3) -> {
        try {
          return this.apply(a1, a2, a3);
        } catch (Exception e){
          throw new RuntimeException(e);
        }
      };
    }
  }
  public interface Func4 <A1, A2, A3, A4, R> {
    static <A1, A2, A3, A4, R> Func4<A1, A2, A3, A4, R> from(Func4<A1, A2, A3, A4, R> f){
      return f;
    }
    R apply(A1 a1, A2 a2, A3 a3, A4 a4);
    default Func3<A2, A3, A4, R> apply(A1 a1){
      return (A2 a2, A3 a3, A4 a4) -> this.apply(a1, a2, a3, a4);
    }
    default Func3<A1, A2, A3, R> applyR(A4 a4){
      return (A1 a1, A2 a2, A3 a3) -> this.apply(a1, a2, a3, a4);
    }
    default Func2<A3, A4, R> apply(A1 a1, A2 a2){
      return (A3 a3, A4 a4) -> this.apply(a1, a2, a3, a4);
    }
    default Func2<A1, A2, R> applyR(A4 a4, A3 a3){
      return (A1 a1, A2 a2) -> this.apply(a1, a2, a3, a4);
    }
    default Func1<A4, R> apply(A1 a1, A2 a2, A3 a3){
      return (A4 a4) -> this.apply(a1, a2, a3, a4);
    }
    default Func1<A1, R> applyR(A4 a4, A3 a3, A2 a2){
      return (A1 a1) -> this.apply(a1, a2, a3, a4);
    }
    default Func0<R> nullary(A1 a1, A2 a2, A3 a3, A4 a4){
      return () -> this.apply(a1, a2, a3, a4);
    }
    default Func1<A1, Func1<A2, Func1<A3, Func1<A4, R>>>> curry(){
      return (A1 a1) -> (A2 a2) -> (A3 a3) -> (A4 a4) -> this.apply(a1, a2, a3, a4);
    }
    default Pipeable<Func4<A1, A2, A3, A4, R>> asPipeable(){
      return new Pipeable<>(this);
    }
  }
  public interface Func4Throws <A1, A2, A3, A4, R, E extends Exception> {
    static <A1, A2, A3, A4, R, E extends Exception> Func4Throws<A1, A2, A3, A4, R, E> from(Func4Throws<A1, A2, A3, A4, R, E> f){
      return f;
    }
    R apply(A1 a1, A2 a2, A3 a3, A4 a4);
    default Func3Throws<A2, A3, A4, R, E> apply(A1 a1){
      return (A2 a2, A3 a3, A4 a4) -> this.apply(a1, a2, a3, a4);
    }
    default Func3Throws<A1, A2, A3, R, E> applyR(A4 a4){
      return (A1 a1, A2 a2, A3 a3) -> this.apply(a1, a2, a3, a4);
    }
    default Func2Throws<A3, A4, R, E> apply(A1 a1, A2 a2){
      return (A3 a3, A4 a4) -> this.apply(a1, a2, a3, a4);
    }
    default Func2Throws<A1, A2, R, E> applyR(A4 a4, A3 a3){
      return (A1 a1, A2 a2) -> this.apply(a1, a2, a3, a4);
    }
    default Func1Throws<A4, R, E> apply(A1 a1, A2 a2, A3 a3){
      return (A4 a4) -> this.apply(a1, a2, a3, a4);
    }
    default Func1Throws<A1, R, E> applyR(A4 a4, A3 a3, A2 a2){
      return (A1 a1) -> this.apply(a1, a2, a3, a4);
    }
    default Func0Throws<R, E> nullary(A1 a1, A2 a2, A3 a3, A4 a4){
      return () -> this.apply(a1, a2, a3, a4);
    }
    default Func1Throws<A1, Func1Throws<A2, Func1Throws<A3, Func1Throws<A4, R, E>, E>, E>, E> curry(){
      return (A1 a1) -> (A2 a2) -> (A3 a3) -> (A4 a4) -> this.apply(a1, a2, a3, a4);
    }
    default Pipeable<Func4Throws<A1, A2, A3, A4, R, E>> asPipeable(){
      return new Pipeable<>(this);
    }
    default Func4<A1, A2, A3, A4, R> asRuntime(){
      return (a1, a2, a3, a4) -> {
        try {
          return this.apply(a1, a2, a3, a4);
        } catch (Exception e){
          throw new RuntimeException(e);
        }
      };
    }
  }
  public interface Func0V {
    static Func0V from(Func0V f){
      return f;
    }
    void apply();
    default Pipeable<Func0V> asPipeable(){
      return new Pipeable<>(this);
    }
  }
  public interface Func0VThrows <E extends Exception> {
    static <E extends Exception> Func0VThrows<E> from(Func0VThrows<E> f){
      return f;
    }
    void apply() throws E;
    default Func0V asRuntime(){
      return () -> {
        try {
          this.apply();
        } catch (Exception e){
          throw new RuntimeException(e);
        }
      };
    }
    default Pipeable<Func0VThrows<E>> asPipeable() {
      return new Pipeable<>(this);
    }
  }
  public interface Func1V <A1> {
    static <A1> Func1V<A1> from(Func1V<A1> f){
      return f;
    }
    void apply(A1 a1V);
    default Func0V nullary(A1 a1V){
      return () -> this.apply(a1V);
    }
    default Pipeable<Func1V<A1>> asPipeable(){
      return new Pipeable<>(this);
    }
  }
  public interface Func1VThrows <A1, E extends Exception> {
    static <A1, E extends Exception> Func1VThrows<A1, E> from(Func1VThrows<A1, E> f){
      return f;
    }
    void apply(A1 a1V);
    default Func1V<A1> asRuntime(){
      return a1V -> {
        try {
          this.apply(a1V);
        } catch (Exception e){
          throw new RuntimeException(e);
        }
      };
    }
    default Func0VThrows<E> nullary(A1 a1V){
      return () -> this.apply(a1V);
    }
    default Pipeable<Func1VThrows<A1, E>> asPipeable(){
      return new Pipeable<>(this);
    }
  }
  public interface Func2V <A1, A2> {
    static <A1, A2> Func2V<A1, A2> from(Func2V<A1, A2> f){
      return f;
    }
    void apply(A1 a1V, A2 a2V);
    default Func1V<A2> apply(A1 a1V){
      return (A2 a2V) -> this.apply(a1V, a2V);
    }
    default Func1V<A1> applyR(A2 a2V){
      return (A1 a1V) -> this.apply(a1V, a2V);
    }
    default Func0V nullary(A1 a1V, A2 a2V){
      return () -> this.apply(a1V, a2V);
    }
    default Func1<A1, Func1V<A2>> curry(){
      return (A1 a1V) -> ((A2 a2V) -> this.apply(a1V, a2V));
    }
    default Pipeable<Func2V<A1, A2>> asPipeable(){
      return new Pipeable<>(this);
    }
  }
  public interface Func2VThrows <A1, A2, E extends Exception> {
    static <A1, A2, E extends Exception> Func2VThrows<A1, A2, E> from(Func2VThrows<A1, A2, E> f){
      return f;
    }
    void apply(A1 a1V, A2 a2V);
    default Func1VThrows<A2, E> apply(A1 a1V){
      return (A2 a2V) -> this.apply(a1V, a2V);
    }
    default Func1VThrows<A1, E> applyR(A2 a2V){
      return (A1 a1V) -> this.apply(a1V, a2V);
    }
    default Func0VThrows<E> nullary(A1 a1V, A2 a2V){
      return () -> this.apply(a1V, a2V);
    }
    default Func1Throws<A1, Func1VThrows<A2, E>, E> curry(){
      return (A1 a1V) -> ((A2 a2V) -> this.apply(a1V, a2V));
    }
    default Pipeable<Func2VThrows<A1, A2, E>> asPipeable(){
      return new Pipeable<>(this);
    }
    default Func2V<A1, A2> asRuntime(){
      return (a1V, a2V) -> {
        try {
          this.apply(a1V, a2V);
        } catch (Exception e){
          throw new RuntimeException(e);
        }
      };
    }
  }
  public interface Func3V <A1, A2, A3> {
    static <A1, A2, A3> Func3V<A1, A2, A3> from(Func3V<A1, A2, A3> f){
      return f;
    }
    void apply(A1 a1V, A2 a2V, A3 a3V);
    default Func2V<A2, A3> apply(A1 a1V){
      return (A2 a2V, A3 a3V) -> this.apply(a1V, a2V, a3V);
    }
    default Func2V<A1, A2> applyR(A3 a3V){
      return (A1 a1V, A2 a2V) -> this.apply(a1V, a2V, a3V);
    }
    default Func1V<A3> apply(A1 a1V, A2 a2V){
      return (A3 a3V) -> this.apply(a1V, a2V, a3V);
    }
    default Func1V<A1> applyR(A3 a3V, A2 a2V){
      return (A1 a1V) -> this.apply(a1V, a2V, a3V);
    }
    default Func0V nullary(A1 a1V, A2 a2V, A3 a3V){
      return () -> this.apply(a1V, a2V, a3V);
    }
    default Func1<A1, Func1<A2, Func1V<A3>>> curry(){
      return (A1 a1V) -> (A2 a2V) -> (A3 a3V) -> this.apply(a1V, a2V, a3V);
    }
    default Pipeable<Func3V<A1, A2, A3>> asPipeable(){
      return new Pipeable<>(this);
    }
  }

  public interface Func3VThrows <A1, A2, A3, E extends Exception> {
    static <A1, A2, A3, E extends Exception> Func3VThrows<A1, A2, A3, E> from(Func3VThrows<A1, A2, A3, E> f){
      return f;
    }
    void apply(A1 a1V, A2 a2V, A3 a3V);
    default Func2VThrows<A2, A3, E> apply(A1 a1V){
      return (A2 a2V, A3 a3V) -> this.apply(a1V, a2V, a3V);
    }
    default Func2VThrows<A1, A2, E> applyR(A3 a3V){
      return (A1 a1V, A2 a2V) -> this.apply(a1V, a2V, a3V);
    }
    default Func1VThrows<A3, E> apply(A1 a1V, A2 a2V){
      return (A3 a3V) -> this.apply(a1V, a2V, a3V);
    }
    default Func1VThrows<A1, E> applyR(A3 a3V, A2 a2V){
      return (A1 a1V) -> this.apply(a1V, a2V, a3V);
    }
    default Func0VThrows<E> nullary(A1 a1V, A2 a2V, A3 a3V){
      return () -> this.apply(a1V, a2V, a3V);
    }
    default Func1Throws<A1, Func1Throws<A2, Func1VThrows<A3, E>, E>, E> curry(){
      return (A1 a1V) -> (A2 a2V) -> (A3 a3V) -> this.apply(a1V, a2V, a3V);
    }
    default Pipeable<Func3VThrows<A1, A2, A3, E>> asPipeable(){
      return new Pipeable<>(this);
    }
    default Func3V<A1, A2, A3> asRuntime(){
      return (a1V, a2V, a3V) -> {
        try {
          this.apply(a1V, a2V, a3V);
        } catch (Exception e){
          throw new RuntimeException(e);
        }
      };
    }
  }
  public interface Func4V <A1, A2, A3, A4> {
    static <A1, A2, A3, A4> Func4V<A1, A2, A3, A4> from(Func4V<A1, A2, A3, A4> f){
      return f;
    }
    void apply(A1 a1V, A2 a2V, A3 a3V, A4 a4V);
    default Func3V<A2, A3, A4> apply(A1 a1V){
      return (A2 a2V, A3 a3V, A4 a4V) -> this.apply(a1V, a2V, a3V, a4V);
    }
    default Func3V<A1, A2, A3> applyR(A4 a4V){
      return (A1 a1V, A2 a2V, A3 a3V) -> this.apply(a1V, a2V, a3V, a4V);
    }
    default Func2V<A3, A4> apply(A1 a1V, A2 a2V){
      return (A3 a3V, A4 a4V) -> this.apply(a1V, a2V, a3V, a4V);
    }
    default Func2V<A1, A2> applyR(A4 a4V, A3 a3V){
      return (A1 a1V, A2 a2V) -> this.apply(a1V, a2V, a3V, a4V);
    }
    default Func1V<A4> apply(A1 a1V, A2 a2V, A3 a3V){
      return (A4 a4V) -> this.apply(a1V, a2V, a3V, a4V);
    }
    default Func1V<A1> applyR(A4 a4V, A3 a3V, A2 a2V){
      return (A1 a1V) -> this.apply(a1V, a2V, a3V, a4V);
    }
    default Func0V nullary(A1 a1V, A2 a2V, A3 a3V, A4 a4V){
      return () -> this.apply(a1V, a2V, a3V, a4V);
    }
    default Func1<A1, Func1<A2, Func1<A3, Func1V<A4>>>> curry(){
      return (A1 a1V) -> (A2 a2V) -> (A3 a3V) -> (A4 a4V) -> this.apply(a1V, a2V, a3V, a4V);
    }
    default Pipeable<Func4V<A1, A2, A3, A4>> asPipeable(){
      return new Pipeable<>(this);
    }
  }
  public interface Func4VThrows <A1, A2, A3, A4, E extends Exception> {
    static <A1, A2, A3, A4, E extends Exception> Func4VThrows<A1, A2, A3, A4, E> from(Func4VThrows<A1, A2, A3, A4, E> f){
      return f;
    }
    void apply(A1 a1V, A2 a2V, A3 a3V, A4 a4V);
    default Func3VThrows<A2, A3, A4, E> apply(A1 a1V){
      return (A2 a2V, A3 a3V, A4 a4V) -> this.apply(a1V, a2V, a3V, a4V);
    }
    default Func3VThrows<A1, A2, A3, E> applyR(A4 a4V){
      return (A1 a1V, A2 a2V, A3 a3V) -> this.apply(a1V, a2V, a3V, a4V);
    }
    default Func2VThrows<A3, A4, E> apply(A1 a1V, A2 a2V){
      return (A3 a3V, A4 a4V) -> this.apply(a1V, a2V, a3V, a4V);
    }
    default Func2VThrows<A1, A2, E> applyR(A4 a4V, A3 a3V){
      return (A1 a1V, A2 a2V) -> this.apply(a1V, a2V, a3V, a4V);
    }
    default Func1VThrows<A4, E> apply(A1 a1V, A2 a2V, A3 a3V){
      return (A4 a4V) -> this.apply(a1V, a2V, a3V, a4V);
    }
    default Func1VThrows<A1, E> applyR(A4 a4V, A3 a3V, A2 a2V){
      return (A1 a1V) -> this.apply(a1V, a2V, a3V, a4V);
    }
    default Func0VThrows<E> nullary(A1 a1V, A2 a2V, A3 a3V, A4 a4V){
      return () -> this.apply(a1V, a2V, a3V, a4V);
    }
    default Func1Throws<A1, Func1Throws<A2, Func1Throws<A3, Func1VThrows<A4, E>, E>, E>, E> curry(){
      return (A1 a1V) -> (A2 a2V) -> (A3 a3V) -> (A4 a4V) -> this.apply(a1V, a2V, a3V, a4V);
    }
    default Pipeable<Func4VThrows<A1, A2, A3, A4, E>> asPipeable(){
      return new Pipeable<>(this);
    }
    default Func4V<A1, A2, A3, A4> asRuntime(){
      return (a1, a2, a3, a4) -> {
        try {
          this.apply(a1, a2, a3, a4);
        } catch (Exception e){
          throw new RuntimeException(e);
        }
      };
    }
  }
  static class Pipeable <A> {
    public final A value;
    private Pipeable(A obj) {
      this.value = obj;
    }
    public static <T> Pipeable<T> from(T o){
      return new Pipeable<>(o);
    }
    public <B> Pipeable<B> pipe(Func1<A, B> f){
      return new Pipeable<>(Func.pipe(value, f));
    }
    public <B, C> Pipeable<C> pipe(Func1<A, B> f, Func1<B, C> f2){
      return new Pipeable<>(Func.pipe(value, f, f2));
    }
    public <B, C, D> Pipeable<D> pipe(Func1<A, B> f, Func1<B, C> f2, Func1<C, D> f3){
      return new Pipeable<>(Func.pipe(value, f, f2, f3));
    }
    public <B, C, D, E> Pipeable<E> pipe(Func1<A, B> f, Func1<B, C> f2, Func1<C, D> f3, Func1<D, E> f4){
      return new Pipeable<>(Func.pipe(value, f, f2, f3, f4));
    }
    public <B, C, D, E, F> Pipeable<F> pipe(Func1<A, B> f, Func1<B, C> f2, Func1<C, D> f3, Func1<D, E> f4, Func1<E, F> f5){
      return new Pipeable<>(Func.pipe(value, f, f2, f3, f4, f5));
    }
    public <B, C, D, E, F, G> Pipeable<G> pipe(Func1<A, B> f, Func1<B, C> f2, Func1<C, D> f3, Func1<D, E> f4, Func1<E, F> f5, Func1<F, G> f6){
      return new Pipeable<>(Func.pipe(value, f, f2, f3, f4, f5, f6));
    }
    public <B, C, D, E, F, G, H> Pipeable<H> pipe(Func1<A, B> f, Func1<B, C> f2, Func1<C, D> f3, Func1<D, E> f4, Func1<E, F> f5, Func1<F, G> f6, Func1<G, H> f7){
      return new Pipeable<>(Func.pipe(value, f, f2, f3, f4, f5, f6, f7));
    }
  }
  public static <T> Pipeable<T> makePipe(T o){
    return Pipeable.from(o);
  }
  public static <A, B> B pipe(A o, Func1<A, B> f1){
    return f1.apply(o);
  }
  public static <A, B, C> C pipe(A o, Func1<A, B> f1, Func1<B, C> f2){
    return f2.apply(f1.apply(o));
  }
  public static  <A, B, C, D> D pipe(A o, Func1<A, B> f1, Func1<B, C> f2, Func1<C, D> f3){
    return f3.apply(f2.apply(f1.apply(o)));
  }
  public static <A, B, C, D, E> E pipe(A o, Func1<A, B> f1, Func1<B, C> f2, Func1<C, D> f3, Func1<D, E> f4){
    return f4.apply(f3.apply(f2.apply(f1.apply(o))));
  }
  public static <A, B, C, D, E, F> F pipe(A o, Func1<A, B> f1, Func1<B, C> f2, Func1<C, D> f3, Func1<D, E> f4, Func1<E, F> f5){
    return f5.apply(f4.apply(f3.apply(f2.apply(f1.apply(o)))));
  }
  public static <A, B, C, D, E, F, G> G pipe(A o, Func1<A, B> f1, Func1<B, C> f2, Func1<C, D> f3, Func1<D, E> f4, Func1<E, F> f5, Func1<F, G> f6){
    return f6.apply(f5.apply(f4.apply(f3.apply(f2.apply(f1.apply(o))))));
  }
  public static <A, B, C, D, E, F, G, H> H pipe(A o, Func1<A, B> f1, Func1<B, C> f2, Func1<C, D> f3, Func1<D, E> f4, Func1<E, F> f5, Func1<F, G> f6, Func1<G, H> f7){
    return f7.apply(f6.apply(f5.apply(f4.apply(f3.apply(f2.apply(f1.apply(o)))))));
  }
}
