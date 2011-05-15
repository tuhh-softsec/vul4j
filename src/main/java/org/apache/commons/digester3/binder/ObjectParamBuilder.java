package org.apache.commons.digester3.binder;

import org.apache.commons.digester3.ObjectParamRule;

public final class ObjectParamBuilder<T>
    extends AbstractBackToLinkedRuleBuilder<ObjectParamRule>
{

    private final T paramObj;

    private int paramIndex = 0;

    private String attributeName;

    ObjectParamBuilder( String keyPattern, String namespaceURI, RulesBinder mainBinder, LinkedRuleBuilder mainBuilder,
                        /* @Nullable */T paramObj )
    {
        super( keyPattern, namespaceURI, mainBinder, mainBuilder );
        this.paramObj = paramObj;
    }

    /**
     * The zero-relative index of the parameter we are saving.
     *
     * @param paramIndex The zero-relative index of the parameter we are saving
     * @return this builder instance
     */
    public ObjectParamBuilder<T> ofIndex(int paramIndex) {
        if (paramIndex < 0) {
            this.reportError("objectParam(%s).ofIndex(int)", "negative index argument not allowed");
        }

        this.paramIndex = paramIndex;
        return this;
    }

    /**
     * The attribute which we are attempting to match.
     *
     * @param attributeName The attribute which we are attempting to match
     * @return this builder instance
     */
    public ObjectParamBuilder<T> matchingAttribute(/* @Nullable */String attributeName) {
        this.attributeName = attributeName;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ObjectParamRule createRule()
    {
        return new ObjectParamRule( paramIndex, attributeName, paramObj );
    }

}
