package net.webassembletool;

import net.webassembletool.resource.Resource;

public interface ResourceFactory {
	Resource getResource(ResourceContext resourceContext) throws HttpErrorPage;
}
