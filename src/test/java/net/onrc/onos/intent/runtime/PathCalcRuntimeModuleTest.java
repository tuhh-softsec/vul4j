package net.onrc.onos.intent.runtime;

import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.onrc.onos.datagrid.IDatagridService;
import net.onrc.onos.datagrid.IEventChannel;
import net.onrc.onos.datagrid.IEventChannelListener;
import net.onrc.onos.intent.*;
import net.onrc.onos.intent.Intent.IntentState;
import net.onrc.onos.intent.IntentOperation.Operator;
import net.onrc.onos.ofcontroller.networkgraph.INetworkGraphListener;
import net.onrc.onos.ofcontroller.networkgraph.INetworkGraphService;
import net.onrc.onos.registry.controller.IControllerRegistryService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collection;

import static org.easymock.EasyMock.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.hamcrest.Matchers;

/**
 * @author Ray Milkey (ray@onlab.us)
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(PathCalcRuntimeModule.class)
public class PathCalcRuntimeModuleTest {
    private FloodlightModuleContext modContext;
    private IDatagridService datagridService;
    private INetworkGraphService networkGraphService;
    private IControllerRegistryService controllerRegistryService;
    private PersistIntent persistIntent;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        final MockNetworkGraph graph = new MockNetworkGraph();
        graph.createSampleTopology1();

        datagridService = createMock(IDatagridService.class);
        networkGraphService = createMock(INetworkGraphService.class);
        controllerRegistryService = createMock(IControllerRegistryService.class);
        modContext = createMock(FloodlightModuleContext.class);
        final IEventChannel eventChannel = createMock(IEventChannel.class);
        persistIntent = PowerMock.createMock(PersistIntent.class);

        PowerMock.expectNew(PersistIntent.class,
                anyObject(IControllerRegistryService.class),
                anyObject(INetworkGraphService.class)).andReturn(persistIntent);

        expect(modContext.getServiceImpl(IDatagridService.class))
                .andReturn(datagridService).once();
        expect(modContext.getServiceImpl(INetworkGraphService.class))
                .andReturn(networkGraphService).once();
        expect(modContext.getServiceImpl(IControllerRegistryService.class))
                .andReturn(controllerRegistryService).once();
        expect(persistIntent.getKey()).andReturn(1L).anyTimes();
        expect(persistIntent.persistIfLeader(eq(1L),
                anyObject(IntentOperationList.class))).andReturn(true)
                .anyTimes();

        expect(networkGraphService.getNetworkGraph()).andReturn(graph)
                .anyTimes();
        networkGraphService.registerNetworkGraphListener(
                anyObject(INetworkGraphListener.class));
        expectLastCall();

        expect(datagridService.createChannel("onos.pathintent",
                Long.class, IntentOperationList.class))
                .andReturn(eventChannel).once();

        expect(datagridService.addListener(
                eq("onos.pathintent_state"),
                anyObject(IEventChannelListener.class),
                eq(Long.class),
                eq(IntentStateList.class)))
                .andReturn(eventChannel).once();

        replay(datagridService);
        replay(networkGraphService);
        replay(modContext);
        replay(controllerRegistryService);
        PowerMock.replay(persistIntent, PersistIntent.class);
    }

    @After
    public void tearDown() {
        verify(datagridService);
        verify(networkGraphService);
        verify(modContext);
        verify(controllerRegistryService);
        PowerMock.verify(persistIntent, PersistIntent.class);
    }

    /**
     * Test the result of executing a path calculation on an
     * Intent Operation List which contains a path that references a
     * non-existent switch.
     * <p/>
     * A 3 path list is created where one of the paths references a switch
     * that is not in the topology.  The test checks that the resulting
     * Operation List has entries for the 2 correct paths, and that the
     * high level intents contain a proper error entry for the bad path.
     */
    @Test
    public void testInvalidSwitchName() throws FloodlightModuleException {

        final Long LOCAL_PORT = 0xFFFEL;
        final String BAD_SWITCH_INTENT_NAME = "No Such Switch Intent";

        // create shortest path intents
        final IntentOperationList opList = new IntentOperationList();
        opList.add(Operator.ADD,
                new ShortestPathIntent(BAD_SWITCH_INTENT_NAME, 111L, 12L,
                        LOCAL_PORT, 2L, 21L, LOCAL_PORT));
        opList.add(Operator.ADD,
                new ShortestPathIntent("2", 1L, 14L, LOCAL_PORT, 4L, 41L,
                        LOCAL_PORT));
        opList.add(Operator.ADD,
                new ShortestPathIntent("3", 2L, 23L, LOCAL_PORT, 3L, 32L,
                        LOCAL_PORT));

        // compile high-level intent operations into low-level intent
        // operations (calculate paths)
        final PathCalcRuntimeModule runtime = new PathCalcRuntimeModule();
        runtime.init(modContext);
        runtime.startUp(modContext);
        final IntentOperationList pathIntentOpList =
                runtime.executeIntentOperations(opList);
        assertThat(pathIntentOpList, notNullValue());

        final IntentMap highLevelIntents = runtime.getHighLevelIntents();
        assertThat(highLevelIntents, notNullValue());

        final Collection<Intent> allIntents = highLevelIntents.getAllIntents();
        assertThat(allIntents, notNullValue());

        //  One intent had an error and should not create a path list entry
        assertThat(pathIntentOpList, hasSize(opList.size() - 1));

        //  Should be a high level intent for each operation
        assertThat(opList, hasSize(allIntents.size()));

        // Check that we got a high level intent for each operation
        assertThat(allIntents,
                   hasItem(Matchers.<Intent>
                           hasProperty("id", equalTo("3"))));
        assertThat(allIntents,
                   hasItem(Matchers.<Intent>
                           hasProperty("id", equalTo("2"))));
        assertThat(allIntents,
                   hasItem(Matchers.<Intent>
                           hasProperty("id", equalTo(BAD_SWITCH_INTENT_NAME))));

        //  Check that the non existent switch was NACKed
        final Intent intentForBadSwitch =
                highLevelIntents.getIntent(BAD_SWITCH_INTENT_NAME);
        assertThat(intentForBadSwitch, notNullValue());
        assertThat(intentForBadSwitch.getState(),
                is(equalTo(IntentState.INST_NACK)));

        //  Check that switch 2 was correctly processed
        final Intent intentForSwitch2 = highLevelIntents.getIntent("2");
        assertThat(intentForSwitch2, notNullValue());
        assertThat(intentForSwitch2.getState(),
                is(equalTo(IntentState.INST_REQ)));

        //  Check that switch 3 was correctly processed
        final Intent intentForSwitch3 = highLevelIntents.getIntent("3");
        assertThat(intentForSwitch3, notNullValue());
        assertThat(intentForSwitch3.getState(),
                is(equalTo(IntentState.INST_REQ)));

    }


}
