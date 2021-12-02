package com.appgallabs.cloudmlplatform.datascience.codelabs;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.util.Eval;
import io.quarkus.test.junit.QuarkusTest;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@QuarkusTest
public class GroovyModeling {

    @Test
    public void simpleScript() throws Exception{
        Binding context = new Binding();
        GroovyShell shell = new GroovyShell(Thread.currentThread().getContextClassLoader(),context);
        String script = IOUtils.toString(Thread.currentThread().
                getContextClassLoader().getResourceAsStream(
                        "scripting/simpleScript.ml"
        ), StandardCharsets.UTF_8);
        Object result = shell.run(script,"simpleScript",new ArrayList<>());
        System.out.println(result);
    }
}
