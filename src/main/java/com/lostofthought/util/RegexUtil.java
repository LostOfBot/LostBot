package com.lostofthought.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {
  public static class GroupedMatcher {
    private final Pattern pattern;
    public GroupedMatcher(String regex){
      pattern = Pattern.compile(regex);
    }
    public GroupedMatcher(Pattern regex){
      pattern = regex;
    }
    public String[][] Match(String str){
      Matcher m = pattern.matcher(str);
      List<String[]> ret = new ArrayList<>();
      while (m.find()) {
        String[] strings = new String[m.groupCount() + 1];
        for (int i = 0; i <= m.groupCount(); i++) {
          strings[i] = m.group(i);
        }
        ret.add(strings);
      }
      return ret.toArray(new String[][] {});
    }
  }
}
