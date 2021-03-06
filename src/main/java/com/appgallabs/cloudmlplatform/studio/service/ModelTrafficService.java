package com.appgallabs.cloudmlplatform.studio.service;

import com.appgallabs.dataplatform.history.service.DataReplayService;
import com.appgallabs.dataplatform.preprocess.SecurityToken;
import com.appgallabs.dataplatform.preprocess.SecurityTokenContainer;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class ModelTrafficService
{
    private static Logger logger = LoggerFactory.getLogger(ModelTrafficService.class);

    @Inject
    private DataReplayService dataReplayService;

    @Inject
    private SecurityTokenContainer securityTokenContainer;

    public Map<String,List<JsonObject>> getModelTraffic()
    {
        SecurityToken securityToken = this.securityTokenContainer.getSecurityToken();
        return null;
    }
}
