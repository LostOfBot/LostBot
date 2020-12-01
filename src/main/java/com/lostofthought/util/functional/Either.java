package com.lostofthought.util.functional;

import com.lostofthought.util.Cast;
import com.lostofthought.util.Func;
import com.lostofthought.util.Reflection;

import java.util.ArrayList;
import java.util.List;

public abstract class Either<L,R> {
  private Either(){}
  public abstract <T> T reduce(Func.Func1<? super L, ? extends T> lf, Func.Func1<? super R, ? extends T> rf);
  public void reduceV(Func.Func1V<? super L> lf, Func.Func1V<? super R> rf){
    this.reduce(
        l -> {
          lf.apply(l);
          return null;
        },
        r -> {
          rf.apply(r);
          return null;
        });
  }
  public <T> T reduceR(Func.Func0<T> lf, Func.Func1<R, T> rf){
    return this.reduce(
        l -> lf.apply(),
        rf
    );
  }
  public <T> T reduceL(Func.Func1<L, T> lf, Func.Func0<T> rf){
    return this.reduce(
        lf,
        r -> rf.apply()
    );
  }
  public void reduceRV(Func.Func0V lf, Func.Func1V<R> rf){
    this.reduce(
      l -> {
        lf.apply();
        return null;
      },
      r -> {
        rf.apply(r);
        return null;
      }
    );
  }
  public void reduceLV(Func.Func1V<L> lf, Func.Func0V rf){
    this.reduce(
      l -> {
        lf.apply(l);
        return null;
      },
      r -> {
        rf.apply();
        return null;
      }
    );
  }
  public static <L, R, T> T reduce(Func.Func1<? super L, ? extends T> lf, Func.Func1<? super R, ? extends T> rf, Either<L, R> e){
    return e.reduce(lf, rf);
  }
  public static <L, R> void reduceV(Func.Func1V<? super L> lf, Func.Func1V<? super R> rf, Either<L, R> e){
    e.reduceV(lf, rf);
  }
  public static <L, R, T> Func.Func3<Func.Func1<? super L, ? extends T>, Func.Func1<? super R, ? extends T>, Either<L, R>, T> reduce(){
    return Either::reduce;
  }
  public static <L, R> Func.Func3V<Func.Func1V<? super L>, Func.Func1V<? super R>, Either<L, R>> reduceV(){
    return Either::reduceV;
  }
  public <R2> Either<L, R2> map(Func.Func1<? super R, ? extends R2> f){
    return this.reduce(Either::left, r -> Either.right(f.apply(r)));
  }
  public static <L, R, R2> Either<L, R2> map(Func.Func1<R, R2> f, Either<L, R> e){
    return e.map(f);
  }
  public static <L, R, R2> Func.Func2<Func.Func1<R, R2>, Either<L, R>, Either<L, R2>> map(){
    return Either::map;
  }
  public <L2> Either<L2, R> mapL(Func.Func1<? super L, ? extends L2> f){
    return this.reduce(l -> Either.left(f.apply(l)), Either::right);
  }
  public static <L, R, L2> Either<L2, R> mapL(Func.Func1<L, L2> f, Either<L, R> e){
    return e.mapL(f);
  }
  public static <L, R, L2> Func.Func2<Func.Func1<L, L2>, Either<L, R>, Either<L2, R>> mapL(){
    return Either::mapL;
  }
  public R fromRight(R default_){
    return this.reduce(l -> default_, r -> r);
  }
  public static <R> R fromRight(R default_, Either<?, R> e){
    return e.fromRight(default_);
  }
  public static <R> Func.Func2<Either<?, R>, R, R> fromRight(){
    return Either::fromRight;
  }
  public L fromLeft(L default_){
    return this.reduce(l -> l, r -> default_);
  }
  public static <L> L fromLeft(L default_, Either<L, ?> e){
    return e.fromLeft(default_);
  }
  public static <L> Func.Func2<Either<L, ?>, L, L> fromLeft(){
    return Either::fromLeft;
  }
  public boolean isRight_(){
    return this.reduce(l -> false, r -> true);
  };
  public static boolean isRight(Either<?, ?> e){
    return e.isRight_();
  };
  public static Func.Func1<Either<?, ?>, Boolean> isRight(){
    return Either::isRight;
  }
  public boolean isLeft_(){
    return !this.isRight_();
  };
  public static boolean isLeft(Either<?, ?> e){
    return !e.isLeft_();
  };
  public static Func.Func1<Either<?, ?>, Boolean> isLeft(){
    return Either::isLeft;
  }
  public static <L,R> Either<L,R> left(L value) {
    return new Either<L,R>() {
      @Override
      public <T> T reduce(Func.Func1<? super L, ? extends T> lf, Func.Func1<? super R, ? extends T> rf) {
        return lf.apply(value);
      }
    };
  }
  public static <L, R> Func.Func1<L, Either<L, R>> left(){
    return Either::left;
  }
  public static <L,R> Either<L,R> right(R value) {
    return new Either<L,R>() {
      @Override
      public <T> T reduce(Func.Func1<? super L, ? extends T> lf, Func.Func1<? super R, ? extends T> rf) {
        return rf.apply(value);
      }
    };
  }
  public static <L, R> Func.Func1<R, Either<L, R>> right(){
    return Either::right;
  }
  public Either<R, L> swap_(){
    return this.reduce(Either::right, Either::left);
  }
  public static <L, R> Either<R, L> swap(Either<L, R> e){
    return e.swap_();
  }
  public static <L, R> Func.Func1<Either<L, R>, Either<R, L>> swap(){
    return Either::swap;
  }
  public <R2> Either<L, Either<L, R2>> joinRight__Evil(){
    // Due to type erasure, we can't verify if `this instanceof Either<L, Either<L, R2>>`
    return Either.joinRight(Cast.checked(this, Either.class.getGenericSuperclass()));
  }
  public static <L, R2> Either<L, R2> joinRight(Either<L, Either<L, R2>> e){
    return e.reduce(
      l -> Cast.unchecked(e),
      r -> e.fromRight(null)
    );
  }
  public static <L, R2> Func.Func1<Either<L, Either<L, R2>>, Either<L, R2>> joinRight(){
    return Either::joinRight;
  }
  public <L2> Either<L2, R> joinLeft_Evil(){
    // Due to type erasure, we can't verify if `this instanceof Either<Either<L2, R>, R>`
    return Either.joinLeft(Cast.checked(this, Either.class.getGenericSuperclass()));
  }
  public static <L2, R> Either<L2, R> joinLeft(Either<Either<L2, R>, R> e){
    return e.reduce(
      l -> e.fromLeft(null),
      r -> Cast.unchecked(e)
    );
  }
  public static <L2, R> Func.Func1<Either<Either<L2, R>, R>, Either<L2, R>> joinLeft(){
    return Either::joinLeft;
  }
  public <R2> Either<L, R2> bind(Func.Func1<? super R, Either<L, R2>> f){
    return this.reduce(
      l -> Cast.unchecked(this),
      r -> f.apply(this.fromRight(null))
    );
  }
  public static <L, R, R2> Either<L, R2> bind(Func.Func1<R, Either<L, R2>> f, Either<L, R> e){
    return e.bind(f);
  }
  public static <L, R, R2> Func.Func2<Func.Func1<R, Either<L, R2>>, Either<L, R>, Either<L, R2>> bind(){
    return Either::bind;
  }
  public Optional<R> toOptional_(){
    return this.reduce(
      l -> Optional.empty(),
      Optional::of
    );
  }
  public static <R> Optional<R> toOptional(Either<?, R> e){
    return e.toOptional_();
  }
  public static <R> Func.Func1<Either<?, R>, Optional<R>> toOptional(){
    return Either::toOptional;
  }
  public static <L> List<L> lefts(List<Either<L, ?>> ll){
    return Func.pipe(
      ll,
      ListUtil.<Either<L, ?>>filter().apply(Either::isLeft),
      ListUtil.<Either<L, ?>, List<L>>reduce().apply(new ArrayList<>(), (acc, l) -> {
        acc.add(fromLeft(null, l));
        return acc;
      })
    );
  }
  public static <R> List<R> rights(List<Either<?, R>> rl){
    return Func.pipe(
      rl,
      ListUtil.<Either<?, R>>filter().apply(Either::isRight),
      ListUtil.<Either<?, R>, List<R>>reduce().apply(new ArrayList<>(), (acc, r) -> {
        acc.add(fromRight(null, r));
        return acc;
      })
    );
  }
  @Override
  public String toString() {
    return reduce(
      (L l) -> "Either<" + Reflection.GetTemplateArguments(this)[0].getTypeName() + " /* " + l.getClass().getSimpleName() + " */, "
        + Reflection.GetTemplateArguments(this)[1].getTypeName() + " /* [Unknown] */>{" + l + "}",
      (R r) -> "Either<" + Reflection.GetTemplateArguments(this)[0].getTypeName() + " /* [Unknown] */, "
        + Reflection.GetTemplateArguments(this)[1].getTypeName() + " /* " + r.getClass().getSimpleName() + " */>{" + r + "}",
      this
    );
  }
  public static String toStringStatic(Either<?, ?> e){
    return e.toString();
  }
  public static Func.Func1<Either<?, ?>, String> toStringStatic(){
    return Either::toStringStatic;
  }
  public R throwOrRight() {
    if(this.isLeft_()){
      throw new RuntimeException(Cast.<Throwable>unchecked(this.reduce(e -> e, r -> new RuntimeException())));
    }
    return Cast.unchecked(this.reduce(e -> e, r -> r));
  }
}