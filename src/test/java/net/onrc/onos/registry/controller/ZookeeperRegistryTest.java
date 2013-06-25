package net.onrc.onos.registry.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.test.FloodlightTestCase;
import net.onrc.onos.registry.controller.StandaloneRegistryTest.LoggingCallback;
import net.onrc.onos.registry.controller.ZookeeperRegistry.SwitchLeaderListener;

import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflow.util.HexString;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.netflix.curator.RetryPolicy;
import com.netflix.curator.framework.CuratorFramework;
import com.netflix.curator.framework.CuratorFrameworkFactory;
import com.netflix.curator.framework.listen.ListenerContainer;
import com.netflix.curator.framework.recipes.atomic.AtomicValue;
import com.netflix.curator.framework.recipes.atomic.DistributedAtomicLong;
import com.netflix.curator.framework.recipes.cache.ChildData;
import com.netflix.curator.framework.recipes.cache.PathChildrenCache;
import com.netflix.curator.framework.recipes.cache.PathChildrenCacheEvent;
import com.netflix.curator.framework.recipes.cache.PathChildrenCacheListener;
import com.netflix.curator.framework.recipes.cache.PathChildrenCache.StartMode;
import com.netflix.curator.framework.recipes.leader.LeaderLatch;
import com.netflix.curator.x.discovery.ServiceCache;
import com.netflix.curator.x.discovery.ServiceCacheBuilder;
import com.netflix.curator.x.discovery.ServiceDiscovery;
import com.netflix.curator.x.discovery.ServiceDiscoveryBuilder;
import com.netflix.curator.x.discovery.ServiceInstance;

