package hudson.plugins.ccm.tokens;

import hudson.Extension;
import hudson.plugins.analysis.tokens.AbstractFixedAnnotationsTokenMacro;
import hudson.plugins.ccm.CcmMavenResultAction;
import hudson.plugins.ccm.CcmResultAction;

/**
 * Provides a token that evaluates to the number of fixed CCM warnings.
 */
@Extension(optional = true)
public class FixedCcmWarningsTokenMacro extends AbstractFixedAnnotationsTokenMacro {
    /**
     * Creates a new instance of {@link FixedCcmWarningsTokenMacro}.
     */
    @SuppressWarnings("unchecked")
    public FixedCcmWarningsTokenMacro() {
        super("CCM_FIXED", CcmResultAction.class, CcmMavenResultAction.class);
    }
}

