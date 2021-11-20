package com.appgallabs.cloudmlplatform.infrastructure;

import io.quarkus.test.junit.QuarkusTest;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.nio.charset.StandardCharsets;

@QuarkusTest
public class PythonEnvironmentTests {
    private static Logger logger = LoggerFactory.getLogger(PythonEnvironmentTests.class);

    @Inject
    private PythonEnvironment pythonEnvironment;

    //@Test
    public void executeScript() throws Exception{
        String pythonScript = IOUtils.toString(Thread.currentThread().getContextClassLoader().
                        getResourceAsStream("scripting/helloJPype.py"),
                StandardCharsets.UTF_8);

        this.pythonEnvironment.executeScript(pythonScript);

        Thread.sleep(30000);
    }
}
