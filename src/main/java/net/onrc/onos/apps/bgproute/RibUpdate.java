package net.onrc.onos.apps.bgproute;

public class RibUpdate {
    private final Operation operation;
    private final Prefix prefix;
    private final RibEntry ribEntry;

    public enum Operation {
        UPDATE,
        DELETE
    }

    public RibUpdate(Operation operation, Prefix prefix, RibEntry ribEntry) {
        this.operation = operation;
        this.prefix = prefix;
        this.ribEntry = ribEntry;
    }

    public Operation getOperation() {
        return operation;
    }

    public Prefix getPrefix() {
        return prefix;
    }

    public RibEntry getRibEntry() {
        return ribEntry;
    }
}
