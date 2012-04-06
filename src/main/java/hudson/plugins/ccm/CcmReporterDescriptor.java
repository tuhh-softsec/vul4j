package hudson.plugins.ccm;

import hudson.Extension;
import hudson.plugins.analysis.core.ReporterDescriptor;

/**
 * Descriptor for the class {@link CcmReporter}. Used as a singleton.
 *
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 3.0
 */
@Extension(ordinal = 100)
public class CcmReporterDescriptor extends ReporterDescriptor {
    /**
     * Creates a new instance of <code>PmdReporterDescriptor</code>.
     */
    public CcmReporterDescriptor() {
        super(CcmReporter.class, new CcmDescriptor());
    }
}

