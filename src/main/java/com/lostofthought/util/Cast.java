package com.lostofthought.util;

import com.lostofthought.util.functional.Optional;
import java.lang.reflect.Type;

public class Cast {
  @SuppressWarnings("unchecked")
  public static <T> T unchecked(Object o){
    return (T) o;
  }
  public static <T> T checked(Object o, Class<T> c){
    if(c.isAssignableFrom(o.getClass())){
      return Cast.unchecked(c.cast(o));
    }
    throw new RuntimeException(o.getClass() + " cannot be cast to class: " + c.getCanonicalName());
  }
  public static <T> T checked(Object o, Type c){
    if(c.getTypeName().equals(o.getClass().getGenericSuperclass().getTypeName())){
      return Cast.unchecked(o);
    }
    throw new RuntimeException(o.getClass() + " cannot be cast to type: " + c.getTypeName());
  }
  public static <T, R> Optional<R> optionOfChecked(T o, Class<R> c){
    return Exceptional.optionOfExceptional(() -> checked(o, c));
  }
}
