package com.appgallabs.cloudmlplatform.datascience.service;

public class ArtifactNotFoundException extends Exception
{
    public ArtifactNotFoundException(String message)
    {
        super(message);
    }

    public ArtifactNotFoundException(Exception source)
    {
        super(source);
    }
}
