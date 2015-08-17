package com.epam.jenkins.deployment.sphere.plugin.mock;

import hudson.model.Run;
import hudson.scm.ChangeLogSet;
import hudson.scm.RepositoryBrowser;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Iterator;

public class EmptyChangeLogSet extends ChangeLogSet<ChangeLogSet.Entry> {
	public EmptyChangeLogSet(Run<?, ?> build) {
        super(build, new RepositoryBrowser<ChangeLogSet.Entry>() {
			private static final long serialVersionUID = 1L;
			@Override public URL getChangeSetLink(ChangeLogSet.Entry changeSet) throws IOException {
                return null;
            }
        });
    }

    @Override
    public boolean isEmptySet() {
        return true;
    }

    public Iterator<Entry> iterator() {
        return Collections.<Entry>emptySet().iterator();
    }
}
