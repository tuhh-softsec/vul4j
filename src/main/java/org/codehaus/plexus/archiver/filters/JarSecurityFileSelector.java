package org.codehaus.plexus.archiver.filters;

/*
 * Copyright 2007 The Codehaus Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
import java.io.IOException;

import org.codehaus.plexus.components.io.fileselectors.FileInfo;
import org.codehaus.plexus.components.io.fileselectors.FileSelector;
import org.codehaus.plexus.util.SelectorUtils;

import javax.annotation.Nonnull;

/**
 * @version $Id$
 * @since 1.0-alpha-9
 */
public class JarSecurityFileSelector
    implements FileSelector
{
    public static final String ROLE_HINT = "jar-security";

    public static final String[] SECURITY_FILE_PATTERNS = {
        "META-INF/*.RSA",
        "META-INF/*.DSA",
        "META-INF/*.SF",
        "META-INF/*.rsa",
        "META-INF/*.dsa",
        "META-INF/*.sf" };

    public boolean isSelected( @Nonnull FileInfo fileInfo )
        throws IOException
    {
        String name = fileInfo.getName();
		for (String pattern : SECURITY_FILE_PATTERNS) {
			if (SelectorUtils.match(pattern, name)) {
				return false;
			}
		}

        return true;
    }
}
