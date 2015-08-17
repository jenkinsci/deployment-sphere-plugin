package com.epam.jenkins.deployment.sphere.plugin.listener;

import java.io.IOException;
import java.util.List;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Descriptor;
import hudson.model.Item;
import hudson.model.ParameterDefinition;
import hudson.model.ParametersDefinitionProperty;
import hudson.model.listeners.ItemListener;
import hudson.tasks.Publisher;
import hudson.util.DescribableList;

import com.epam.jenkins.deployment.sphere.plugin.DeployVersionMetaDataPublisher;
import com.epam.jenkins.deployment.sphere.plugin.metadata.Constants;
import com.epam.jenkins.deployment.sphere.plugin.parameter.DeployMetaDataParameterDefinition;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;

@Extension
public class DeployVersionMetaDataListener extends ItemListener {

    @Override
    public void onCreated(Item item) {
        configureJobWithDeployVersionPublisher(item);
    }

    @Override
    public void onUpdated(Item item) {
        configureJobWithDeployVersionPublisher(item);
    }

    private void configureJobWithDeployVersionPublisher(Item item) {
        if (isAbstractProject(item)) {
            AbstractProject project = (AbstractProject) item;
            DescribableList<Publisher, Descriptor<Publisher>> publishers = project.getPublishersList();
            DeployVersionMetaDataPublisher dataPublisher = publishers.get(DeployVersionMetaDataPublisher.class);
            if (null != dataPublisher) {
                savePublisherProperty(project);
            }
        }
    }

    private boolean isAbstractProject(Item item) {
        return item instanceof AbstractProject;
    }

    private boolean isExistDeployVersionParameter(ParametersDefinitionProperty paramsDefinitionProperty) {
        return (findDeployMetaDataParameter(paramsDefinitionProperty) != null);
    }

    private DeployMetaDataParameterDefinition findDeployMetaDataParameter(ParametersDefinitionProperty paramsDefinitionProperty) {
        if (null != paramsDefinitionProperty) {
            for (ParameterDefinition parameterDefinition : paramsDefinitionProperty.getParameterDefinitions()) {
                if (parameterDefinition instanceof DeployMetaDataParameterDefinition)
                    return (DeployMetaDataParameterDefinition) parameterDefinition;
            }
        }
        return null;
    }

    private void savePublisherProperty(AbstractProject project) {
        ParametersDefinitionProperty paramDefProp = (ParametersDefinitionProperty) project.getProperty(ParametersDefinitionProperty.class);
        if (isExistDeployVersionParameter(paramDefProp)) {
            updatePublisherProperty(project);
        } else {
            addPublisherProperty(project);
        }
    }

    @SuppressWarnings("unchecked")
    private void addPublisherProperty(AbstractProject project) {
        try {
            ParametersDefinitionProperty parametersDefinitionProperty = (ParametersDefinitionProperty) project.getProperty(ParametersDefinitionProperty.class);
            List<ParameterDefinition> definitions = Lists.newArrayList();
            if (null != parametersDefinitionProperty) {
                definitions = parametersDefinitionProperty.getParameterDefinitions();
            }
            DescribableList<Publisher, Descriptor<Publisher>> publishers = project.getPublishersList();
            DeployVersionMetaDataPublisher dataPublisher = publishers.get(DeployVersionMetaDataPublisher.class);
            DeployMetaDataParameterDefinition deployMetaData = new DeployMetaDataParameterDefinition(Constants.DEPLOY_META_DATA, Constants.DEPLOY_META_DATA, dataPublisher.getDeployedAppName());
            definitions.add(deployMetaData);
            project.addProperty(new ParametersDefinitionProperty(definitions));
            project.save();
        } catch (IOException e) {
            Throwables.propagate(e);
        }
    }

    @SuppressWarnings("unchecked")
    private void updatePublisherProperty(AbstractProject prj) {
        try {
            ParametersDefinitionProperty parametersDefinitionProperty = (ParametersDefinitionProperty) prj.getProperty(ParametersDefinitionProperty.class);
            if (null != parametersDefinitionProperty) {
                List<ParameterDefinition> definitions = Lists.newArrayList();
                for (ParameterDefinition parameterDefinition : parametersDefinitionProperty.getParameterDefinitions()) {
                    if (!(parameterDefinition instanceof DeployMetaDataParameterDefinition)) {
                        definitions.add(parameterDefinition);
                    }
                }
                prj.removeProperty(ParametersDefinitionProperty.class);
                prj.addProperty(new ParametersDefinitionProperty(definitions));
                prj.save();
                addPublisherProperty(prj);
            }
        } catch (IOException e) {
            Throwables.propagate(e);
        }
    }

}
