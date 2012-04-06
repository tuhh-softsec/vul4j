package hudson.plugins.ccm.tokens;

import hudson.Extension;
import hudson.plugins.analysis.tokens.AbstractResultTokenMacro;
import hudson.plugins.ccm.CcmMavenResultAction;
import hudson.plugins.ccm.CcmResultAction;

/**
 * Provides a token that evaluates to the CCM build result.
 *
 * @author Ulli Hafner
 */
@Extension(optional = true)
public class CcmResultTokenMacro extends AbstractResultTokenMacro {
    /**
     * Creates a new instance of {@link CcmResultTokenMacro}.
     */
    @SuppressWarnings("unchecked")
    public CcmResultTokenMacro() {
        super("CCM_RESULT", CcmResultAction.class, CcmMavenResultAction.class);
    }
}

