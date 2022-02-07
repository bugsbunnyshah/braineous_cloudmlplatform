package com.appgallabs.cloudmlplatform.infrastructure;

import com.appgallabs.dataplatform.util.JsonUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.quarkus.test.junit.QuarkusTest;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class OpenWhiskClientTests {
    private static Logger logger = LoggerFactory.getLogger(OpenWhiskClientTests.class);


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

    @Test
    public void updateAction() throws Exception{
        String action = "noOp";

        String data = FileUtils.readFileToString(new File("./token"), StandardCharsets.UTF_8);
        int startIndex = data.indexOf("B");
        String bearerToken = data.substring(startIndex).trim();
        System.out.println(bearerToken);

        String restUrl = "https://us-south.functions.cloud.ibm.com/api/v1/namespaces/_/actions/"+action+"?overwrite=true";

        //JsonObject json = new JsonObject();
        //JsonUtil.printStdOut(JsonParser.parseString(payload));
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

        /*String action = "noOp";
        String data = FileUtils.readFileToString(new File("/Users/babyboy/mamasboy/appgallabs/ian/armaan/ian/token"), StandardCharsets.UTF_8);
        int startIndex = data.indexOf("B");
        String bearerToken = data.substring(startIndex).trim();
        System.out.println(bearerToken);

        Process process = Runtime.getRuntime().exec("./updateAction.sh");
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = "";
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }*/

        this.act(bearerToken,action);
    }

    @Test
    public void executeOnWhisk() throws Exception{
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();
        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        System.out.println(generatedString);

        //Get Token
        String data = FileUtils.readFileToString(new File("./token"), StandardCharsets.UTF_8);
        int startIndex = data.indexOf("B");
        String bearerToken = data.substring(startIndex).trim();
        System.out.println(bearerToken);


        String action = generatedString;
        String restUrl = "https://us-south.functions.cloud.ibm.com/api/v1/namespaces/_/actions/"+action+"?overwrite=true";

        //JsonObject json = new JsonObject();
        //JsonUtil.printStdOut(JsonParser.parseString(payload));
        String payload = "{\"namespace\":\"_\",\"name\":\""+action+"\",\"exec\":{\"kind\":\"blackbox\",\"code\":\"def main(args):\\n    return {\\\"body\\\": \\\""+generatedString+"\\\"}\",\"image\":\"slydogshah/action-python-v3.6-ai\"}}";

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

        HttpClient httpClient = HttpClient.newBuilder().build();
        HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder();
        HttpRequest httpRequest = httpRequestBuilder.uri(new URI(restUrl))
                .headers("Authorization",bearerToken,"X-Namespace-Id","34f9adfd-d4c1-4674-ae2d-ae772a0f967e")
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .build();

        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        logger.info(httpResponse.statusCode()+"");
        JsonElement json = JsonParser.parseString(httpResponse.body());
        JsonUtil.printStdOut(json);
    }

    //@Test
    public void readActivation() throws Exception{
        String restUrl = "https://us-south.functions.cloud.ibm.com/api/v1/namespaces/_/activations/d3865230267b45bd865230267b65bdfa";

        String data = FileUtils.readFileToString(new File("/Users/babyboy/mamasboy/appgallabs/ian/armaan/ian/token"), StandardCharsets.UTF_8);
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
}
