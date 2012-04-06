package hudson.plugins.ccm;

/**
 * Represents the result summary of the PMD parser. This summary will be
 * shown in the summary.jelly script of the PMD result action.
 *
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 3.0
 */
public final class ResultSummary {
    /**
     * Returns the message to show as the result summary.
     *
     * @param result
     *            the result
     * @return the message
     */
    public static String createSummary(final CcmResult result) {
        StringBuilder summary = new StringBuilder();
        int bugs = result.getNumberOfAnnotations();

        summary.append("CCM: ");
        if (bugs > 0) {
            summary.append("<a href=\"ccmResult\">");
        }
        if (bugs == 1) {
            summary.append(Messages.CCM_ResultAction_OneWarning());
        }
        else {
            summary.append(Messages.CCM_ResultAction_MultipleWarnings(bugs));
        }
        if (bugs > 0) {
            summary.append("</a>");
        }
        summary.append(" ");
        if (result.getNumberOfModules() == 1) {
            summary.append(Messages.CCM_ResultAction_OneFile());
        }
        else {
            summary.append(Messages.CCM_ResultAction_MultipleFiles(result.getNumberOfModules()));
        }
        return summary.toString();
    }

    /**
     * Returns the message to show as the result summary.
     *
     * @param result
     *            the result
     * @return the message
     */
    public static String createDeltaMessage(final CcmResult result) {
        StringBuilder summary = new StringBuilder();
        if (result.getNumberOfNewWarnings() > 0) {
            summary.append("<li><a href=\"ccmResult/new\">");
            if (result.getNumberOfNewWarnings() == 1) {
                summary.append(Messages.CCM_ResultAction_OneNewWarning());
            }
            else {
                summary.append(Messages.CCM_ResultAction_MultipleNewWarnings(result.getNumberOfNewWarnings()));
            }
            summary.append("</a></li>");
        }
        if (result.getNumberOfFixedWarnings() > 0) {
            summary.append("<li><a href=\"ccmResult/fixed\">");
            if (result.getNumberOfFixedWarnings() == 1) {
                summary.append(Messages.CCM_ResultAction_OneFixedWarning());
            }
            else {
                summary.append(Messages.CCM_ResultAction_MultipleFixedWarnings(result.getNumberOfFixedWarnings()));
            }
            summary.append("</a></li>");
        }

        return summary.toString();
    }

    /**
     * Instantiates a new result summary.
     */
    private ResultSummary() {
        // prevents instantiation
    }
}

