package org.esigate;

import org.esigate.resource.Resource;

public interface ResourceFactory {
	Resource getResource(ResourceContext resourceContext) throws HttpErrorPage;
}
