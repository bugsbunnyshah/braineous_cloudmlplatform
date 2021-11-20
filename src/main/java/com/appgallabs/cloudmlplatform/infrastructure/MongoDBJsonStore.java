package com.appgallabs.cloudmlplatform.infrastructure;

import com.appgallabs.dataplatform.configuration.AIPlatformConfig;
import com.appgallabs.dataplatform.infrastructure.Tenant;
import com.appgallabs.cloudmlplatform.datascience.model.Project;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.client.*;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.*;

@Singleton
public class MongoDBJsonStore implements Serializable
{
    private static Logger logger = LoggerFactory.getLogger(MongoDBJsonStore.class);

    @Inject
    private AIPlatformConfig aiPlatformConfig;

    @Inject
    private ProjectStore projectStore;

    private MongoClient mongoClient;
    private Map<String,MongoDatabase> databaseMap;

    public MongoDBJsonStore()
    {
        this.databaseMap = new HashMap<>();
    }

    @PostConstruct
    public void start()
    {
        try {
            JsonObject config = this.aiPlatformConfig.getConfiguration();

            //mongodb://[username:password@]host1[:port1][,host2[:port2],...[,hostN[:portN]]][/[database][?options]]
            StringBuilder connectStringBuilder = new StringBuilder();
            connectStringBuilder.append("mongodb://");

            String mongodbHost = config.get("mongodbHost").getAsString();
            long mongodbPort = config.get("mongodbPort").getAsLong();
            if (config.has("mongodbUser") && config.has("mongodbPassword")) {
                connectStringBuilder.append(config.get("mongodbUser").getAsString()
                        + ":" + config.get("mongodbPassword").getAsString() + "@");
            }
            connectStringBuilder.append(mongodbHost + ":" + mongodbPort);

            String connectionString = connectStringBuilder.toString();
            this.mongoClient = MongoClients.create(connectionString);
        }
        catch(Exception e)
        {
            this.mongoClient = null;
        }
    }

    @PreDestroy
    public void stop()
    {
        this.mongoClient.close();
    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }
    //-----------------------------------------------------------------------------
    public String storeModel(Tenant tenant, JsonObject modelPackage)
    {
        String principal = tenant.getPrincipal();
        String databaseName = principal + "_" + "aiplatform";
        MongoDatabase database = mongoClient.getDatabase(databaseName);

        MongoCollection<Document> collection = database.getCollection("aimodels");
        String modelId = UUID.randomUUID().toString();
        modelPackage.addProperty("modelId", modelId);
        Document doc = Document.parse(modelPackage.toString());
        collection.insertOne(doc);

        return modelId;
    }

    public String getModel(Tenant tenant, String modelId)
    {
        String principal = tenant.getPrincipal();
        String databaseName = principal + "_" + "aiplatform";
        MongoDatabase database = mongoClient.getDatabase(databaseName);

        MongoCollection<Document> collection = database.getCollection("aimodels");

        String queryJson = "{\"modelId\":\""+modelId+"\"}";
        Bson bson = Document.parse(queryJson);
        FindIterable<Document> iterable = collection.find(bson);
        MongoCursor<Document> cursor = iterable.cursor();
        if(cursor.hasNext())
        {
            Document document = cursor.next();
            String documentJson = document.toJson();
            return JsonParser.parseString(documentJson).getAsJsonObject().get("model").getAsString();
        }
        return null;
    }

    public JsonObject getModelPackage(Tenant tenant, String modelId)
    {
        String principal = tenant.getPrincipal();
        String databaseName = principal + "_" + "aiplatform";
        MongoDatabase database = mongoClient.getDatabase(databaseName);

        MongoCollection<Document> collection = database.getCollection("aimodels");

        String queryJson = "{\"modelId\":\""+modelId+"\"}";
        Bson bson = Document.parse(queryJson);
        FindIterable<Document> iterable = collection.find(bson);
        MongoCursor<Document> cursor = iterable.cursor();
        if(cursor.hasNext())
        {
            Document document = cursor.next();
            String documentJson = document.toJson();
            return JsonParser.parseString(documentJson).getAsJsonObject();
        }
        return null;
    }

    public void deployModel(Tenant tenant, String modelId)
    {
        String principal = tenant.getPrincipal();
        String databaseName = principal + "_" + "aiplatform";
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        MongoCollection<Document> collection = database.getCollection("aimodels");

        JsonObject currentModel = this.getModelPackage(tenant,modelId);
        Bson bson = Document.parse(currentModel.toString());
        collection.deleteOne(bson);

        currentModel.remove("_id");
        currentModel.addProperty("live", true);
        this.storeLiveModel(tenant,currentModel);
    }

    public void undeployModel(Tenant tenant, String modelId)
    {
        String principal = tenant.getPrincipal();
        String databaseName = principal + "_" + "aiplatform";
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        MongoCollection<Document> collection = database.getCollection("aimodels");

        JsonObject currentModel = this.getModelPackage(tenant,modelId);
        Bson bson = Document.parse(currentModel.toString());
        collection.deleteOne(bson);

        currentModel.remove("_id");
        currentModel.addProperty("live", false);
        this.storeLiveModel(tenant,currentModel);
    }

