package com.lostofthought.util;

import java.util.ArrayList;
import java.util.List;

public class CLI {
  public static String[] SplitIntoArgs(String s){
    // Attempt at having sane string splitting, adhering to C escape sequences, sh single quoting rules, and escaped
    // double quoted strings.
    final int length = s.length();
    List<String> ret = new ArrayList<>();
    StringBuilder tok = new StringBuilder();
    boolean squote = false;
    boolean dquote = false;
    for(int i = 0; i < length;){
      int c = s.codePointAt(i);
      if(c == '\'' && !dquote){
        squote = !squote;
        i += Character.charCount(c);
        continue;
      }
      if(c == '\\' && !squote){
        i += Character.charCount(c);
        if (i >= length) {
          throw new RuntimeException("Invalid escape sequence");
        }
        c = s.codePointAt(i);
        switch (c){
          case '\'':
          case '\"':
          case '?':
          case '\\':
            tok.append('\\');
            break;
          case 'a':
            tok.append('\u0007');
            break;
          case 'b':
            tok.append('\b');
            break;
          case 'f':
            tok.append('\f');
            break;
          case 'n':
            tok.append('\n');
            break;
          case 'r':
            tok.append('\r');
            break;
          case 't':
            tok.append('\t');
            break;
          case 'v':
            tok.append('\u000b');
            break;
          case 'x':
          case 'u':
          case 'U':
            int flag = c;
            i += Character.charCount(c);
            c = s.codePointAt(i);
            StringBuilder hex = new StringBuilder();
            int count = 0;
            while('0' <= c  && c <= '9' || 'A' <= c && c <= 'F') {
              if(count == 4 && flag == 'u' || count == 8 && flag == 'U'){
                break;
              }
              count++;
              hex.appendCodePoint(c);
              if (i + Character.charCount(c) >= length) {
                break;
              }
              i += Character.charCount(c);
              c = s.codePointAt(i);
            }
            if(count < 1 && flag == 'x' || count != 4 && flag == 'u' || count != 8 && flag == 'U'){
              throw new RuntimeException("Invalid hex escape");
            }
            i -= Character.charCount(c); // Offset the index change at end of switch
            tok.appendCodePoint(Integer.parseInt(hex.toString(), 16));
            break;
          default:
            StringBuilder octal = new StringBuilder();
            boolean isOctal = false;
            for (int j = 0; j < 3; j++) {
              if('0' <= c && c <= '7') {
                isOctal = true;
                octal.appendCodePoint(c);
                if (i + Character.charCount(c) >= length) {
                  tok.appendCodePoint(Integer.parseInt(octal.toString(), 8));
                  break;
                }
                i += Character.charCount(c);
                c = s.codePointAt(i);
              } else {
                i -= Character.charCount(c); // Offset the index change at end of switch
                tok.appendCodePoint(Integer.parseInt(octal.toString(), 8));
                break;
              }
            }
            if(!isOctal){
              throw new RuntimeException("Invalid escape sequence");
            }
        }
        i += Character.charCount(c);
        continue;
      }
      if(c == '\"' && !squote){
        dquote = !dquote;
        i += Character.charCount(c);
        continue;
      }
      if(squote || dquote || !Character.isWhitespace(c)){
        tok.appendCodePoint(c);
        i += Character.charCount(c);
        continue;
      }
      if(Character.isWhitespace(c)){
        if(tok.length() > 0){
          ret.add(tok.toString());
          tok = new StringBuilder();
        }
        i += Character.charCount(c);
      }
    }
    if(squote || dquote){
      throw new RuntimeException("Invalid state");
    }
    if(tok.length() > 0){
      ret.add(tok.toString());
    }
    return ret.toArray(new String[0]);
  }
}
