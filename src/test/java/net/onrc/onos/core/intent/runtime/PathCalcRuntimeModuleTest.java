package net.onrc.onos.core.intent.runtime;

import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.onrc.onos.core.datagrid.IDatagridService;
import net.onrc.onos.core.datagrid.IEventChannel;
import net.onrc.onos.core.datagrid.IEventChannelListener;
import net.onrc.onos.core.intent.Intent;
import net.onrc.onos.core.intent.IntentMap;
import net.onrc.onos.core.intent.IntentOperationList;
import net.onrc.onos.core.intent.MockNetworkGraph;
import net.onrc.onos.core.intent.ShortestPathIntent;
import net.onrc.onos.core.intent.Intent.IntentState;
import net.onrc.onos.core.intent.IntentOperation.Operator;
import net.onrc.onos.core.intent.runtime.IntentStateList;
import net.onrc.onos.core.intent.runtime.PathCalcRuntimeModule;
import net.onrc.onos.core.intent.runtime.PersistIntent;
import net.onrc.onos.core.topology.DeviceEvent;
import net.onrc.onos.core.topology.INetworkGraphListener;
import net.onrc.onos.core.topology.INetworkGraphService;
import net.onrc.onos.core.topology.LinkEvent;
import net.onrc.onos.core.topology.PortEvent;
import net.onrc.onos.core.topology.SwitchEvent;
import net.onrc.onos.registry.controller.IControllerRegistryService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;

import static org.easymock.EasyMock.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * @author Ray Milkey (ray@onlab.us)
 *
 * Unit tests for the Path Calculation Runtime module (PathCalcRuntimeModule).
 * These test cases check the results of creating paths, deleting paths, and
 * rerouting paths.  The network graph, controller registry, and data grid are
 * mocked out.  The individual tests check the high level intents and the
 * resulting operation lists to be sure they match the intended APIs.
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(PathCalcRuntimeModule.class)
public class PathCalcRuntimeModuleTest {
    private static final Long LOCAL_PORT = 0xFFFEL;

    private FloodlightModuleContext modContext;
    private IDatagridService datagridService;
    private INetworkGraphService networkGraphService;
    private IControllerRegistryService controllerRegistryService;
    private PersistIntent persistIntent;
    private MockNetworkGraph graph;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        graph = new MockNetworkGraph();
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


    /**
     * Hamcrest matcher to check that a collection of Intents contains an
     * Intent with the specified Intent Id.
     */
    public static class EntryForIntentMatcher extends TypeSafeMatcher<Collection<Intent>> {
        final private String id;

        public EntryForIntentMatcher(String idValue) {
            id = idValue;
        }

        @Override
        public boolean matchesSafely(Collection<Intent> intents) {
            assertThat(intents,
                       hasItem(Matchers.<Intent>hasProperty("id", equalTo(id))));
            return true;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("an intent with id \" ").
                    appendText(id).
                    appendText("\"");
        }
    }


    /**
     * Factory method to create an Intent entry Matcher.  Returns a matcher
     * for the Intent with the given id.
     * @param id id of the intent to match
     * @return Matcher object
     */
    @Factory
    public static Matcher<Collection<Intent>> hasIntentWithId(String id) {
        return new EntryForIntentMatcher(id);
    }


    /**
     * Matcher to determine if an IntentMap contains an entry with a given id,
     * and that entry has a given state.
     */
    public static class IntentsHaveIntentWithStateMatcher extends TypeSafeMatcher<IntentMap> {
        final private String id;
        final private IntentState state;
        private Intent intent;

        public IntentsHaveIntentWithStateMatcher(String idValue,
                                                 IntentState stateValue) {
            id = idValue;
            state = stateValue;
        }

        @Override
        public boolean matchesSafely(IntentMap intents) {
            intent = intents.getIntent(id);

            return intent != null && intent.getState() == state;
        }

        @Override
        public void describeTo(Description description) {
            if (intent == null) {
                description.appendText("intent lookup for id \"");
                description.appendText(id);
                description.appendText("\"");
            } else {
                description.appendText("state ");
                description.appendText(state.toString());
            }
        }

        @Override
        public void describeMismatchSafely(IntentMap intents,
                                           Description mismatchDescription) {
            if (intent != null) {
                mismatchDescription.appendText("was ").
                                    appendText(intent.getState().toString());
            } else {
                mismatchDescription.appendText("that intent was not found");
            }
        }
    }


