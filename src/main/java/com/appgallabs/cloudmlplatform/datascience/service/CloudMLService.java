package com.appgallabs.cloudmlplatform.datascience.service;

import com.appgallabs.dataplatform.util.JsonUtil;
import com.google.gson.JsonObject;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.io.*;
import java.util.ArrayList;

@ApplicationScoped
public class CloudMLService {
    private static Logger logger = LoggerFactory.getLogger(CloudMLService.class);

    public JsonObject executeScript(String script){
        JsonObject json = new JsonObject();

        Binding context = new Binding();
        GroovyShell shell = new GroovyShell(Thread.currentThread().getContextClassLoader(),context);
        PrintStream old = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            PrintStream ps = new PrintStream(baos);
            // IMPORTANT: Save the old System.out!
            // Tell Java to use your special stream
            System.setOut(ps);

            context.setProperty("out", ps);
            Object result = shell.run(script, "simpleScript", new ArrayList<>());
            ps.flush();
        }catch(Exception e){
            String trace = ExceptionUtils.getStackTrace(e);
            json.addProperty("exception",trace);
        }
        finally {
            System.setOut(old);
            json.addProperty("output",baos.toString());
            JsonUtil.print(json);
            try {
                baos.close();
            } catch (IOException ioException) {
            }
        }


        return json;
    }
}
