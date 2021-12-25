package com.appgallabs.cloudmlplatform.datascience.endpoint;


import com.appgallabs.dataplatform.util.JsonUtil;
import test.components.BaseTest;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
public class CloudMLTests extends BaseTest
{
    private static Logger logger = LoggerFactory.getLogger(CloudMLTests.class);

    @Test
    public void executeScript() throws Exception
    {
        String script = IOUtils.toString(Thread.currentThread().getContextClassLoader().
                        getResourceAsStream("scripting/simpleScript.ml"),
                StandardCharsets.UTF_8);
        JsonObject json = new JsonObject();
        json.addProperty("script",script);

        String url = "/cloudml/executeScript/";
        Response response = given().body(json.toString()).post(url).andReturn();
        response.getBody().prettyPrint();
        JsonObject result = JsonParser.parseString(response.getBody().asString()).getAsJsonObject();
        JsonUtil.print(result);
        assertTrue(result.has("output"));
    }

    @Test
    public void executeScriptMissingScript() throws Exception
    {
        JsonObject json = new JsonObject();

        String url = "/cloudml/executeScript/";
        Response response = given().body(json.toString()).post(url).andReturn();
        response.getBody().prettyPrint();
        JsonObject result = JsonParser.parseString(response.getBody().asString()).getAsJsonObject();
        JsonUtil.print(result);
        assertEquals(403,response.getStatusCode());
    }

    @Test
    public void executeScriptWithException() throws Exception
    {
        String script = IOUtils.toString(Thread.currentThread().getContextClassLoader().
                        getResourceAsStream("scripting/simpleScriptWithException.ml"),
                StandardCharsets.UTF_8);
        JsonObject json = new JsonObject();
        json.addProperty("script",script);

        String url = "/cloudml/executeScript/";
        Response response = given().body(json.toString()).post(url).andReturn();
        response.getBody().prettyPrint();
        JsonObject result = JsonParser.parseString(response.getBody().asString()).getAsJsonObject();
        JsonUtil.print(result);
        assertTrue(result.has("output"));
        assertTrue(result.has("exception"));
    }
}