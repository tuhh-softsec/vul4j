package net.floodlightcontroller.core.internal;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

// Extends Controller class to access protected inner class
public class RoleChangeCallbackTest extends Controller {
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test if {@link RoleChangeCallback#controlChanged(long, boolean)} correctly calls {@link RoleChanger#submitRequest(Collection, net.floodlightcontroller.core.IFloodlightProviderService.Role)}
	 * when connectedSwitch is not empty.
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testNormalSwitches() throws Exception {
		Long [] dpids = new Long [] { 1000L, 1001L, 1002L, 1003L };
		final long dpidExist = 1000L;
		final long dpidNotExist = 2000L;
		
		roleChanger = EasyMock.createMock(RoleChanger.class);
		
		// First call will be called with (dpidExist,true)
		roleChanger.submitRequest(EasyMock.anyObject(Collection.class), EasyMock.anyObject(Role.class));
		EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {
			@Override
			public Object answer() throws Throwable {
				Collection<OFSwitchImpl> switches = (Collection<OFSwitchImpl>)EasyMock.getCurrentArguments()[0];
				Role role = (Role)EasyMock.getCurrentArguments()[1];

				List<Long> dpids = new ArrayList<Long>();
				
				for(OFSwitchImpl sw : switches) {
					dpids.add(sw.getId());
				}
				assertTrue(dpids.contains(dpidExist));
				assertEquals(role, Role.MASTER);
				
				return null;
			}
		}).once();

		// Second call will be called with (dpidExist,false)
		roleChanger.submitRequest(EasyMock.anyObject(Collection.class), EasyMock.anyObject(Role.class));
		EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {
			@Override
			public Object answer() throws Throwable {
				Collection<OFSwitchImpl> switches = (Collection<OFSwitchImpl>)EasyMock.getCurrentArguments()[0];
				Role role = (Role)EasyMock.getCurrentArguments()[1];

				List<Long> dpids = new ArrayList<Long>();
				
				for(OFSwitchImpl sw : switches) {
					dpids.add(sw.getId());
				}
				assertTrue(dpids.contains(dpidExist));
				assertEquals(role, Role.SLAVE);
				
				return null;
			}
		}).once();

		EasyMock.replay(roleChanger);
		
		initNetwork(roleChanger, dpids);
		
		RoleChangeCallback callback = new RoleChangeCallback();
		callback.controlChanged(dpidExist, true);
		callback.controlChanged(dpidExist, false);
		callback.controlChanged(dpidNotExist, true);
		callback.controlChanged(dpidNotExist, false);
		
		EasyMock.verify(roleChanger);
	}

	/**
	 * Test if {@link RoleChangeCallback#controlChanged(long, boolean)} doesn't call RoleChanger methods
	 * when connectedSwitch is empty.
	 * @throws Exception
	 */
	@Test
	public void testEmptySwitches() throws Exception {
		Long [] dpids = new Long [] {};
		final long dpidToTest = 1000L;
		
		roleChanger = EasyMock.createMock(RoleChanger.class);
		// roleChanger methods must not be used
		EasyMock.replay(roleChanger);
		
		initNetwork(roleChanger, dpids);
		
		RoleChangeCallback callback = new RoleChangeCallback();
		callback.controlChanged(dpidToTest, true);
		callback.controlChanged(dpidToTest, false);
		
		EasyMock.verify(roleChanger);
	}
	
	/**
	 * Create mock OFSwitchImpl object.
	 * @param id
	 * @return
	 */
	private OFSwitchImpl createOFSwitchImplMock(Long id) {
		OFSwitchImpl sw = EasyMock.createMock(OFSwitchImpl.class);

		EasyMock.expect(sw.getId()).andReturn(id).anyTimes();
		EasyMock.replay(sw);
		
		return sw;
	}
	
	/**
	 * Setup connectedSwitches
	 * @param changer
	 * @param ids
	 * @throws Exception
	 */
	private void initNetwork(RoleChanger changer, Long [] ids) throws Exception {
		connectedSwitches = new HashSet<OFSwitchImpl>();
		
		for(Long id : ids) {
			connectedSwitches.add(createOFSwitchImplMock(id));
		}
	}
}
