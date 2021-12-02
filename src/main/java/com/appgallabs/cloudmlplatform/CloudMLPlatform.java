package com.appgallabs.cloudmlplatform;

import com.appgallabs.dataplatform.ingestion.service.StreamIngesterContext;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain(name="cloudmlplatform")
public class CloudMLPlatform {
    public static void main(String[] args) throws Exception
    {
        System.out.println("Braineous CloudML Platform Started...");
        System.out.println("*******************************");
        System.out.println("STARTING_INGESTION");
        System.out.println("*******************************");
        StreamIngesterContext.getStreamIngester().start();
        Quarkus.run(args);
    }
}
