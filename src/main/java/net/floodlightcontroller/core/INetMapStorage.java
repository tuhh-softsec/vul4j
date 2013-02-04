/**
 * 
 */
package net.floodlightcontroller.core;

/**
 * @author pankaj
 *
 */
public interface INetMapStorage {
	enum DM_OPERATION {
		CREATE, // Create the object type if does not exist and insert the object
		INSERT, // Insert the object if it does not exist
		DELETE, // Delete the object
		UPDATE  // Update the object if exists or CREATE/INSERT if does not exist
	}
	public void init(String conf);
	public void close();
}
