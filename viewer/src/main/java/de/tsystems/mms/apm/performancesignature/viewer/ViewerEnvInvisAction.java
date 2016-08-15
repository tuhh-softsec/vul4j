package de.tsystems.mms.apm.performancesignature.viewer;

import hudson.model.InvisibleAction;

class ViewerEnvInvisAction extends InvisibleAction {
    private final int currentBuild;

    ViewerEnvInvisAction(final int currentBuild) {
        this.currentBuild = currentBuild;
    }

    int getCurrentBuild() {
        return currentBuild;
    }
}
