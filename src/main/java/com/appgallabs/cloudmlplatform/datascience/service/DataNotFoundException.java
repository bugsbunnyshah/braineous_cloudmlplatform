package com.appgallabs.cloudmlplatform.datascience.service;

public class DataNotFoundException extends Exception
{
    public DataNotFoundException(String message)
    {
        super(message);
    }

    public DataNotFoundException(Exception source)
    {
        super(source);
    }
}
