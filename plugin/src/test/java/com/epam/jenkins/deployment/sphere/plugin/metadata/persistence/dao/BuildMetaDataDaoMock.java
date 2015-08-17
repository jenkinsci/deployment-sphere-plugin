package com.epam.jenkins.deployment.sphere.plugin.metadata.persistence.dao;

import java.util.Collection;

import com.epam.jenkins.deployment.sphere.plugin.metadata.model.BuildMetaData;

public class BuildMetaDataDaoMock extends BuildMetaDataDao {
	@Override
	public void save(BuildMetaData buildMetaData) {
	
	}
	@Override
	public BuildMetaData find(String applicationName, String buildVersion) {
		return null;
	}
	
	@Override
	public Collection<BuildMetaData> findByAppName(String applicationName) {
		return null;
	}
}
