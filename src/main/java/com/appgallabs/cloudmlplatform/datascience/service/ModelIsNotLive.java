package com.appgallabs.cloudmlplatform.datascience.service;

public class ModelIsNotLive extends Exception
{
    public ModelIsNotLive(String message)
    {
        super(message);
    }

    public ModelIsNotLive(Exception source)
    {
        super(source);
    }
}
