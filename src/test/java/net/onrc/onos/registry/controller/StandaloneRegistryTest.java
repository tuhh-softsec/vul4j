package net.onrc.onos.registry.controller;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.onrc.onos.registry.controller.IControllerRegistryService.ControlChangeCallback;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openflow.util.HexString;

/**
 * Unit test for {@link StandaloneRegistry}.
 * @author Naoki Shiota
 *
 */
public class StandaloneRegistryTest {
	protected static final long TIMEOUT_MSEC = 1000;
	
	protected StandaloneRegistry registry;
	
	/**
	 * Implementation of {@link ControlChangeCallback} which defines callback interfaces called by Registry.
	 * This class remembers past callback parameters and provides methods to access them.
	 * This class also provides CountDownLatch so one can wait until the callback be called
	 * specific times (specified by constructor parameter). Particularly, the first time callback
	 * called is supposed for registration, this class has an independent latch to wait for
	 * the first callback.
	 * @author Naoki Shiota
	 */
	public static class LoggingCallback implements ControlChangeCallback {
		private LinkedList<Long> dpidsCalledback = new LinkedList<Long>();
		private LinkedList<Boolean> controlsCalledback = new LinkedList<Boolean>();
		private CountDownLatch lock = null, registerLock = null;;
		
		/**
		 * Constructor with number of times callback to be called.
		 * @param numberToCall Number of times expected callback to be called
		 */
		public LoggingCallback(int numberToCall) {
			lock = new CountDownLatch(numberToCall);
			registerLock = new CountDownLatch(1);
		}

		/**
		 * Wait until registration is finished (callback is called for the first time).
		 * @throws InterruptedException
		 */
		public void waitForRegistration() throws InterruptedException {
			registerLock.await();
		}
		
		/**
		 * Wait for registration specifying timeout.
		 * @param msec Milliseconds to timeout
		 * @throws InterruptedException
		 */
		public void waitForRegistration(long msec) throws InterruptedException {
			registerLock.await(msec, TimeUnit.MILLISECONDS);
		}

		/**
		 * Wait until callback is called specific times.
		 * @throws InterruptedException
		 */
		public void waitUntilCalled() throws InterruptedException {
			lock.await();
		}
		
		/**
		 * Wait until callback is called specific times, specifying timeout.
		 * @param msec Milliseconds to timeout
		 * @throws InterruptedException
		 */
		public void waitUntilCalled(long msec) throws InterruptedException {
			lock.await(msec, TimeUnit.MILLISECONDS);
		}
		
		/**
		 * Get DPID parameter given by specific callback time.
		 * @param index Specify which time to get parameter
		 * @return DPID value by number.
		 */
		public Long getDpid(int index) { return dpidsCalledback.get(index); }
		
		/**
		 * Get hasControl parameter given by specific callback time.
		 * @param index Specify which time to get parameter
		 * @return hasControl value
		 */
		public Boolean getControl(int index) { return controlsCalledback.get(index); }
		
		/**
		 * Get DPID parameter given by latest call.
		 * @return DPID value by number
		 */
		public Long getLatestDpid() { return dpidsCalledback.peekLast(); }
		
		/**
		 * Get hasControl parameter given by latest call
		 * @return hasControl value
		 */
		public Boolean getLatestControl() { return controlsCalledback.peekLast(); }
		
		@Override
		public void controlChanged(long dpid, boolean hasControl) {
			dpidsCalledback.addLast(dpid);
			controlsCalledback.addLast(hasControl);
			
			lock.countDown();
			registerLock.countDown();
		}
	};
	
	@Before
	public void setUp() throws Exception {
        FloodlightModuleContext fmc = new FloodlightModuleContext();
		registry = new StandaloneRegistry();
		registry.init(fmc);
	}

	@After
	public void tearDown() {
	}
	
