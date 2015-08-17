package com.epam.jenkins.deployment.sphere.plugin.metadata.model;

import java.util.HashSet;
import java.util.Set;

import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;
import lombok.Data;

@Data
@ExportedBean
public class MetaData {
    @Exported
    private Set<CommitMetaData> commits = new HashSet<>();
    @Exported
    private Set<JiraIssueMetaData> tickets = new HashSet<>();
}
