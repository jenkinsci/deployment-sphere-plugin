package com.epam.jenkins.deployment.sphere.plugin.metadata.model;

import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@ExportedBean
public class DeploymentMetaData extends ApplicationMetaData {
    @Exported
    private String buildVersion;
    @Exported
    private String deployedAt;
    @Exported
    private String environmentKey;
    @Exported
    private BuildMetaData build;
}
