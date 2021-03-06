package com.appgallabs.cloudmlplatform.datascience.model;

import com.google.gson.JsonObject;

public interface PortableAIModelInterface
{
    public String getModelId();
    public void setModelId(String modelId);
    public void load(String encodedModelString);
    public void unload();
    public double calculate();
    public JsonObject toJson();
}