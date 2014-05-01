package com.khoa.quach.norcalcaltraintimetable;

import java.lang.Thread.UncaughtExceptionHandler;

public class MyExceptionHandler implements UncaughtExceptionHandler 
{       
    @Override
    public void uncaughtException(Thread thread, Throwable ex)
    {
        ex.printStackTrace();
    }
}
