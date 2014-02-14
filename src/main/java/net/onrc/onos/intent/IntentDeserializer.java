package net.onrc.onos.intent;


/**
 * @author Toshio Koide (t-koide@onlab.us)
 */
public class IntentDeserializer {
	private String id;
	private String className;
	private Intent intent = null;

	public IntentDeserializer(byte[] b) {
		// TODO deserialize object and get (unique id, class name, object data) tuple.
		id = "id";
		className = "pi";
		byte[] objectData = null;

		switch (className) {
		case "pi":
			parsePathIntent(objectData);
			break;
		case "spi":
			parseShortestPathIntent(objectData);
			break;
		case "cspi":
			parseConstrainedShortestPathIntent(objectData);
			break;
		default:
			// TODO error
		}
	}

	private void parsePathIntent(byte[] objectData) {
		// TODO deserialize object and create instance
		intent = new PathIntent(id, null, 0, null);
	}

	private void parseShortestPathIntent(byte[] objectData) {
		// TODO deserialize object and create instance
		intent = new ShortestPathIntent(id, 0L, 0L, 0L, 0L, 0L, 0L);
	}

	private void parseConstrainedShortestPathIntent(byte[] objectData) {
		// TODO deserialize object and create instance
		intent = new ConstrainedShortestPathIntent(id, 0L, 0L, 0L, 0L, 0L, 0L, 0.0);
	}

	public Intent getIntent() {
		return intent;
	}
}
