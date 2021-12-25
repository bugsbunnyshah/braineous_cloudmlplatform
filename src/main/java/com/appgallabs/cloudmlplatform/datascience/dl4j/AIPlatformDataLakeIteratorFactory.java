package com.appgallabs.cloudmlplatform.datascience.dl4j;

import com.appgallabs.cloudmlplatform.datascience.model.Artifact;
import com.appgallabs.dataplatform.preprocess.SecurityToken;
import com.appgallabs.dataplatform.preprocess.SecurityTokenContainer;
import com.google.gson.JsonArray;
import org.deeplearning4j.datasets.iterator.loader.DataSetLoaderIterator;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collection;

@ApplicationScoped
public class AIPlatformDataLakeIteratorFactory
{
    @Inject
    private AIPlatformDataLakeLoader aiPlatformDataLakeLoader;

    @Inject
    private SecurityTokenContainer securityTokenContainer;

    public SecurityTokenContainer getSecurityTokenContainer() {
        return securityTokenContainer;
    }

    public void setSecurityTokenContainer(SecurityTokenContainer securityTokenContainer) {
        this.securityTokenContainer = securityTokenContainer;
    }

    public AIPlatformDataLakeLoader getAiPlatformDataLakeLoader() {
        return aiPlatformDataLakeLoader;
    }

    public void setAiPlatformDataLakeLoader(AIPlatformDataLakeLoader aiPlatformDataLakeLoader) {
        this.aiPlatformDataLakeLoader = aiPlatformDataLakeLoader;
    }

    public DataSetIterator getInstance(Artifact artifact,String[] dataSetIds)
    {
        SecurityToken securityToken = this.securityTokenContainer.getSecurityToken();
        AIPlatformDataLakeSourceFactory sourceFactory = new AIPlatformDataLakeSourceFactory(securityToken, artifact);

        JsonArray array = new JsonArray();
        for(String dataSetId:dataSetIds)
        {
            array.add(dataSetId);
        }
        Collection<String> paths = Arrays.asList(array.toString());

        final DataSetLoaderIterator dataLakeLoaderIterator = new DataSetLoaderIterator(paths,
                this.aiPlatformDataLakeLoader, sourceFactory);

        return dataLakeLoaderIterator;
    }
}
