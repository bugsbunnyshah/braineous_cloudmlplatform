package com.appgallabs.cloudmlplatform.restClient;

public class NetworkException extends Exception
{
    public NetworkException(String message)
    {
        super(message);
    }

    public NetworkException(Exception source)
    {
        super(source);
    }
}
