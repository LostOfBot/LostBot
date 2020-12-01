package com.lostofthought.util;

import com.lostofthought.util.functional.Optional;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.List;

public class Reflection {
  public static Optional<Class<?>> FullyQualifiedClass(String name){
    return Exceptional.optionOfExceptional(() -> Class.forName(name));
  }
  public static Type[] GetTemplateArguments(Class<?> clazz){
    return Cast.<ParameterizedType>unchecked(clazz.getGenericSuperclass()).getActualTypeArguments();
  }
  public static Type[] GetTemplateArguments(Object o){
    return GetTemplateArguments(o.getClass());
  }
  public static <R> Optional<R> WalkInheritance(Class<?> clazz, Func.Func1<Class<?>, Optional<R>> func){
    return ForEach.BreadthFirst(
      clazz,
      cls -> {
        Queue<Class<?>> ret = new LinkedList<>();
        if(cls.getSuperclass() != null){
          ret.add(cls.getSuperclass());
        }
        ret.addAll(Arrays.asList(cls.getInterfaces()));
        return ret;
      },
      func);
  }
  public static Optional<Field> FieldFrom(Class<?> clazz, String name){
    return Reflection.WalkInheritance(
      clazz,
      cls ->
        Func.pipe(Exceptional.optionOfExceptional(() -> cls.getDeclaredField(name)),
          Optional::coerceNull,
          Func.Func2.<Func.Func1<Field, Field>, Optional<Field>, Optional<Field>>from(Optional::map).apply(
            m -> {
              m.setAccessible(true);
              return m;
            }
          )
        )
    );
  }
  public static Optional<Class<?>> ClassFrom(Class<?> clazz, String name){
    return WalkInheritance(
      clazz,
      child -> {
        for (Class<?> declaredClass : child.getDeclaredClasses()) {
          if(declaredClass.getSimpleName().equals(name)){
            return Optional.of(declaredClass);
          }
        }
        return Optional.empty();
      });
  }
  public static Optional<Method> MethodFrom(Class<?> clazz, String name, Class<?>... argTypes){
    return Reflection.WalkInheritance(
      clazz,
      cls ->
        Func.pipe(Exceptional.optionOfExceptional(() -> cls.getDeclaredMethod(name, argTypes)),
          Optional::coerceNull,
          Func.Func2.<Func.Func1<Method, Method>, Optional<Method>, Optional<Method>>from(Optional::map).apply(
            m -> {
              m.setAccessible(true);
              return m;
            }
          )
        )
    );
  }
  public static <T> Optional<T> InstanceValueFrom(Object o, String name, Class<?> clazz){
    return Func.pipe(
      FieldFrom(clazz, name),
      Optional.<Field, T>bind().apply(
        field ->
        Cast.unchecked(
          Exceptional.optionOfExceptional(
            () -> field.get(o)
          )
        )
      )
    );
  }
  public static <T> Optional<T> InstanceValueFrom(Object o, String name){
    return InstanceValueFrom(o, name, o.getClass());
  }
  public static <T> Optional<T> StaticValueFrom(Class<?> clazz, String name){
    return InstanceValueFrom(null, name, clazz);
  }
  public static <T> Optional<T> RunMethodFrom(Object o, Class<?> clazz, String name, Class<?>[] argTypes, Object[] arguments){
    return Func.pipe(
      MethodFrom(clazz, name, argTypes),
      Optional.<Method, T>bind().apply(
        method ->
        Exceptional.optionOfExceptional(
          () -> Cast.unchecked(method.invoke(o, arguments))
        )
      )
    );
  }
  public static <T> Optional<T> RunMethodFrom(Object o, Class<?> clazz, String name, Object[] arguments){
    List<Class<?>> argTypes = new ArrayList<>();
    for (Object argument : arguments) {
      argTypes.add(argument.getClass());
    }
    return RunMethodFrom(o, clazz, name, Cast.unchecked(argTypes.toArray(new Class<?>[0])), arguments);
  }
  public static <T> Optional<T> RunMethodFrom(Object o, String name, Object... arguments){
    return RunMethodFrom(o, o.getClass(), name, arguments);
  }
  public static <T> Optional<T> RunStaticMethodFrom(Class<?> clazz, String name, Class<?>[] argTypes, Object[] arguments){
    return RunMethodFrom(null, clazz, name, argTypes, arguments);
  }
  public static <T> Optional<T> RunStaticMethodFrom(Class<?> clazz, String name, Object... arguments){
    return RunMethodFrom(null, clazz, name, arguments);
  }
}