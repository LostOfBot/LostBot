package com.lostofthought.util.functional;

import com.lostofthought.util.Cast;
import com.lostofthought.util.Func;
import com.lostofthought.util.Reflection;

public abstract class Optional<T> {
  private Optional(){}
  public abstract <R> R reduce(Func.Func0<? extends R> ef, Func.Func1<? super T, ? extends R> of);
  public void reduceV(Func.Func0V ef, Func.Func1V<? super T> of){
    this.reduce(
        () -> {
          ef.apply();
          return null;
        },
        o -> {
          of.apply(o);
          return null;
        }
    );
  }
  public static <T, R> R reduce(Func.Func0<? extends R> ef, Func.Func1<? super T, ? extends R> of, Optional<T> option){
    return option.reduce(ef, of);
  }
  public static <T> void reduceV(Func.Func0V ef, Func.Func1V<? super T> of, Optional<T> option){
    option.reduceV(ef, of);
  }
  public static <T, R> Func.Func3<Func.Func0<? extends R>, Func.Func1<? super T, ? extends R>, Optional<T>, R> reduce(){
    return Optional::reduce;
  }
  public static <T> Func.Func3V<Func.Func0V, Func.Func1V<? super T>, Optional<T>> reduceV(){
    return Optional::reduceV;
  }
  public static <T> Optional<T> of(T value) {
    return new Optional<T>() {
      @Override
      public <R> R reduce(Func.Func0<? extends R> ef, Func.Func1<? super T, ? extends R> of){
        return of.apply(value);
      }
    };
  }
  public static <T> Func.Func1<T, Optional<T>> of(){
    return Optional::of;
  }
  public static <T> Optional<T> empty() {
    return new Optional<T>() {
      @Override
      public <R> R reduce(Func.Func0<? extends R> ef, Func.Func1<? super T, ? extends R> of){
        return ef.apply();
      }
    };
  }
  public static <T> Func.Func0<Optional<T>> emptyStatic(){
    return Optional::empty;
  }
  public static <T> Optional<T> ofNotNull(T value) {
    return Optional.of(value).coerceNull_();
  }
  public static <T> Func.Func1<T, Optional<T>> ofNotNull(){
    return Optional::ofNotNull;
  }
  public <R> Optional<R> map(Func.Func1<? super T, ? extends R> f){
    return this.reduce(Optional::empty, (value) -> Optional.of(f.apply(value)));
  }
  public static <T, R> Optional<R> map(Func.Func1<? super T, ? extends R> f, Optional<T> option){
    return option.map(f);
  }
  public static <T, R> Func.Func2<Func.Func1<? super T, ? extends R>, Optional<T>, Optional<R>> map(){
    return Optional::map;
  }
  public T from(T default_){
    return this.reduce(
      () -> default_,
      (value) -> value
    );
  }
  public static <T> T from(T default_, Optional<T> option){
    return option.from(default_);
  }
  public static <T> Func.Func2<T, Optional<T>, T> from(){
    return Optional::from;
  }
  public boolean is_(){
    return this.reduce(
      () -> false,
      (value) -> true
    );
  }
  public static <T> boolean is(Optional<T> option){
    return option.is_();
  }
  public static <T> Func.Func1<Optional<T>, Boolean> is(){
    return Optional::is;
  }
  public boolean isNot_(){
    return !this.is_();
  }
  public static <T> boolean isNot(Optional<T> option){
    return option.isNot_();
  }
  public static <T> Func.Func1<Optional<T>, Boolean> isNot(){
    return Optional::isNot;
  }
  public <Y> Optional<Y> join_Evil(){
    return Optional.join(Cast.checked(this, Optional.class.getGenericSuperclass()));
  }
  public static <Y> Optional<Y> join(Optional<Optional<Y>> option){
    return option.from(Cast.unchecked(option));
  }
  public static <Y> Func.Func1<Optional<Optional<Y>>, Optional<Y>> join(){
    return Optional::join;
  }
  public <R> Optional<R> bind(Func.Func1<T, Optional<R>> f) {
    return this.reduce(
      () -> Cast.unchecked(this),
      f
    );
  }
  public static <T, R> Optional<R> bind(Func.Func1<T, Optional<R>> f, Optional<T> option) {
    return option.bind(f);
  }
  public static <T, R> Func.Func2<Func.Func1<T, Optional<R>>, Optional<T>, Optional<R>> bind() {
    return Optional::bind;
  }
  public Optional<T> coerceNull_() {
    return this.bind(
      value -> value == null
        ? Optional.empty()
        : this
    );
  }
  public static <T> Optional<T> coerceNull(Optional<T> option) {
    return option.coerceNull_();
  }
  public static <T> Func.Func1<Optional<T>, Optional<T>> coerceNull() {
    return Optional::coerceNull;
  }
  @Override
  public String toString() {
    return Optional.reduce(
      () -> "Optional<" + Reflection.GetTemplateArguments(this)[0].getTypeName() + ">{}",
      (T t) -> "Optional<" + t.getClass().getSimpleName() + " */>{" + t.toString() + "}",
      this
    );
  }
  public static <T> String toStringStatic(Optional<T> option) {
    return option.reduce(
      () -> "Optional<" + Reflection.GetTemplateArguments(option)[0].getTypeName() + ">{}",
      (T t) -> "Optional<" + t.getClass().getSimpleName() + " */>{" + t.toString() + "}"
    );
  }
  public static <T> Func.Func1<Optional<T>, String> toStringStatic(){
    return Optional::toStringStatic;
  }

  @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
  public static <T> Optional<T> ofJava(java.util.Optional<T> o){
    return o.map(Optional::of).orElseGet(Optional::empty);
  }
}