package com.epam.jenkins.deployment.sphere.plugin.metadata.persistence.domain;

import java.io.Serializable;

import lombok.Data;

@Data
public class Environment implements Serializable {
    private String key;
    private String title;

}
