package hudson.plugins.ccm.tokens;

import hudson.Extension;
import hudson.plugins.analysis.tokens.AbstractAnnotationsCountTokenMacro;
import hudson.plugins.ccm.CcmMavenResultAction;
import hudson.plugins.ccm.CcmResultAction;

/**
 * Provides a token that evaluates to the number of CCM warnings.
 */
@Extension(optional = true)
public class CcmWarningCountTokenMacro extends AbstractAnnotationsCountTokenMacro {
    /**
     * Creates a new instance of {@link CcmWarningCountTokenMacro}.
     */
    @SuppressWarnings("unchecked")
    public CcmWarningCountTokenMacro() {
        super("CCM_COUNT", CcmResultAction.class, CcmMavenResultAction.class);
    }
}

