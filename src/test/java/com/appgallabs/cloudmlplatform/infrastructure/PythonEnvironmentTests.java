package com.appgallabs.cloudmlplatform.infrastructure;

import com.google.gson.JsonObject;
import io.quarkus.test.junit.QuarkusTest;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.nd4j.python4j.PythonGC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import org.datavec.python.PythonGIL;

//@QuarkusTest
public class PythonEnvironmentTests {
    private static Logger logger = LoggerFactory.getLogger(PythonEnvironmentTests.class);

    @Inject
    private PythonEnvironment pythonEnvironment;

    @Inject
    private Http http;

    //@Test
    public void executeScript() throws Exception{
        String pythonScript = IOUtils.toString(Thread.currentThread().getContextClassLoader().
                        getResourceAsStream("scripting/helloJPype.py"),
                StandardCharsets.UTF_8);

        this.pythonEnvironment.executeScript(pythonScript);

        Thread.sleep(30000);
    }

    //@Test
    public void executePython() throws Exception{
        System.setProperty("org.eclipse.python4j.path.append","/Users/babyboy/.pyenv/versions/3.10.1/lib/python3.10/lib-dynload");
        System.out.println(System.getProperty("org.eclipse.python4j.path.append"));

        String command = "python3 findpython.py";
        Process process = Runtime.getRuntime().exec(command);
        String result = IOUtils.toString(process.getInputStream(),StandardCharsets.UTF_8);
        System.out.println(result);


        try(PythonGIL pythonGIL = PythonGIL.lock()) {
            try(PythonGC gc = PythonGC.watch()) {
                //execute your code
            }
        }
    }
}
