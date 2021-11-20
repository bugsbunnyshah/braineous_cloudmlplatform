package com.appgallabs.cloudmlplatform.datascience.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@ApplicationScoped
public class CloudMLService {
    private static Logger logger = LoggerFactory.getLogger(CloudMLService.class);

    public void executeScript(String script){
        //TODO: IMPLEMENT_ME
    }
}
