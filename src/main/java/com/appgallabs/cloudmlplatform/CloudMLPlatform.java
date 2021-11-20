package com.appgallabs.cloudmlplatform;

import com.appgallabs.dataplatform.ingestion.service.StreamIngesterContext;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public class CloudMLPlatform {
    public static void main(String[] args) throws Exception
    {
        System.out.println("CloudMLPlatform Started...");
        System.out.println("*******************************");
        System.out.println("STARTING_INGESTION");
        System.out.println("*******************************");
        JsonArray array = new JsonArray();
        array.add(new JsonObject());
        StreamIngesterContext.getStreamIngester().submit(null,null, null,null,array);


        Quarkus.run(args);
    }
}
