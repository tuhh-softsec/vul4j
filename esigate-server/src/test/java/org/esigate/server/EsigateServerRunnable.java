package org.esigate.server;

/**
 * Starts esigate server.
 * 
 * @author Nicolas Richeton
 * 
 */
public final class EsigateServerRunnable implements Runnable {
    @Override
    public void run() {
        EsigateServer.init();
        try {
            EsigateServer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
