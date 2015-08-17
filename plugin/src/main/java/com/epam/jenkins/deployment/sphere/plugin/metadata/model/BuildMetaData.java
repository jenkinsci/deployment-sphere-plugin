package com.epam.jenkins.deployment.sphere.plugin.metadata.model;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
@ExportedBean
public class BuildMetaData extends ApplicationMetaData {

    @Exported
    private Long number;
    @Exported
    private String jobName;
    @Exported
    private String builtAt;
    @Exported
    private String buildVersion;
    @Exported
    private String buildUrl;
    @Exported
    private MetaData metaData;

    @Override
    public String toString() {
        String toString = null;
        try {
            toString = new ObjectMapper().writeValueAsString(this);
        } catch (IOException e) {
        }
        return toString;
    }
}
