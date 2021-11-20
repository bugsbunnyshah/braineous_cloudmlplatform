//
package com.appgallabs.cloudmlplatform.datascience.dl4j;

import com.appgallabs.cloudmlplatform.datascience.model.Artifact;
import com.appgallabs.dataplatform.preprocess.SecurityToken;
import org.nd4j.common.loader.Source;
import org.nd4j.common.loader.SourceFactory;

public class AIPlatformDataLakeSourceFactory implements SourceFactory
{
    private SecurityToken securityToken;
    private Artifact artifact;

    public AIPlatformDataLakeSourceFactory(SecurityToken securityToken,Artifact artifact)
    {
        this.securityToken = securityToken;
        this.artifact = artifact;
    }

    @Override
    public Source getSource(String s)
    {
        return new AIPlatformDataLakeSource(this.securityToken,this.artifact,s);
    }
}
