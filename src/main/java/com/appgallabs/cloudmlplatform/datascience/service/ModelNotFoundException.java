package com.appgallabs.cloudmlplatform.datascience.service;

public class ModelNotFoundException extends Exception
{
    public ModelNotFoundException(String message)
    {
        super(message);
    }

    public ModelNotFoundException(Exception source)
    {
        super(source);
    }
}