	/**
	 * Test if {@link StandaloneRegistry#registerController(String)} can run without error.
	 */
	@Test
	public void testRegisterController() {
		String controllerIdToRegister = "test";
		try {
			registry.registerController(controllerIdToRegister);
		} catch (RegistryException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		// Register Controller ID doubly 
		try {
			registry.registerController(controllerIdToRegister);
			fail("Double registration goes through without exception");
		} catch (RegistryException e) {
			// expected behavior
		}
	}
	
	/**
	 * Test if {@link StandaloneRegistry#getControllerId()} can return correct ID.
	 * @throws RegistryException
	 */
	@Test
	public void testGetControllerId() throws RegistryException {
		String controllerIdToRegister = "test";
		
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
	 * Test if {@link StandaloneRegistry#getAllControllers()} can return correct list of controllers.
	 * @throws RegistryException
	 */
	@Test
	public void testGetAllControllers() throws RegistryException {
		String controllerIdToRegister = "test";
		
		// Test before register controller
		try {
			Collection<String> ctrls = registry.getAllControllers();
			assertFalse(ctrls.contains(controllerIdToRegister));
		} catch (RegistryException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		// register
		registry.registerController(controllerIdToRegister);

		// Test after register controller
		try {
			Collection<String> ctrls = registry.getAllControllers();
			assertTrue(ctrls.contains(controllerIdToRegister));
		} catch (RegistryException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/**
	 * Test if {@link StandaloneRegistry#requestControl(long, ControlChangeCallback)} can correctly take control for switch so that callback is called.
	 * @throws RegistryException
	 * @throws InterruptedException
	 */
	@Test
	public void testRequestControl() throws InterruptedException, RegistryException {
		String controllerId = "test";
		registry.registerController(controllerId);

		LoggingCallback callback = new LoggingCallback(1);
		long dpidToRequest = 1000L;

		try {
			registry.requestControl(dpidToRequest, callback);
		} catch (RegistryException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		callback.waitForRegistration();
		
		long dpidCallback = callback.getLatestDpid();
		boolean controlCallback = callback.getLatestControl();
		
		assertEquals(dpidToRequest, dpidCallback);
		assertTrue(controlCallback);
	}

	/**
	 * Test if {@link StandaloneRegistry#releaseControl(long)} can correctly release the control so that callback is called.
	 * @throws InterruptedException
	 * @throws RegistryException
	 */
	@Test
	public void testReleaseControl() throws InterruptedException, RegistryException {
		String controllerId = "test";
		registry.registerController(controllerId);
		
		long dpidToRequest = 1000L;
		LoggingCallback callback = new LoggingCallback(2);
		
		// to request and wait to take control
		registry.requestControl(dpidToRequest, callback);
		callback.waitForRegistration();
		
		registry.releaseControl(dpidToRequest);
		
		// verify
		callback.waitUntilCalled();
		assertEquals(dpidToRequest, (long)callback.getLatestDpid());
		assertFalse(callback.getLatestControl());
	}

	/**
	 * Test if {@link StandaloneRegistry#hasControl(long)} returns correct status.
	 * @throws InterruptedException
	 * @throws RegistryException
	 */
	@Test
	public void testHasControl() throws InterruptedException, RegistryException {
		String controllerId = "test";
		registry.registerController(controllerId);
		
		long dpidToRequest = 1000L;
		LoggingCallback callback = new LoggingCallback(2);
		
		// Test before request control
		assertFalse(registry.hasControl(dpidToRequest));
		
		registry.requestControl(dpidToRequest, callback);
		callback.waitForRegistration();
		
		// Test after take control
		assertTrue(registry.hasControl(dpidToRequest));
		
		registry.releaseControl(dpidToRequest);
		
		callback.waitUntilCalled();

		// Test after release control
		assertFalse(registry.hasControl(dpidToRequest));
	}

	/**
	 * Test if {@link StandaloneRegistry#getControllerForSwitch(long)} returns correct controller ID.
	 * @throws InterruptedException
	 * @throws RegistryException
	 */
	@Test
	public void testGetControllerForSwitch() throws InterruptedException, RegistryException {
		String controllerId = "test";
		registry.registerController(controllerId);
		
		long dpidToRequest = 1000L;
		LoggingCallback callback = new LoggingCallback(2);
		
		// Test before request control
		try {
			String controllerForSw = registry.getControllerForSwitch(dpidToRequest);
			assertNotEquals(controllerId,controllerForSw);
		} catch (RegistryException e) {
			fail("Failed before request control : " + e.getMessage());
			e.printStackTrace();
		}

		registry.requestControl(dpidToRequest, callback);
		callback.waitForRegistration();

		// Test after take control
		try {
			String controllerForSw = registry.getControllerForSwitch(dpidToRequest);
			assertEquals(controllerId,controllerForSw);
		} catch (RegistryException e) {
			fail("Failed after take control : " + e.getMessage());
			e.printStackTrace();
		}

		registry.releaseControl(dpidToRequest);
		callback.waitUntilCalled();

		// Test after release control
		try {
			String controllerForSw = registry.getControllerForSwitch(dpidToRequest);
			assertNotEquals(controllerId,controllerForSw);
		} catch (RegistryException e) {
			fail("Failed after release control : " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Test if {@link StandaloneRegistry#getAllSwitches()} returns correct list of switches.
	 * @throws InterruptedException
	 * @throws RegistryException
	 */
	@Test
	public void testGetAllSwitches() throws InterruptedException, RegistryException {
		String controllerId = "test";
		registry.registerController(controllerId);

		long dpidToRequest = 1000L;
		String dpidToRequestStr = HexString.toHexString(dpidToRequest);
		LoggingCallback callback = new LoggingCallback(2);

		// Test before request control
		Map<String, List<ControllerRegistryEntry>> switches = registry.getAllSwitches();
		assertNotNull(switches);
		assertFalse(switches.keySet().contains(dpidToRequestStr));

		registry.requestControl(dpidToRequest, callback);
		callback.waitForRegistration();

		// Test after take control
		switches = registry.getAllSwitches();
		assertNotNull(switches);
		assertTrue(switches.keySet().contains(dpidToRequestStr));
		int count = 0;
		for(ControllerRegistryEntry ctrl : switches.get(dpidToRequestStr)) {
			if(ctrl.getControllerId().equals(controllerId)) {
				++count;
			}
		}
		assertEquals(1,count);
		
		registry.releaseControl(dpidToRequest);
		callback.waitUntilCalled();

		// Test after release control
		switches = registry.getAllSwitches();
		assertNotNull(switches);
		assertFalse(switches.keySet().contains(dpidToRequestStr));
	}

	/**
	 * Test if {@link StandaloneRegistry#getSwitchesControlledByController(String)} returns correct list of switches.
	 * @throws InterruptedException
	 * @throws RegistryException
	 */
	// TODO: remove @Ignore after implement StandaloneRegistry#getSwitchesControlledByController
	@Ignore @Test
	public void testGetSwitchesControlledByController() throws InterruptedException, RegistryException {
		String controllerId = "test";
		registry.registerController(controllerId);

		long dpidToRequest = 1000L;
		String dpidToRequestStr = HexString.toHexString(dpidToRequest);
		LoggingCallback callback = new LoggingCallback(2);

		// Test before request control
		Collection<Long> switches = registry.getSwitchesControlledByController(controllerId);
		assertNotNull(switches);
		assertFalse(switches.contains(dpidToRequestStr));

		registry.requestControl(dpidToRequest, callback);
		callback.waitForRegistration();

		// Test after take control
		switches = registry.getSwitchesControlledByController(controllerId);
		assertNotNull(switches);
		assertTrue(switches.contains(dpidToRequestStr));
		int count = 0;
		for(Long dpid : switches) {
			if((long)dpid == dpidToRequest) {
				++count;
			}
		}
		assertEquals(1, count);
		
		registry.releaseControl(dpidToRequest);
		callback.waitUntilCalled();

		// Test after release control
		switches = registry.getSwitchesControlledByController(controllerId);
		assertNotNull(switches);
		assertFalse(switches.contains(dpidToRequestStr));
	}

	/**
	 * Test if {@link StandaloneRegistry#allocateUniqueIdBlock()} returns appropriate object.
	 * Get bulk of IdBlocks and check if they do have unique range of IDs.
	 */
	@Test
	public void testAllocateUniqueIdBlock() {
		// Number of blocks to be verified that any of them has unique block
		final int NUM_BLOCKS = 100;
		ArrayList<IdBlock> blocks = new ArrayList<IdBlock>(NUM_BLOCKS);
		
		for(int i = 0; i < NUM_BLOCKS; ++i) {
			blocks.add(registry.allocateUniqueIdBlock());
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
				assertTrue(lower.getEnd() < higher.getStart());
			}
		}
	}
}
