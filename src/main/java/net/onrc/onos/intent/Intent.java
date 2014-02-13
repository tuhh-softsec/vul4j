package net.onrc.onos.intent;

/**
 * @author Toshio Koide (t-koide@onlab.us)
 */
public abstract class Intent {
	protected String id;

	public Intent(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
	
	abstract public byte[] toBytes();

	@Override
	public int hashCode() {
		return id.hashCode();
	}
}