/**
 * Unit test for ZookeeperRegistry.
 * NOTE: FloodlightTestCase conflicts with PowerMock. If FloodLight-related methods need to be tested,
 *       implement another test class to test them.
 * @author Naoki Shiota
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ZookeeperRegistry.class, CuratorFramework.class, CuratorFrameworkFactory.class,
	ServiceDiscoveryBuilder.class, ServiceDiscovery.class, ServiceCache.class, PathChildrenCache.class,
	ZookeeperRegistry.SwitchPathCacheListener.class})
public class ZookeeperRegistryTest extends FloodlightTestCase {
	private final static Long ID_BLOCK_SIZE = 0x100000000L;
	
	protected ZookeeperRegistry registry;
	protected CuratorFramework client;
	
	protected PathChildrenCacheListener pathChildrenCacheListener;
	protected final String CONTROLLER_ID = "controller2013";

	/**
	 * Initialize ZookeeperRegistry Object and inject initial value with init() method.
	 * This setup code also tests init() method itself.
	 */
	@Before
	public void setUp() throws Exception {
		super.setUp();
		
		pathChildrenCacheListener = null;
		
		// Mock of CuratorFramework
		client = createCuratorFrameworkMock();
		
		// Mock of CuratorFrameworkFactory
		PowerMock.mockStatic(CuratorFrameworkFactory.class);
		EasyMock.expect(CuratorFrameworkFactory.newClient((String)EasyMock.anyObject(),
				EasyMock.anyInt(), EasyMock.anyInt(), (RetryPolicy)EasyMock.anyObject())).andReturn(client);
		PowerMock.replay(CuratorFrameworkFactory.class);

		FloodlightModuleContext fmc = new FloodlightModuleContext();
		registry = new ZookeeperRegistry();
		fmc.addService(ZookeeperRegistry.class, registry);
		
		registry.init(fmc);
		
		PowerMock.verify(client, CuratorFrameworkFactory.class);
	}

	/**
	 * Clean up member variables (empty for now).
	 */
	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test if registerController() method can go through without exception.
	 * (Exceptions are usually out of test target, but registerController() throws an exception in case of invalid registration.)
	 */
	@Test
	public void testRegisterController() {
		String controllerIdToRegister = "controller2013";
		
		try {
			registry.registerController(controllerIdToRegister);
		} catch (RegistryException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/**
	 * Test if getControllerId() correctly returns registered ID.
	 * @throws Exception
	 */
	@Test
	public void testGetControllerId() throws Exception {
		String controllerIdToRegister = "controller1";
		
		// try before controller is registered
		String controllerId = registry.getControllerId();
		assertNull(controllerId);
		
		// register
		registry.registerController(controllerIdToRegister);
	
		// call getControllerId and verify
		controllerId = registry.getControllerId();
		assertNotNull(controllerId);
		assertEquals(controllerIdToRegister, controllerId);
	}

	/**
	 * Test if getAllControllers() returns all controllers.
	 * Controllers are injected while setup. See createCuratorFrameworkMock() to what controllers
	 * are injected using ServiceCache Mock.
	 * @throws Exception
	 */
	@Test
	public void testGetAllControllers() throws Exception {
		String controllerIdRegistered = "controller1";
		String controllerIdNotRegistered = "controller2013";

		// Test registered controller
		try {
			Collection<String> ctrls = registry.getAllControllers();
			assertTrue(ctrls.contains(controllerIdRegistered));
			assertFalse(ctrls.contains(controllerIdNotRegistered));
		} catch (RegistryException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/**
	 * Test if requestControl() correctly take control of specific switch.
	 * Because requestControl() doesn't return values, inject mock LeaderLatch object and verify latch is correctly set up.
	 * @throws Exception
	 */
	@Test
	public void testRequestControl() throws Exception {
		// Mock of LeaderLatch
		LeaderLatch latch = EasyMock.createMock(LeaderLatch.class);
		latch.addListener(EasyMock.anyObject(SwitchLeaderListener.class));
		EasyMock.expectLastCall().once();
		latch.start();
		EasyMock.expectLastCall().once();
		EasyMock.replay(latch);
		
		PowerMock.expectNew(LeaderLatch.class,
				EasyMock.anyObject(CuratorFramework.class), EasyMock.anyObject(String.class), EasyMock.anyObject(String.class))
				.andReturn(latch);
		PowerMock.replay(LeaderLatch.class);
		
		String controllerId = "controller2013";
		registry.registerController(controllerId);

		LoggingCallback callback = new LoggingCallback(1);
		long dpidToRequest = 2000L;

		try {
			registry.requestControl(dpidToRequest, callback);
		} catch (RegistryException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		EasyMock.verify(latch);
	}

	/**
	 * Test if releaseControl() correctly release control of specific switch.
	 * Because releaseControl() doesn't return values, inject mock LeaderLatch object and verify latch is correctly set up.
	 * @throws Exception
	 */
	@Test
	public void testReleaseControl() throws Exception {
		// Mock of LeaderLatch
		LeaderLatch latch = EasyMock.createMock(LeaderLatch.class);
		latch.addListener(EasyMock.anyObject(SwitchLeaderListener.class));
		EasyMock.expectLastCall().once();
		latch.start();
		EasyMock.expectLastCall().once();
		latch.removeAllListeners();
		EasyMock.expectLastCall().once();
		latch.close();
		EasyMock.expectLastCall().once();
		EasyMock.replay(latch);
		
		PowerMock.expectNew(LeaderLatch.class,
				EasyMock.anyObject(CuratorFramework.class), EasyMock.anyObject(String.class), EasyMock.anyObject(String.class))
				.andReturn(latch);
		PowerMock.replay(LeaderLatch.class);
		
		String controllerId = "controller2013";
		registry.registerController(controllerId);
		
		long dpidToRequest = 1000L;
		LoggingCallback callback = new LoggingCallback(1);
		
		// to request and wait to take control
		registry.requestControl(dpidToRequest, callback);
		registry.releaseControl(dpidToRequest);
		
		// verify
		EasyMock.verify(latch);
	}

	/**
	 * Test if hasControl() returns correct status whether controller has control of specific switch.
	 * @throws Exception
	 */
	@Test
	public void testHasControl() throws Exception {
		// Mock of LeaderLatch
		LeaderLatch latch = EasyMock.createMock(LeaderLatch.class);
		latch.addListener(EasyMock.anyObject(SwitchLeaderListener.class));
		EasyMock.expectLastCall().once();
		latch.start();
		EasyMock.expectLastCall().once();
		EasyMock.expect(latch.hasLeadership()).andReturn(true).anyTimes();
		latch.removeAllListeners();
		EasyMock.expectLastCall().once();
		latch.close();
		EasyMock.expectLastCall().once();
		EasyMock.replay(latch);
		
		PowerMock.expectNew(LeaderLatch.class,
				EasyMock.anyObject(CuratorFramework.class), EasyMock.anyObject(String.class), EasyMock.anyObject(String.class))
				.andReturn(latch);
		PowerMock.replay(LeaderLatch.class);
		
		String controllerId = "controller2013";
		registry.registerController(controllerId);
		
		long dpidToRequest = 2000L;
		LoggingCallback callback = new LoggingCallback(2);
		
		// Test before request control
		assertFalse(registry.hasControl(dpidToRequest));

		registry.requestControl(dpidToRequest, callback);
		
		// Test after request control
		assertTrue(registry.hasControl(dpidToRequest));
		
		registry.releaseControl(dpidToRequest);
		
		// Test after release control
		assertFalse(registry.hasControl(dpidToRequest));
		
		EasyMock.verify(latch);
	}

	/**
	 * Test if getControllerForSwitch() correctly returns controller ID of specific switch.
	 * Relation between controllers and switches are defined by setPathChildrenCache() function.
	 * @throws Throwable
	 */
	@Test
	public void testGetControllerForSwitch() throws Throwable {
		long dpidRegistered = 1000L;
		long dpidNotRegistered = 2000L;
		
		setPathChildrenCache();
		
		String controllerForSw = registry.getControllerForSwitch(dpidRegistered);
		assertEquals("controller1",controllerForSw);

		controllerForSw = registry.getControllerForSwitch(dpidNotRegistered);
		assertEquals(null, controllerForSw);
	}

	/**
	 * Test if getSwitchesControlledByController() returns correct list of switches controlled by
	 * a controller.
	 * @throws Exception
	 */
	// TODO: Test after implementation of getSwitch() is done.
	@Ignore @Test
	public void testGetSwitchesControlledByController() throws Exception {
		String controllerIdRegistered = "controller1";
		String dpidRegistered = HexString.toHexString(1000L);
		String controllerIdNotRegistered = CONTROLLER_ID;
		
		Collection<Long> switches = registry.getSwitchesControlledByController(controllerIdRegistered);
		assertNotNull(switches);
		assertTrue(switches.contains(dpidRegistered));

		switches = registry.getSwitchesControlledByController(controllerIdNotRegistered);
		assertNotNull(switches);
		assertEquals(0, switches.size());
	}

	/**
	 * Test if getAllSwitches() returns correct list of all switches.
	 * Switches are injected in setPathChildrenCache() function.
	 * @throws Exception
	 */
	@Test
	public void testGetAllSwitches() throws Exception {
		String [] dpids = {
				HexString.toHexString(1000L),
				HexString.toHexString(1001L),
				HexString.toHexString(1002L),
		};
		
		setPathChildrenCache();

		Map<String, List<ControllerRegistryEntry>> switches = registry.getAllSwitches();
		assertNotNull(switches);
		assertEquals(dpids.length, switches.size());
		for(String dpid : dpids) {
			assertTrue(switches.keySet().contains(dpid));
		}
	}

	/**
	 * Test if allocateUniqueIdBlock() can assign IdBlock without duplication.
	 */
	@Test
	public void testAllocateUniqueIdBlock() {
		// Number of blocks to be verified that any of them has unique block
		final int NUM_BLOCKS = 100;
		ArrayList<IdBlock> blocks = new ArrayList<IdBlock>(NUM_BLOCKS);
		
		for(int i = 0; i < NUM_BLOCKS; ++i) {
			IdBlock block = registry.allocateUniqueIdBlock();
			assertNotNull(block);
			blocks.add(block);
		}
		
		for(int i = 0; i < NUM_BLOCKS; ++i) {
			IdBlock block1 = blocks.get(i);
			for(int j = i + 1; j < NUM_BLOCKS; ++j) {
				IdBlock block2 = blocks.get(j);
				IdBlock lower,higher;
				
				if(block1.getStart() < block2.getStart()) {
					lower = block1;
					higher = block2;
				} else {
					lower = block2;
					higher = block1;
				}
				
				assertTrue(lower.getSize() > 0L);
				assertTrue(higher.getSize() > 0L);
				assertTrue(lower.getEnd() <= higher.getStart());
			}
		}
	}
	
	/**
	 * Create mock CuratorFramework object with initial value below.
	 *   [Ctrl ID]    : [DPID]
	 * controller1    :  1000
	 * controller2    :  1001
	 * controller2    :  1002
	 * controller2013 : nothing
	 * @return Created mock object
	 * @throws Exception
	 */
	@SuppressWarnings("serial")
	private CuratorFramework createCuratorFrameworkMock() throws Exception {
		// Mock of AtomicValue
		@SuppressWarnings("unchecked")
		AtomicValue<Long> atomicValue = EasyMock.createMock(AtomicValue.class);
		EasyMock.expect(atomicValue.succeeded()).andReturn(true).anyTimes();
		EasyMock.expect(atomicValue.preValue()).andAnswer(new IAnswer<Long>() {
			private long value = 0;
			@Override
			public Long answer() throws Throwable {
				value += ID_BLOCK_SIZE;
				return value;
			}
		}).anyTimes();
		EasyMock.expect(atomicValue.postValue()).andAnswer(new IAnswer<Long>() {
			private long value = ID_BLOCK_SIZE;
			@Override
			public Long answer() throws Throwable {
				value += ID_BLOCK_SIZE;
				return value;
			}
		}).anyTimes();
		EasyMock.replay(atomicValue);
		
		// Mock of DistributedAtomicLong
		DistributedAtomicLong daLong = EasyMock.createMock(DistributedAtomicLong.class);
		EasyMock.expect(daLong.add(EasyMock.anyLong())).andReturn(atomicValue).anyTimes();
		EasyMock.replay(daLong);
		PowerMock.expectNew(DistributedAtomicLong.class,
				new Class<?> [] {CuratorFramework.class, String.class, RetryPolicy.class},
				EasyMock.anyObject(CuratorFramework.class), EasyMock.anyObject(String.class), EasyMock.anyObject(RetryPolicy.class)).
				andReturn(daLong).anyTimes();
		PowerMock.replay(DistributedAtomicLong.class);
		
		// Mock of ListenerContainer
		@SuppressWarnings("unchecked")
		ListenerContainer<PathChildrenCacheListener> listenerContainer = EasyMock.createMock(ListenerContainer.class);
		listenerContainer.addListener(EasyMock.anyObject(PathChildrenCacheListener.class));
		EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {
			@Override
			public Object answer() throws Throwable {
				pathChildrenCacheListener = (PathChildrenCacheListener)EasyMock.getCurrentArguments()[0];
				return null;
			}
		}).once();
		EasyMock.replay(listenerContainer);

		// Mock of PathChildrenCache
		PathChildrenCache pathChildrenCacheMain = createPathChildrenCacheMock(CONTROLLER_ID, new String[] {"/switches"}, listenerContainer);
		PathChildrenCache pathChildrenCache1 = createPathChildrenCacheMock("controller1", new String[] {HexString.toHexString(1000L)}, listenerContainer);
		PathChildrenCache pathChildrenCache2 = createPathChildrenCacheMock("controller2", new String[] { 
			HexString.toHexString(1001L), HexString.toHexString(1002L) },listenerContainer);
		
		// Mock of PathChildrenCache constructor
		PowerMock.expectNew(PathChildrenCache.class,
				EasyMock.anyObject(CuratorFramework.class), EasyMock.anyObject(String.class), EasyMock.anyBoolean()).
				andReturn(pathChildrenCacheMain).once();
		PowerMock.expectNew(PathChildrenCache.class,
				EasyMock.anyObject(CuratorFramework.class), EasyMock.anyObject(String.class), EasyMock.anyBoolean()).
				andReturn(pathChildrenCache1).once();
		PowerMock.expectNew(PathChildrenCache.class,
				EasyMock.anyObject(CuratorFramework.class), EasyMock.anyObject(String.class), EasyMock.anyBoolean()).
				andReturn(pathChildrenCache2).anyTimes();
		PowerMock.replay(PathChildrenCache.class);
		
		// Mock of ServiceCache
		@SuppressWarnings("unchecked")
		ServiceCache<ControllerService> serviceCache = EasyMock.createMock(ServiceCache.class);
		serviceCache.start();
		EasyMock.expectLastCall().once();
		EasyMock.expect(serviceCache.getInstances()).andReturn(new ArrayList<ServiceInstance<ControllerService> > () {{
			add(createServiceInstanceMock("controller1"));
			add(createServiceInstanceMock("controller2"));
		}}).anyTimes();
		EasyMock.replay(serviceCache);
		
		// Mock of ServiceCacheBuilder
		@SuppressWarnings("unchecked")
		ServiceCacheBuilder<ControllerService> serviceCacheBuilder = EasyMock.createMock(ServiceCacheBuilder.class);
		EasyMock.expect(serviceCacheBuilder.name(EasyMock.anyObject(String.class))).andReturn(serviceCacheBuilder).once();
		EasyMock.expect(serviceCacheBuilder.build()).andReturn(serviceCache).once();
		EasyMock.replay(serviceCacheBuilder);

		// Mock of ServiceDiscovery
		@SuppressWarnings("unchecked")
		ServiceDiscovery<ControllerService> serviceDiscovery = EasyMock.createMock(ServiceDiscovery.class);
		serviceDiscovery.start();
		EasyMock.expectLastCall().once();
		EasyMock.expect(serviceDiscovery.serviceCacheBuilder()).andReturn(serviceCacheBuilder).once();
		serviceDiscovery.registerService(EasyMock.anyObject(ServiceInstance.class));
		EasyMock.expectLastCall().once();
		EasyMock.replay(serviceDiscovery);
		
		// Mock of CuratorFramework
		CuratorFramework client = EasyMock.createMock(CuratorFramework.class);
		client.start();
		EasyMock.expectLastCall().once();
		EasyMock.expect(client.usingNamespace(EasyMock.anyObject(String.class))).andReturn(client);
		EasyMock.replay(client);

		// Mock of ServiceDiscoveryBuilder
		@SuppressWarnings("unchecked")
		ServiceDiscoveryBuilder<ControllerService> builder = EasyMock.createMock(ServiceDiscoveryBuilder.class);
		EasyMock.expect(builder.client(client)).andReturn(builder).once();
		EasyMock.expect(builder.basePath(EasyMock.anyObject(String.class))).andReturn(builder);
		EasyMock.expect(builder.build()).andReturn(serviceDiscovery);
		EasyMock.replay(builder);
		
		PowerMock.mockStatic(ServiceDiscoveryBuilder.class);
		EasyMock.expect(ServiceDiscoveryBuilder.builder(ControllerService.class)).andReturn(builder).once();
		PowerMock.replay(ServiceDiscoveryBuilder.class);

		return client;
	}
	
	/**
	 * Create mock ServiceInstance object using given controller ID.
	 * @param controllerId Controller ID to represent instance's payload (ControllerSeervice).
	 * @return
	 */
	private ServiceInstance<ControllerService> createServiceInstanceMock(String controllerId) {
		ControllerService controllerService = EasyMock.createMock(ControllerService.class);
		EasyMock.expect(controllerService.getControllerId()).andReturn(controllerId).anyTimes();
		EasyMock.replay(controllerService);
		
		@SuppressWarnings("unchecked")
		ServiceInstance<ControllerService> serviceInstance = EasyMock.createMock(ServiceInstance.class);
		EasyMock.expect(serviceInstance.getPayload()).andReturn(controllerService).anyTimes();
		EasyMock.replay(serviceInstance);

		return serviceInstance;
	}
	
	/**
	 * Create mock PathChildrenCache using given controller ID and DPIDs.
	 * @param controllerId Controller ID to represent current data.
	 * @param paths List of HexString indicating switch's DPID.
	 * @param listener Callback object to be set as Listenable.
	 * @return
	 * @throws Exception
	 */
	private PathChildrenCache createPathChildrenCacheMock(final String controllerId, final String [] paths,
			ListenerContainer<PathChildrenCacheListener> listener) throws Exception {
		PathChildrenCache pathChildrenCache = EasyMock.createMock(PathChildrenCache.class);
		
		EasyMock.expect(pathChildrenCache.getListenable()).andReturn(listener).anyTimes();
		
		pathChildrenCache.start(EasyMock.anyObject(StartMode.class));
		EasyMock.expectLastCall().anyTimes();
		
		List<ChildData> childs = new ArrayList<ChildData>();
		for(String path : paths) {
			childs.add(createChildDataMockForCurrentData(controllerId,path));
		}
		EasyMock.expect(pathChildrenCache.getCurrentData()).andReturn(childs).anyTimes();
		
		pathChildrenCache.rebuild();
		EasyMock.expectLastCall().anyTimes();
		
		EasyMock.replay(pathChildrenCache);

		return pathChildrenCache;
	}
	
	/**
	 * Create mock ChildData for {@link PathChildrenCache#getCurrentData()} return value.
	 * This object need to include 'sequence number' in tail of path string. ("-0" means 0th sequence)
	 * @param controllerId Controller ID
	 * @param path HexString indicating switch's DPID
	 * @return
	 */
	private ChildData createChildDataMockForCurrentData(String controllerId, String path) {
		ChildData data = EasyMock.createMock(ChildData.class);
		EasyMock.expect(data.getPath()).andReturn(path + "-0").anyTimes();
		EasyMock.expect(data.getData()).andReturn(controllerId.getBytes()).anyTimes();
		EasyMock.replay(data);
		
		return data;
	}

	/**
	 * Inject relations between controllers and switches using callback object.
	 * @throws Exception
	 */
	private void setPathChildrenCache() throws Exception {
		pathChildrenCacheListener.childEvent(client,
				createChildDataEventMock("controller1", HexString.toHexString(1000L), PathChildrenCacheEvent.Type.CHILD_ADDED));
		pathChildrenCacheListener.childEvent(client,
				createChildDataEventMock("controller2", HexString.toHexString(1001L), PathChildrenCacheEvent.Type.CHILD_ADDED));
		pathChildrenCacheListener.childEvent(client,
				createChildDataEventMock("controller2", HexString.toHexString(1002L), PathChildrenCacheEvent.Type.CHILD_ADDED));
	}

	/**
	 * Create mock ChildDataEvent object using given controller ID and DPID.
	 * @param controllerId Controller ID.
	 * @param path HexString of DPID.
	 * @param type Event type to be set to mock object.
	 * @return
	 */
	private PathChildrenCacheEvent createChildDataEventMock(String controllerId, String path,
			PathChildrenCacheEvent.Type type) {
		PathChildrenCacheEvent event = EasyMock.createMock(PathChildrenCacheEvent.class);
		ChildData data = EasyMock.createMock(ChildData.class);
		
		EasyMock.expect(data.getPath()).andReturn(path).anyTimes();
		EasyMock.expect(data.getData()).andReturn(controllerId.getBytes()).anyTimes();
		EasyMock.replay(data);
		
		EasyMock.expect(event.getType()).andReturn(type).anyTimes();
		EasyMock.expect(event.getData()).andReturn(data).anyTimes();
		EasyMock.replay(event);
		
		return event;
	}
}
