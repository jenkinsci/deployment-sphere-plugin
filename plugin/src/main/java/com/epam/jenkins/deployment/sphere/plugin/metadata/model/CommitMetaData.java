package com.epam.jenkins.deployment.sphere.plugin.metadata.model;

import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;
import lombok.Data;

@Data
@ExportedBean
public class CommitMetaData {
    @Exported
    private String author;
    @Exported
    private String id;
    @Exported
    private String message;
}
