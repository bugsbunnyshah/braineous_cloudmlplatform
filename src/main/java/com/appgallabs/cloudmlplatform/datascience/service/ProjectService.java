package com.appgallabs.cloudmlplatform.datascience.service;

import com.appgallabs.cloudmlplatform.datascience.model.*;
import com.appgallabs.cloudmlplatform.infrastructure.MongoDBJsonStore;
import com.appgallabs.dataplatform.preprocess.SecurityTokenContainer;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@ApplicationScoped
public class ProjectService {
    private static Logger logger = LoggerFactory.getLogger(ProjectService.class);

    @Inject
    private MongoDBJsonStore mongoDBJsonStore;

    @Inject
    private SecurityTokenContainer securityTokenContainer;

    @Inject
    private AIModelService aiModelService;

    @Inject
    private ModelDataSetService modelDataSetService;

    @PostConstruct
    public void onStart(){

    }

    public Project createArtifactForTraining(String scientist, JsonObject artifactData){
        try
        {
            Project project = new Project();
            project.setProjectId(UUID.randomUUID().toString());
            Artifact artifact = new Artifact();
            artifact.setArtifactId(UUID.randomUUID().toString());
            artifact.setScientist(scientist);

            //Store the AI Model
            //artifactData.addProperty("live", false);
            //String modelId = this.mongoDBJsonStore.storeModel(this.securityTokenContainer.getTenant(),artifactData);

            //Link to a Project
            JsonArray labels = artifactData.get("labels").getAsJsonArray();
            JsonArray features = artifactData.get("features").getAsJsonArray();
            JsonObject parameters = artifactData.get("parameters").getAsJsonObject();

            for(int i=0; i<labels.size(); i++){
                JsonObject local = labels.get(i).getAsJsonObject();
                artifact.addLabel(new Label(local.get("value").getAsString(),local.get("field").getAsString()));
            }

            for(int i=0; i<features.size(); i++){
                JsonObject local = features.get(i).getAsJsonObject();
                artifact.addFeature(new Feature(local.get("value").getAsString()));
            }

            Set<Map.Entry<String,JsonElement>> entrySet = parameters.entrySet();
            for(Map.Entry<String,JsonElement> entry:entrySet){
                artifact.addParameter(entry.getKey(),entry.getValue().getAsString());
            }

            if(artifactData.has("numberOfLabels")) {
                artifact.setNumberOfLabels(artifactData.get("numberOfLabels").getAsInt());
            }

            if(artifactData.has("labelIndex")) {
                artifact.setLabelIndex(artifactData.get("labelIndex").getAsInt());
            }

            project.addArtifact(artifact);
            project.getTeam().addScientist(new Scientist(scientist));

            this.addProject(project);

            return this.readProject(project.getProjectId());
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public void addProject(Project project){
        this.mongoDBJsonStore.addProject(this.securityTokenContainer.getTenant(),project);
    }

    public void addScientist(String projectId,Scientist scientist)
    {
        Project project = this.mongoDBJsonStore.readProject(this.securityTokenContainer.getTenant(),projectId);
        project.getTeam().addScientist(scientist);
        this.mongoDBJsonStore.updateProject(this.securityTokenContainer.getTenant(),project);
    }

    public Artifact getArtifact(String projectId,String artifactId){
        try
        {
            Project project = this.readProject(projectId);
            if(project == null){
                return null;
            }

            Artifact artifact = project.findArtifact(artifactId);
            if(artifact == null){
                return null;
            }

            return artifact;
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public Artifact updateArtifact(String projectId,Artifact artifact){
        try
        {
            Project project = this.readProject(projectId);
            if(project == null){
                return null;
            }

            Artifact current = project.findArtifact(artifact.getArtifactId());
            if(current == null){
                return null;
            }

            project.removeArtifact(artifact);
            project.addArtifact(artifact);
            this.updateProject(project);

            return artifact;
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public Project deleteArtifact(String projectId,String artifactId){
        try
        {
            Project project = this.readProject(projectId);
            if(project == null){
                return null;
            }

            Artifact current = project.findArtifact(artifactId);
            if(current == null){
                return null;
            }

            project.removeArtifact(current);
            project = this.updateProject(project);

            return project;
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public List<Project> readProjects(){
        return this.mongoDBJsonStore.readProjects(this.securityTokenContainer.getTenant());
    }

    public Project readProject(String projectId)
    {
        return this.mongoDBJsonStore.readProject(this.securityTokenContainer.getTenant(),projectId);
    }

    public Project updateProject(Project project){
        Project stored = this.readProject(project.getProjectId());
        if(stored == null){
            return null;
        }

        this.mongoDBJsonStore.updateProject(this.securityTokenContainer.getTenant(),project);

        return project;
    }


    public String getAiModel(String projectId, String artifactId){
        try{
            Project project = this.readProject(projectId);
            List<Artifact> artifacts = project.getArtifacts();
            for(Artifact local:artifacts){
                if(local.getArtifactId().equals(artifactId)){
                    String modelId = local.getAiModel().getModelId();
                    String model = this.mongoDBJsonStore.getModel(this.securityTokenContainer.getTenant(),modelId);
                    return model;
                }
            }
            return null;
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public JsonObject storeAiModel(String projectId, String artifactId, String modelName, String language, String model)
            throws ArtifactNotFoundException{
        Artifact artifact = this.getArtifact(projectId,artifactId);
        if(artifact == null){
            throw new ArtifactNotFoundException("ARTIFACT_NOT_FOUND");
        }

        JsonObject modelPackageJson = new JsonObject();
        modelPackageJson.addProperty("name",modelName);
        modelPackageJson.addProperty("model",model);
        modelPackageJson.addProperty("live", false);
        String modelId = this.mongoDBJsonStore.storeModel(this.securityTokenContainer.getTenant(),modelPackageJson);
        AIModel aiModel = new AIModel();
        aiModel.setModelId(modelId);
        aiModel.setLanguage(language);
        artifact.setAiModel(aiModel);
        this.updateArtifact(projectId,artifact);

        JsonObject result = new JsonObject();
        result.addProperty("projectId",projectId);
        result.addProperty("artifactId",artifactId);
        result.addProperty("modelId",modelId);
        result.addProperty("modelName",modelName);
        result.addProperty("language",language);

        return result;
    }

    public JsonObject trainModelFromDataLake(String projectId, String artifactId,String[] dataLakeIds,int nEpochs)
            throws ArtifactNotFoundException, DataNotFoundException, ModelIsLive, ModelNotFoundException{
        Artifact artifact = this.getArtifact(projectId,artifactId);
        if(artifact == null){
            throw new ArtifactNotFoundException("ARTIFACT_NOT_FOUND");
        }

        if(dataLakeIds == null || dataLakeIds.length == 0){
            throw new DataNotFoundException("DATA_NOT_SPECIFIED");
        }

        PortableAIModelInterface aiModel = artifact.getAiModel();
        String modelId = aiModel.getModelId();
        aiModel.setModelId(modelId);
        DataSet dataSet = new DataSet();
        for(String dataLakeId:dataLakeIds) {
            DataItem dataItem = new DataItem();
            dataItem.setDataLakeId(dataLakeId);
            dataSet.addDataItem(dataItem);
        }
        artifact.setDataSet(dataSet);
        artifact = this.updateArtifact(projectId,artifact);

        JsonObject evaluation = this.aiModelService.trainModelFromDataLake(artifact,nEpochs);
        return evaluation;
    }

    public JsonObject trainModelFromDataSet(String projectId, String artifactId, String[] dataSetIds, int nEpochs)
            throws ArtifactNotFoundException, DataNotFoundException, ModelIsLive, ModelNotFoundException {

        Artifact artifact = this.getArtifact(projectId,artifactId);
        if(artifact == null){
            throw new ArtifactNotFoundException("ARTIFACT_NOT_FOUND");
        }

        if(dataSetIds == null || dataSetIds.length == 0){
            throw new DataNotFoundException("DATA_NOT_SPECIFIED");
        }

        PortableAIModelInterface aiModel = artifact.getAiModel();
        String modelId = aiModel.getModelId();
        aiModel.setModelId(modelId);
        DataSet dataSet = new DataSet();
        for(String dataSetId:dataSetIds) {
            DataItem dataItem = new DataItem();
            dataItem.setDataSetId(dataSetId);
            dataSet.addDataItem(dataItem);
        }
        artifact.setDataSet(dataSet);
        artifact = this.updateArtifact(projectId,artifact);

        JsonObject evaluation = this.aiModelService.trainModelFromDataSet(artifact,nEpochs);
        return evaluation;
    }

    public void deployModel(Artifact artifact){
        try
        {
            this.aiModelService.deployModel(artifact.getAiModel().getModelId());
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public void verifyDeployment(JsonObject payload){
        try{

        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
    }
}
