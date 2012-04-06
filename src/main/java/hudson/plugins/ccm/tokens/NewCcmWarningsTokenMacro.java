package hudson.plugins.ccm.tokens;

import hudson.Extension;
import hudson.plugins.analysis.tokens.AbstractNewAnnotationsTokenMacro;
import hudson.plugins.ccm.CcmMavenResultAction;
import hudson.plugins.ccm.CcmResultAction;


/**
 * Provides a token that evaluates to the number of new CCM warnings.
 **/
@Extension(optional = true)
public class NewCcmWarningsTokenMacro extends AbstractNewAnnotationsTokenMacro {
    /**
     * Creates a new instance of {@link NewCcmWarningsTokenMacro}.
     */
    @SuppressWarnings("unchecked")
    public NewCcmWarningsTokenMacro() {
        super("CCM_NEW", CcmResultAction.class, CcmMavenResultAction.class);
    }
}

