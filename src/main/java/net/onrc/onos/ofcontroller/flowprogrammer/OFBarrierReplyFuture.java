package net.onrc.onos.ofcontroller.flowprogrammer;

import java.util.concurrent.TimeUnit;

import org.openflow.protocol.OFBarrierReply;
import org.openflow.protocol.OFMessage;
import org.openflow.protocol.OFType;

import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.internal.OFMessageFuture;
import net.floodlightcontroller.threadpool.IThreadPoolService;

public class OFBarrierReplyFuture extends OFMessageFuture<OFBarrierReply> {

    protected volatile boolean finished;

    public OFBarrierReplyFuture(IThreadPoolService tp,
            IOFSwitch sw, int transactionId) {
        super(tp, sw, OFType.FEATURES_REPLY, transactionId);
        init();
    }

    public OFBarrierReplyFuture(IThreadPoolService tp,
            IOFSwitch sw, int transactionId, long timeout, TimeUnit unit) {
        super(tp, sw, OFType.FEATURES_REPLY, transactionId, timeout, unit);
        init();
    }

    private void init() {
        this.finished = false;
        this.result = null;
    }

    @Override
    protected void handleReply(IOFSwitch sw, OFMessage msg) {
        this.result = (OFBarrierReply) msg;
        this.finished = true;
    }

    @Override
    protected boolean isFinished() {
        return finished;
    }

    @Override
    protected void unRegister() {
        super.unRegister();
    }
}