    /**
     * Factory method to create a Matcher for an IntentMap that looks for an
     * Intent with a given id and state.
     *
     * @param id id of the Intent to match
     * @param state if the Intent is found, its state must match this
     * @return Matcher object
     */
    @Factory
    public static Matcher<IntentMap> hasIntentWithIdAndState(String id,
                                                             IntentState state) {
        return new IntentsHaveIntentWithStateMatcher(id, state);
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
     *
     * A 3 path list is created where one of the paths references a switch
     * that is not in the topology.  The test checks that the resulting
     * Operation List has entries for the 2 correct paths, and that the
     * high level intents contain a proper error entry for the bad path.
     */
    @Test
    public void testInvalidSwitchName() throws FloodlightModuleException {
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
        assertThat(allIntents, hasSize(opList.size()));

        // Check that we got a high level intent for each operation
        assertThat(allIntents, hasIntentWithId("3"));
        assertThat(allIntents, hasIntentWithId("2"));
        assertThat(allIntents, hasIntentWithId(BAD_SWITCH_INTENT_NAME));

        //  Check that the non existent switch was NACKed
        assertThat(highLevelIntents, hasIntentWithIdAndState(BAD_SWITCH_INTENT_NAME, IntentState.INST_NACK));

        //  Check that switch 2 was correctly processed
        assertThat(highLevelIntents, hasIntentWithIdAndState("2", IntentState.INST_REQ));

        //  Check that switch 3 was correctly processed
        assertThat(highLevelIntents, hasIntentWithIdAndState("3", IntentState.INST_REQ));

    }


    /**
     * Test the result of executing a path calculation on an
     * Intent Operation List and then removing one of the switches.
     *
     * A 3 path list is created and then one of the paths is removed.
     * The test checks that the resulting Operation List is correct,
     * and that the high level intents contain a proper "delete requested"
     * entry for the deleted path.
     */
    @Test
    public void testIntentRemoval() throws FloodlightModuleException {

        // create shortest path intents
        final IntentOperationList opList = new IntentOperationList();
        opList.add(Operator.ADD,
                new ShortestPathIntent("1", 1L, 12L, LOCAL_PORT, 2L, 21L,
                                       LOCAL_PORT));
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

        //  Should be one operation per path
        assertThat(pathIntentOpList, hasSize(opList.size()));

        //  Should be a high level intent for each operation
        assertThat(allIntents, hasSize(opList.size()));

        // Check that we got a high level intent for each operation
        assertThat(allIntents, hasIntentWithId("3"));
        assertThat(allIntents, hasIntentWithId("2"));
        assertThat(allIntents, hasIntentWithId("1"));

        //  Check that switch 1 was correctly processed
        assertThat(highLevelIntents,
                   hasIntentWithIdAndState("1", IntentState.INST_REQ));

        //  Check that switch 2 was correctly processed
        assertThat(highLevelIntents,
                   hasIntentWithIdAndState("2", IntentState.INST_REQ));

        //  Check that switch 3 was correctly processed
        assertThat(highLevelIntents,
                   hasIntentWithIdAndState("3", IntentState.INST_REQ));

        //  Now delete one path and check the results
        final IntentOperationList opListForRemoval = new IntentOperationList();
        opListForRemoval.add(Operator.REMOVE,
                new ShortestPathIntent("1", 1L, 12L, LOCAL_PORT, 2L, 21L,
                        LOCAL_PORT));

        final IntentOperationList pathIntentOpListAfterRemoval =
                runtime.executeIntentOperations(opListForRemoval);
        assertThat(pathIntentOpListAfterRemoval, notNullValue());
        assertThat(pathIntentOpListAfterRemoval, hasSize(1));

        //  Check the high level intents.
        final IntentMap highLevelIntentsAfterRemoval = runtime.getHighLevelIntents();
        assertThat(highLevelIntentsAfterRemoval, notNullValue());

        final Collection<Intent> allIntentsAfterRemoval = highLevelIntentsAfterRemoval.getAllIntents();
        assertThat(allIntentsAfterRemoval, notNullValue());
        assertThat(allIntentsAfterRemoval, hasSize(3));

        // Check that we got a high level intent for each operation
        assertThat(allIntentsAfterRemoval, hasIntentWithId("3"));
        assertThat(allIntentsAfterRemoval, hasIntentWithId("2"));
        assertThat(allIntentsAfterRemoval, hasIntentWithId("1"));

        //  Check the states of the high level intents
        //  Check that switch 1 was correctly processed
        assertThat(highLevelIntents,
                hasIntentWithIdAndState("1", IntentState.DEL_REQ));

        //  Check that switch 2 was correctly processed
        assertThat(highLevelIntents,
                hasIntentWithIdAndState("2", IntentState.INST_REQ));

        //  Check that switch 3 was correctly processed
        assertThat(highLevelIntents,
                hasIntentWithIdAndState("3", IntentState.INST_REQ));
    }

    /**
     * Test the result of executing a path calculation on an
     * Intent Operation List and then forcing a reroute.
     *
     * A 3 path list is created and then one of the links is removed.
     * The test checks that the resulting Operation List is correct,
     * and that the high level intents contain a proper "reroute requested"
     * entry for the deleted link.
     */
    @Test
    public void testIntentReroute() throws FloodlightModuleException {

        // create shortest path intents
        final IntentOperationList opList = new IntentOperationList();
        final ShortestPathIntent pathIntent1 =
                new ShortestPathIntent("1", 1L, 12L, LOCAL_PORT, 2L, 21L,
                                       LOCAL_PORT);

        opList.add(Operator.ADD, pathIntent1);
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

        //  Should be one operation per path
        assertThat(pathIntentOpList, hasSize(opList.size()));

        //  Should be a high level intent for each operation
        assertThat(allIntents, hasSize(opList.size()));

        // Check that we got a high level intent for each operation
        assertThat(allIntents, hasIntentWithId("3"));
        assertThat(allIntents, hasIntentWithId("2"));
        assertThat(allIntents, hasIntentWithId("1"));

        //  Check that switch 1 was correctly processed
        assertThat(highLevelIntents,
                   hasIntentWithIdAndState("1", IntentState.INST_REQ));

        //  Check that switch 2 was correctly processed
        assertThat(highLevelIntents,
                   hasIntentWithIdAndState("2", IntentState.INST_REQ));

        //  Check that switch 3 was correctly processed
        assertThat(highLevelIntents,
                   hasIntentWithIdAndState("3", IntentState.INST_REQ));

        //  Now add a different path to one of the switches path and check
        //  the results
        IntentStateList states = new IntentStateList();
        states.put("1", IntentState.INST_ACK);
        states.put("2", IntentState.INST_ACK);
        states.put("3", IntentState.INST_ACK);
        runtime.getHighLevelIntents().changeStates(states);
        states.clear();
        states.put("1___0", IntentState.INST_ACK);
        states.put("2___0", IntentState.INST_ACK);
        states.put("3___0", IntentState.INST_ACK);
        runtime.getPathIntents().changeStates(states);

        final List<SwitchEvent> emptySwitchEvents = new LinkedList<>();
        final List<PortEvent> emptyPortEvents = new LinkedList<>();
        final List<DeviceEvent> emptyDeviceEvents = new LinkedList<>();
        final List<LinkEvent> addedLinkEvents = new LinkedList<>();
        final List<LinkEvent> removedLinkEvents = new LinkedList<>();

        graph.removeLink(1L, 12L, 2L, 21L); // This link is used by the intent "1"
        graph.removeLink(2L, 21L, 1L, 12L);
        LinkEvent linkEvent1 = new LinkEvent(1L, 12L, 2L, 21L);
        LinkEvent linkEvent2 = new LinkEvent(2L, 21L, 1L, 12L);
        removedLinkEvents.add(linkEvent1);
        removedLinkEvents.add(linkEvent2);
        runtime.networkGraphEvents(
                emptySwitchEvents,
                emptySwitchEvents,
                emptyPortEvents,
                emptyPortEvents,
                addedLinkEvents,
                removedLinkEvents,
                emptyDeviceEvents,
                emptyDeviceEvents);
        final IntentOperationList opListForReroute = new IntentOperationList();
        opListForReroute.add(Operator.ADD, pathIntent1);

        final IntentOperationList pathIntentOpListAfterReroute =
                runtime.executeIntentOperations(opListForReroute);
        assertThat(pathIntentOpListAfterReroute, notNullValue());
        assertThat(pathIntentOpListAfterReroute, hasSize(2));

        //  Check the high level intents.
        final IntentMap highLevelIntentsAfterReroute = runtime.getHighLevelIntents();
        assertThat(highLevelIntentsAfterReroute, notNullValue());

        final Collection<Intent> allIntentsAfterReroute = highLevelIntentsAfterReroute.getAllIntents();
        assertThat(allIntentsAfterReroute, notNullValue());
        assertThat(allIntentsAfterReroute, hasSize(3));

        // Check that we got a high level intent for each operation
        assertThat(allIntentsAfterReroute, hasIntentWithId("3"));
        assertThat(allIntentsAfterReroute, hasIntentWithId("2"));
        assertThat(allIntentsAfterReroute, hasIntentWithId("1"));

        //  Check the states of the high level intents
        //  Check that switch 1 was correctly processed
        assertThat(highLevelIntents,
                hasIntentWithIdAndState("1", IntentState.REROUTE_REQ));

        //  Check that switch 2 was correctly processed
        assertThat(highLevelIntents,
                hasIntentWithIdAndState("2", IntentState.INST_ACK));

        //  Check that switch 3 was correctly processed
        assertThat(highLevelIntents,
                hasIntentWithIdAndState("3", IntentState.INST_ACK));


    }


}
