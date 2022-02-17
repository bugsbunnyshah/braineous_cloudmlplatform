package com.appgallabs.cloudmlplatform.infrastructure;

import com.appgallabs.cloudmlplatform.util.JsonUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Singleton
public class PythonEnvironment {
    private static Logger logger = LoggerFactory.getLogger(PythonEnvironment.class);

    public boolean isPythonDetected() {
        return true;
    }

    public String executeTraining(String action, String code){
        try {
            String restUrl = "https://us-south.functions.cloud.ibm.com/api/v1/namespaces/_/actions/"+action+"?overwrite=true";

            //Get Token
            String data = FileUtils.readFileToString(new File("./token"), StandardCharsets.UTF_8);
            int startIndex = data.indexOf("B");
            String bearerToken = data.substring(startIndex).trim();
            System.out.println(bearerToken);

            JsonObject json = new JsonObject();
            json.addProperty("namespace","_");
            json.addProperty("name",action);
            JsonObject exec = new JsonObject();
            exec.addProperty("kind","blackbox");
            exec.addProperty("image","slydogshah/action-python-v3.6-ai");
            exec.addProperty("code",code);
            json.add("exec",exec);

            String payload = json.toString();
            com.appgallabs.dataplatform.util.JsonUtil.printStdOut(json);


            HttpClient httpClient = HttpClient.newBuilder().build();
            HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder();
            HttpRequest httpRequest = httpRequestBuilder.uri(new URI(restUrl))
                    .headers("Content-Type", "application/json","Authorization",bearerToken,"X-Namespace-Id","34f9adfd-d4c1-4674-ae2d-ae772a0f967e")
                    .PUT(HttpRequest.BodyPublishers.ofString(payload))
                    .build();

            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            logger.info(httpResponse.statusCode()+"");
            JsonElement response = JsonParser.parseString(httpResponse.body());
            com.appgallabs.dataplatform.util.JsonUtil.printStdOut(response);

            return this.act(bearerToken,action);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private String act(String bearerToken,String action) throws Exception{
        String restUrl = "https://us-south.functions.cloud.ibm.com/api/v1/namespaces/_/actions/"+action+"?blocking=false&result=true";


        String body = "";
        HttpClient httpClient = HttpClient.newBuilder().build();
        HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder();
        HttpRequest httpRequest = httpRequestBuilder.uri(new URI(restUrl))
                .headers("Content-Type", "application/json","Authorization",bearerToken,"X-Namespace-Id","34f9adfd-d4c1-4674-ae2d-ae772a0f967e")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        logger.info(httpResponse.statusCode()+"");
        JsonObject json = JsonParser.parseString(httpResponse.body()).getAsJsonObject();
        com.appgallabs.dataplatform.util.JsonUtil.printStdOut(json);

        return json.get("activationId").getAsString();
    }

    public String readActivation(String activation) throws Exception{
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
        //logger.info(httpResponse.body());


        JsonObject json = JsonParser.parseString(httpResponse.body()).getAsJsonObject();
        JsonUtil.printStdOut(json);

        String payload = json.get("response").getAsJsonObject().get("result").getAsJsonObject().get("payload").getAsString();
        return payload;
    }
}
