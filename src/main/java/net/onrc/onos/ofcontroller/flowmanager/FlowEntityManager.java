/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.onrc.onos.ofcontroller.flowmanager;

import com.tinkerpop.blueprints.Direction;
import java.util.Map;
import net.onrc.onos.graph.DBOperation;

public interface FlowEntityManager {
    public void setPrimaryKey(String key);
    public String getPrimaryKey();
    public void hasMany(Class<?> cClass);
    public void operationBegin(String opName);
    public void operationEnd(String opName);
    public void setProperty(String propertyName, Object value);
    public Object getProperty(String propertyName);
    public Map<String, Object> getProperties();
    public FlowEntityManager append(Object entity);
    public void addEdge(Object dst, Direction dir, String label);
    public void removeEdge(Object src, Object dst, Direction dir, String label);
    public void persist(DBOperation dbHandler);
}

