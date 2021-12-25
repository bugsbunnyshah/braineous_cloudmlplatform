package com.appgallabs.cloudmlplatform.datascience.service;

import com.appgallabs.dataplatform.util.JsonUtil;
import com.google.gson.JsonObject;
import io.quarkus.test.junit.QuarkusTest;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class CloudMLServiceTests {
    private static Logger logger = LoggerFactory.getLogger(CloudMLServiceTests.class);

    @Inject
    private CloudMLService cloudMLService;

    @Test
    public void executeScript() throws Exception{
        String script = IOUtils.toString(Thread.currentThread().
                getContextClassLoader().getResourceAsStream(
                "scripting/simpleScript.ml"
        ), StandardCharsets.UTF_8);

        JsonObject json = this.cloudMLService.executeScript(script);
        JsonUtil.print(json);
        assertTrue(json.has("output"));
        assertFalse(json.has("exception"));
    }

    @Test
    public void executeScriptWithException() throws Exception{
        String script = IOUtils.toString(Thread.currentThread().
                getContextClassLoader().getResourceAsStream(
                "scripting/simpleScriptWithException.ml"
        ), StandardCharsets.UTF_8);

        JsonObject json = this.cloudMLService.executeScript(script);
        JsonUtil.print(json);
        assertTrue(json.has("output"));
        assertTrue(json.has("exception"));
    }
}