    private void storeLiveModel(Tenant tenant, JsonObject modelPackage)
    {
        String principal = tenant.getPrincipal();
        String databaseName = principal + "_" + "aiplatform";
        MongoDatabase database = mongoClient.getDatabase(databaseName);

        MongoCollection<Document> collection = database.getCollection("aimodels");
        Document doc = Document.parse(modelPackage.toString());
        collection.insertOne(doc);
    }
    //DataLake related operations----------------------------------------------------------------
    public String storeTrainingDataSet(Tenant tenant, JsonObject dataSetJson)
    {
        String principal = tenant.getPrincipal();
        String databaseName = principal + "_" + "aiplatform";
        MongoDatabase database = mongoClient.getDatabase(databaseName);

        MongoCollection<Document> collection = database.getCollection("dataset");

        String oid = UUID.randomUUID().toString();
        dataSetJson.addProperty("dataSetId", oid);
        dataSetJson.addProperty("dataSetType", "training");
        Document doc = Document.parse(dataSetJson.toString());
        collection.insertOne(doc);

        return oid;
    }

    public String storeTrainingDataSetInLake(Tenant tenant, JsonObject dataSetJson)
    {
        String principal = tenant.getPrincipal();
        String databaseName = principal + "_" + "aiplatform";
        MongoDatabase database = mongoClient.getDatabase(databaseName);

        MongoCollection<Document> collection = database.getCollection("datalake");

        String oid = UUID.randomUUID().toString();
        dataSetJson.addProperty("dataSetId", oid);
        dataSetJson.addProperty("dataSetType", "training");
        Document doc = Document.parse(dataSetJson.toString());
        collection.insertOne(doc);

        return oid;
    }

    public String storeEvalDataSet(Tenant tenant, JsonObject dataSetJson)
    {
        String principal = tenant.getPrincipal();
        String databaseName = principal + "_" + "aiplatform";
        MongoDatabase database = mongoClient.getDatabase(databaseName);

        MongoCollection<Document> collection = database.getCollection("dataset");

        String oid = UUID.randomUUID().toString();
        dataSetJson.addProperty("dataSetId", oid);
        dataSetJson.addProperty("dataSetType", "evaluation");
        Document doc = Document.parse(dataSetJson.toString());
        collection.insertOne(doc);

        return oid;
    }

    public JsonObject readDataSet(Tenant tenant, String dataSetId)
    {
        String principal = tenant.getPrincipal();
        String databaseName = principal + "_" + "aiplatform";
        MongoDatabase database = mongoClient.getDatabase(databaseName);

        MongoCollection<Document> collection = database.getCollection("dataset");

        String queryJson = "{\"dataSetId\":\""+dataSetId+"\"}";
        Bson bson = Document.parse(queryJson);
        FindIterable<Document> iterable = collection.find(bson);
        MongoCursor<Document> cursor = iterable.cursor();
        if(cursor.hasNext())
        {
            Document document = cursor.next();
            String documentJson = document.toJson();

            JsonObject cour = JsonParser.parseString(documentJson).getAsJsonObject();
            return cour;
        }

        return null;
    }

    public JsonObject rollOverToTraningDataSets(Tenant tenant, String modelId)
    {
        JsonObject rolledOverDataSetIds = new JsonObject();

        JsonArray dataSetIds = new JsonArray();
        String dataSettype = "training";
        String principal = tenant.getPrincipal();
        String databaseName = principal + "_" + "aiplatform";
        MongoDatabase database = mongoClient.getDatabase(databaseName);

        MongoCollection<Document> collection = database.getCollection("dataset");

        String queryJson = "{\"modelId\":\""+modelId+"\"}";
        Bson bson = Document.parse(queryJson);
        FindIterable<Document> iterable = collection.find(bson);
        MongoCursor<Document> cursor = iterable.cursor();
        while(cursor.hasNext())
        {
            Document document = cursor.next();
            String documentJson = document.toJson();
            JsonObject dataSetJson = JsonParser.parseString(documentJson).getAsJsonObject();
            dataSetJson.remove("_id");
            dataSetJson.addProperty("dataSetType", "training");
            collection.insertOne(Document.parse(dataSetJson.toString()));
            dataSetIds.add(dataSetJson.get("dataSetId").getAsString());
        }

        rolledOverDataSetIds.add("rolledOverDataSetIds", dataSetIds);
        return rolledOverDataSetIds;
    }
    //------
    public List<Project> readProjects(Tenant tenant){
        return this.projectStore.readProjects(tenant,this.mongoClient);
    }

    public Project readProject(Tenant tenant, String projectId){
        return this.projectStore.readProject(tenant,this.mongoClient,projectId);
    }

    public void addProject(Tenant tenant, Project project){
        this.projectStore.addProject(tenant,this.mongoClient,project);
    }

    public void updateProject(Tenant tenant, Project project){
        this.projectStore.updateProject(tenant,this.mongoClient,project);
    }
}
