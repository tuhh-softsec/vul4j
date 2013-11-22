/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.onrc.onos.graph;

/**
 *
 * @author nickkaranatsios
 */
public abstract class DBConnection implements IDBConnection {
    public enum Transaction {
        COMMIT, ROLLBACK
    }

    public enum GenerateEvent {
        TRUE, FALSE
    }
}
