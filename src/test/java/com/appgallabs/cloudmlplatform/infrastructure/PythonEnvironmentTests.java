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
import java.util.UUID;

import org.datavec.python.PythonGIL;

@QuarkusTest
public class PythonEnvironmentTests {
    private static Logger logger = LoggerFactory.getLogger(PythonEnvironmentTests.class);

    @Inject
    private PythonEnvironment pythonEnvironment;

    @Test
    public void executeTraining() throws Exception{
        String code = IOUtils.toString(Thread.currentThread().getContextClassLoader().getResourceAsStream("dataScience/wskTraining.py"),
                StandardCharsets.UTF_8);

        String action = UUID.randomUUID().toString();
        String activationId = this.pythonEnvironment.executeTraining(action,code);

        //int waitTime = 300000;
        //int waitTime = 15000;
        //Thread.sleep(waitTime);

        //this.pythonEnvironment.readActivation(activationId);
    }
}
