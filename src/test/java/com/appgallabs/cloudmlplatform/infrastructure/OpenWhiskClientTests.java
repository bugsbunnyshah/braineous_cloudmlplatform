package com.appgallabs.cloudmlplatform.infrastructure;

import com.appgallabs.dataplatform.util.JsonUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.quarkus.test.junit.QuarkusTest;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class OpenWhiskClientTests {
    private static Logger logger = LoggerFactory.getLogger(OpenWhiskClientTests.class);


    @Test
    public void executeWskApp() throws Exception{
        //Get Token
        String data = FileUtils.readFileToString(new File("./token"), StandardCharsets.UTF_8);
        int startIndex = data.indexOf("B");
        String bearerToken = data.substring(startIndex).trim();
        System.out.println(bearerToken);


        String code = IOUtils.toString(Thread.currentThread().getContextClassLoader().getResourceAsStream("dataScience/wskApp.py"),
                StandardCharsets.UTF_8);
        String action = "wskApp";
        String restUrl = "https://us-south.functions.cloud.ibm.com/api/v1/namespaces/_/actions/"+action+"?overwrite=true";
        JsonObject json = new JsonObject();
        json.addProperty("namespace","_");
        json.addProperty("name",action);
        JsonObject exec = new JsonObject();
        exec.addProperty("kind","blackbox");
        exec.addProperty("image","slydogshah/action-python-v3.6-ai");
        exec.addProperty("code",code);
        json.add("exec",exec);

        String payload = json.toString();


        HttpClient httpClient = HttpClient.newBuilder().build();
        HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder();
        HttpRequest httpRequest = httpRequestBuilder.uri(new URI(restUrl))
                //.headers("X-Namespace-Id","34f9adfd-d4c1-4674-ae2d-ae772a0f967e", "Authorization",bearerToken,"Content-Type", "application/json")
                .headers("Content-Type", "application/json","Authorization",bearerToken,"X-Namespace-Id","34f9adfd-d4c1-4674-ae2d-ae772a0f967e")
                .PUT(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        logger.info(httpResponse.statusCode()+"");
        JsonElement response = JsonParser.parseString(httpResponse.body());
        JsonUtil.printStdOut(response);
        assertEquals(200,httpResponse.statusCode());

        this.act(bearerToken,action);
    }

    @Test
    public void updateWskApp() throws Exception{
        String action = "noOp";

        String data = FileUtils.readFileToString(new File("./token"), StandardCharsets.UTF_8);
        int startIndex = data.indexOf("B");
        String bearerToken = data.substring(startIndex).trim();
        System.out.println(bearerToken);

        String restUrl = "https://us-south.functions.cloud.ibm.com/api/v1/namespaces/_/actions/"+action+"?overwrite=true";

        String payload = "{\"namespace\":\"_\",\"name\":\"noOp\",\"exec\":{\"kind\":\"blackbox\",\"code\":\"def main(args):\\n    return {\\\"body\\\": \\\"action\\\"}\",\"image\":\"slydogshah/action-python-v3.6-ai\"}}";

        HttpClient httpClient = HttpClient.newBuilder().build();
        HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder();
        HttpRequest httpRequest = httpRequestBuilder.uri(new URI(restUrl))
                //.headers("X-Namespace-Id","34f9adfd-d4c1-4674-ae2d-ae772a0f967e", "Authorization",bearerToken,"Content-Type", "application/json")
                .headers("Content-Type", "application/json","Authorization",bearerToken,"X-Namespace-Id","34f9adfd-d4c1-4674-ae2d-ae772a0f967e")
                .PUT(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        logger.info(httpResponse.statusCode()+"");
        JsonElement json = JsonParser.parseString(httpResponse.body());
        JsonUtil.printStdOut(json);
        assertEquals(200,httpResponse.statusCode());

        this.act(bearerToken,action);
    }

    private void act(String bearerToken,String action) throws Exception{
        String restUrl = "https://us-south.functions.cloud.ibm.com/api/v1/namespaces/_/actions/"+action+"?blocking=false&result=true";

        JsonObject payload = new JsonObject();
        payload.addProperty("name","wskapp_input");

        String body = payload.toString();
        HttpClient httpClient = HttpClient.newBuilder().build();
        HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder();
        HttpRequest httpRequest = httpRequestBuilder.uri(new URI(restUrl))
                .headers("Content-Type", "application/json","Authorization",bearerToken,"X-Namespace-Id","34f9adfd-d4c1-4674-ae2d-ae772a0f967e")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        logger.info(httpResponse.statusCode()+"");
        JsonObject json = JsonParser.parseString(httpResponse.body()).getAsJsonObject();
        JsonUtil.printStdOut(json);

        this.readActivation(json.get("activationId").getAsString());
    }

    private void readActivation(String activation) throws Exception{
        Thread.sleep(10000);

        //activation = "c0082db281bd4cb2882db281bd5cb2c5";
        String restUrl = "https://us-south.functions.cloud.ibm.com/api/v1/namespaces/_/activations/"+activation;

        String data = FileUtils.readFileToString(new File("./token"), StandardCharsets.UTF_8);
        int startIndex = data.indexOf("B");
        String bearerToken = data.substring(startIndex).trim();
        System.out.println(bearerToken);

        HttpClient httpClient = HttpClient.newBuilder().build();
        HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder();
        HttpRequest httpRequest = httpRequestBuilder.uri(new URI(restUrl))
                .header("Authorization",bearerToken)
                .header("X-Namespace-Id","34f9adfd-d4c1-4674-ae2d-ae772a0f967e")
                .GET()
                .build();

        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        logger.info(httpResponse.statusCode()+"");
        JsonElement json = JsonParser.parseString(httpResponse.body());
        JsonUtil.printStdOut(json);
        assertEquals(200,httpResponse.statusCode());
    }

    @Test
    public void invokeAction() throws Exception{
        String data = FileUtils.readFileToString(new File("./token"), StandardCharsets.UTF_8);
        int startIndex = data.indexOf("B");
        String bearerToken = data.substring(startIndex).trim();
        System.out.println(bearerToken);

        String restUrl = "https://us-south.functions.cloud.ibm.com/api/v1/namespaces/_/actions/myAction?blocking=true&result=true";

        HttpClient httpClient = HttpClient.newBuilder().build();
        HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder();
        HttpRequest httpRequest = httpRequestBuilder.uri(new URI(restUrl))
                .header("Authorization",bearerToken)
                .header("X-Namespace-Id","34f9adfd-d4c1-4674-ae2d-ae772a0f967e")
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .build();

        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        logger.info(httpResponse.statusCode()+"");
        JsonElement json = JsonParser.parseString(httpResponse.body());
        JsonUtil.printStdOut(json);
        assertEquals(200,httpResponse.statusCode());
    }

    //@Test
    public void getAllActions() throws Exception{
        String restUrl = "https://us-south.functions.cloud.ibm.com/api/v1/namespaces/_/actions?limit=30&skip=0";

        String data = FileUtils.readFileToString(new File("/Users/babyboy/mamasboy/appgallabs/braineous/cloudmlplatform/arjun/braineous_cloudmlplatform/token"), StandardCharsets.UTF_8);
        int startIndex = data.indexOf("B");
        String bearerToken = data.substring(startIndex).trim();
        System.out.println(bearerToken);

        HttpClient httpClient = HttpClient.newBuilder().build();
        HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder();
        HttpRequest httpRequest = httpRequestBuilder.uri(new URI(restUrl))
                .header("Authorization",bearerToken)
                .header("X-Namespace-Id","34f9adfd-d4c1-4674-ae2d-ae772a0f967e")
                .GET()
                .build();

        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        logger.info(httpResponse.statusCode()+"");
        JsonElement json = JsonParser.parseString(httpResponse.body());
        JsonUtil.printStdOut(json);
        assertEquals(200,httpResponse.statusCode());

        //this.act(bearerToken);
    }
}
