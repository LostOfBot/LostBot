package com.lostofthought.util;

import com.lostofthought.util.functional.Either;
import com.lostofthought.util.functional.Optional;

public class Exceptional {
  public static final class Status {
    protected Status(){}
  }
  public static final Status NO_EXCEPTION = new Status();
  public static <E extends Exception, T> Either<E, T> eitherOfExceptional(Func.Func0Throws<T, E> f){
    try {
      return Either.right(f.apply());
    } catch (Exception e){
      return Cast.unchecked(Either.left(e));
    }
  }
  public static <E extends Exception, T> Either<E, T> eitherOfExceptionalFlatten(Func.Func0Throws<Either<E, T>, E> f){
    try {
      return f.apply();
    } catch (Exception e){
      return Cast.unchecked(Either.left(e));
    }
  }
  public static <E extends Exception> Either<E, Status> eitherOfExceptionalV(Func.Func0VThrows<E> f){
    try {
      f.apply();
      return Either.right(NO_EXCEPTION);
    } catch (Exception e){
      return Cast.unchecked(Either.left(e));
    }
  }
  public static <E extends Exception, T> Optional<T> optionOfExceptional(Func.Func0Throws<T, E> f){
    try {
      return Optional.of(f.apply());
    } catch (Exception e){
      return Optional.empty();
    }
  }
  public static <E extends Exception> Optional<Status> optionOfExceptionalV(Func.Func0VThrows<E> f){
    try {
      f.apply();
      return Optional.of(NO_EXCEPTION);
    } catch (Exception e){
      return Optional.empty();
    }
  }
  public static <E extends Exception, T> T eatExceptional(Func.Func0Throws<T, E> f){
    try {
      return f.apply();
    } catch (Exception e){
      throw new RuntimeException(e);
    }
  }
}
