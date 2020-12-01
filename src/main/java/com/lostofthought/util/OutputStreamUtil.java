package com.lostofthought.util;

import java.io.OutputStream;

public class OutputStreamUtil {
  public static class NullOutputStream extends OutputStream {
    @Override
    public void write(int b){}
  };
}
