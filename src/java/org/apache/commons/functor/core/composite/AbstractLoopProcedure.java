/*
 * Copyright 2003,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.functor.core.composite;

import org.apache.commons.functor.Predicate;
import org.apache.commons.functor.Procedure;

import java.io.Serializable;


/**
 * Abstract base class for {@link WhileDoProcedure} and {@link DoWhileProcedure}
 * used to implement loop procedures.
 * <p>
 * @version $Revision$ $Date$
 * @author Herve Quiroz
 * @author Rodney Waldhoff
 */
public abstract class AbstractLoopProcedure implements Procedure, Serializable {
    protected AbstractLoopProcedure(Predicate condition, Procedure action) {
        this.condition=condition;
        this.action=action;
    }

	public boolean equals(Object object) {
		if (object instanceof AbstractLoopProcedure) {			
			AbstractLoopProcedure that = (AbstractLoopProcedure)object;
			return (null == getCondition() ? null == that.getCondition() : getCondition().equals(that.getCondition())) &&
				(null == getAction() ? null == that.getAction() : getAction().equals(that.getAction())); 
		} else {
			return false;
		}
	}

	public int hashCode() {
		return hashCode("AbstractLoopProcedure".hashCode());
	}
	
	public String toString() {
		return getClass().getName() + "<" + getCondition() + "," + getAction() + ">";
	}
	protected int hashCode(int hash) {
		hash <<= 4;
		if(null != getAction()) {
			hash ^= getAction().hashCode();
		}
		hash <<= 4;
		if(null != getCondition()) {
			hash ^= getCondition().hashCode();
		}
		return hash;
	}


	protected Predicate getCondition() {
		return condition;
	}

	protected Procedure getAction() {
		return action;
	}

	private Predicate condition;
	private Procedure action;

}
