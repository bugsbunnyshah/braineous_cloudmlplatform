package com.appgallabs.cloudmlplatform.datascience.codelabs;

import com.appgallabs.dataplatform.util.JsonUtil;
import com.google.gson.JsonObject;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.util.Eval;
import io.quarkus.test.junit.QuarkusTest;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

//@QuarkusTest
public class GroovyModeling {
    private static Logger logger = LoggerFactory.getLogger(GroovyModeling.class);

    @Test
    public void simpleScript() throws Exception{
        Binding context = new Binding();
        GroovyShell shell = new GroovyShell(Thread.currentThread().getContextClassLoader(),context);
        String script = IOUtils.toString(Thread.currentThread().
                getContextClassLoader().getResourceAsStream(
                        "scripting/simpleScript.ml"
        ), StandardCharsets.UTF_8);

        JsonObject json = new JsonObject();
        PrintStream old = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            PrintStream ps = new PrintStream(baos);
            // IMPORTANT: Save the old System.out!
            // Tell Java to use your special stream
            System.setOut(ps);
            // Print some output: goes to your special stream
            //System.out.println("Foofoofoo!");
            // Put things back
            // Show what happened
            //System.out.println("Here: " + baos.toString());

            context.setProperty("out", ps);
            Object result = shell.run(script, "simpleScript", new ArrayList<>());
            ps.flush();
            System.out.println(result);
        }catch(Exception e){
            logger.error(e.getMessage(),e);
            String trace = ExceptionUtils.readStackTrace(e);
            json.addProperty("exception",trace);
        }
        finally {
            System.setOut(old);
            json.addProperty("output",baos.toString());
            JsonUtil.print(json);
            Thread.sleep(5000);
            baos.close();
        }

        assertTrue(json.has("output"));
        assertFalse(json.has("exception"));
    }

    //@Test
    public void simpleScriptWithException() throws Exception{
        Binding context = new Binding();
        GroovyShell shell = new GroovyShell(Thread.currentThread().getContextClassLoader(),context);
        String script = IOUtils.toString(Thread.currentThread().
                getContextClassLoader().getResourceAsStream(
                "scripting/simpleScriptWithException.ml"
        ), StandardCharsets.UTF_8);

        JsonObject json = new JsonObject();
        PrintStream old = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            PrintStream ps = new PrintStream(baos);
            // IMPORTANT: Save the old System.out!
            // Tell Java to use your special stream
            System.setOut(ps);
            // Print some output: goes to your special stream
            //System.out.println("Foofoofoo!");
            // Put things back
            // Show what happened
            //System.out.println("Here: " + baos.toString());

            context.setProperty("out", ps);
            Object result = shell.run(script, "simpleScript", new ArrayList<>());
            ps.flush();
            System.out.println(result);
            json.addProperty("output",baos.toString());
        }catch(Exception e){
            logger.error(e.getMessage(),e);
            String trace = ExceptionUtils.readStackTrace(e);
            json.addProperty("exception",trace);
        }
        finally {
            System.setOut(old);
            json.addProperty("output",baos.toString());
            JsonUtil.print(json);
            Thread.sleep(5000);
            baos.close();
        }

        assertEquals("START_EXECUTION\n",json.get("output").getAsString());
        assertTrue(json.has("exception"));
    }
}
