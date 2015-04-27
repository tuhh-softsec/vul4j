package org.jenkinsci.plugins.IBM_zOS_Connector;


import hudson.model.Run;
import hudson.scm.ChangeLogParser;
import hudson.scm.RepositoryBrowser;
import hudson.util.Digester2;
import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * <h1>SCLMChangeLogParser</h1>
 *
 * Parser of changelog.xml from SCLM.
 *
 * @author <a href="mailto:candiduslynx@gmail.com">Alexander Shcherbakov</a>
 *
 * @version 1.0
 *
 * @see SCLMChangeLogSet
 * @see LogSet
 * @see ChangeLogParser
 */
public class SCLMChangeLogParser extends ChangeLogParser {
    /**
     * Parse changelog.xml.
     *
     * @param build Current build.
     * @param browser Current browser.
     * @param changelogFile changelog.xml
     *
     * @return SCLMChangeLogSet from changelog.xml.
     *
     * @throws IOException
     * @throws SAXException
     */
    @Override
    public SCLMChangeLogSet parse(Run build, RepositoryBrowser<?> browser,
                                                            File changelogFile)
        throws IOException,
        SAXException
    {
        Digester digester = new Digester2();

        // Parse fields.
        digester.addObjectCreate("*/changelog", LogSet.class);
        digester.addSetProperties("*/changelog");
        digester.addObjectCreate("*/changelog/entry", LogSet.Entry.class);
        digester.addSetProperties("*/changelog/entry");
        digester.addBeanPropertySetter("*/changelog/entry/date", "changeDate");
        digester.addBeanPropertySetter("*/changelog/entry/project");
        digester.addBeanPropertySetter("*/changelog/entry/alternate");
        digester.addBeanPropertySetter("*/changelog/entry/group");
        digester.addBeanPropertySetter("*/changelog/entry/type");
        digester.addBeanPropertySetter("*/changelog/entry/name");
        digester.addBeanPropertySetter("*/changelog/entry/version");
        digester.addBeanPropertySetter("*/changelog/entry/userID");
        digester.addBeanPropertySetter("*/changelog/entry/changeGroup");
        digester.addBeanPropertySetter("*/changelog/entry/editType");
        digester.addSetNext("*/changelog/entry", "addEntry");

        // Do the actual parsing
        FileReader reader = new FileReader(changelogFile);
        LogSet temp = (LogSet)digester.parse(reader);
        reader.close();

        // Convert to SCLMChangeLogSet
        SCLMChangeLogSet res = new SCLMChangeLogSet(build,browser);
        res.fromLogSet(temp);
        return res;
    }
}
