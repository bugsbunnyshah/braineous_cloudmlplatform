package com.appgallabs.cloudmlplatform.datascience.endpoint;

import com.appgallabs.cloudmlplatform.datascience.service.CloudMLService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("cloudml")
public class CloudML
{
    private static Logger logger = LoggerFactory.getLogger(CloudML.class);

    private static boolean isPythonDetected = false;

    @Inject
    private CloudMLService cloudMLService;


    @Path("executeScript")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response executeScript(@RequestBody String input){
        try {
            JsonObject json = JsonParser.parseString(input).getAsJsonObject();
            String script = null;
            if(json.has("script")){
               script = json.get("script").getAsString();
            }

            //Validate
            if(script == null){
                JsonObject response = new JsonObject();
                response.addProperty("exception","missing_script");
                return Response.status(403).entity(response.toString()).build();
            }

            JsonObject result = this.cloudMLService.executeScript(script);
            if(result.has("exception")){
                return Response.status(500).entity(result.toString()).build();
            }

            return Response.ok(result.toString()).build();
        }
        catch(Exception e)
        {
            logger.error(e.getMessage(), e);
            JsonObject error = new JsonObject();
            error.addProperty("exception", e.getMessage());
            return Response.status(500).entity(error.toString()).build();
        }
    }
}
