package org.kohsuke.groovy.sandbox;

import groovy.lang.Script;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassCodeExpressionTransformer;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ConstructorNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.AttributeExpression;
import org.codehaus.groovy.ast.expr.CastExpression;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.DeclarationExpression;
import org.codehaus.groovy.ast.expr.EmptyExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ListExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.MethodPointerExpression;
import org.codehaus.groovy.ast.expr.PostfixExpression;
import org.codehaus.groovy.ast.expr.PrefixExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression;
import org.codehaus.groovy.ast.expr.TupleExpression;
import org.codehaus.groovy.classgen.GeneratorContext;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.customizers.CompilationCustomizer;
import org.codehaus.groovy.ast.expr.ConstructorCallExpression;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.syntax.Token;
import org.codehaus.groovy.syntax.Types;
import org.codehaus.groovy.ast.expr.FieldExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.kohsuke.groovy.sandbox.impl.Checker;
import org.kohsuke.groovy.sandbox.impl.Ops;
import org.kohsuke.groovy.sandbox.impl.SandboxedMethodClosure;

import static org.codehaus.groovy.ast.expr.ArgumentListExpression.EMPTY_ARGUMENTS;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import static org.codehaus.groovy.syntax.Types.*;

/**
 * Transforms Groovy code at compile-time to intercept when the script interacts with the outside world.
 *
 * <p>
 * Sometimes you'd like to run Groovy scripts in a sandbox environment, where you only want it to
 * access limited subset of the rest of JVM. This transformation makes that possible by letting you inspect
 * every step of the script execution when it makes method calls and property/field/array access.
 *
 * <p>
 * Once the script is transformed, every intercepted operation results in a call to {@link Checker},
 * which further forwards the call to {@link GroovyInterceptor} for inspection.
 *
 *
 * <p>
 * To use it, add it to the {@link org.codehaus.groovy.control.CompilerConfiguration}, like this:
 *
 * <pre>
 * def cc = new CompilerConfiguration()
 * cc.addCompilationCustomizers(new SandboxTransformer())
 * sh = new GroovyShell(cc)
 * </pre>
 *
 * <p>
 * By default, this code intercepts everything that can be intercepted, which are:
 * <ul>
 *     <li>Method calls (instance method and static method)
 *     <li>Object allocation (that is, a constructor call except of the form "this(...)" and "super(...)")
 *     <li>Property access (e.g., z=foo.bar, z=foo."bar") and assignment (e.g., foo.bar=z, foo."bar"=z)
 *     <li>Attribute access (e.g., z=foo.@bar) and assignments (e.g., foo.@bar=z)
 *     <li>Array access and assignment (z=x[y] and x[y]=z)
 * </ul>
 * <p>
 * You can disable interceptions selectively by setting respective {@code interceptXXX} flags to {@code false}.
 *
 * <p>
 * There'll be a substantial hit to the performance of the execution.
 *
 * @author Kohsuke Kawaguchi
 */
public class SandboxTransformer extends CompilationCustomizer {
    /**
     * Intercept method calls
     */
    boolean interceptMethodCall=true;
    /**
     * Intercept object instantiation by intercepting its constructor call.
     *
     * Note that Java byte code doesn't allow the interception of super(...) and this(...)
     * so the object instantiation by defining and instantiating a subtype cannot be intercepted.
     */
    boolean interceptConstructor=true;
    /**
     * Intercept property access for both read "(...).y" and write "(...).y=..."
     */
    boolean interceptProperty=true;
    /**
     * Intercept array access for both read "y=a[x]" and write "a[x]=y"
     */
    boolean interceptArray=true;
    /**
     * Intercept attribute access for both read "z=x.@y" and write "x.@y=z"
     */
    boolean interceptAttribute=true;

    public SandboxTransformer() {
        super(CompilePhase.CANONICALIZATION);
    }

    @Override
    public void call(final SourceUnit source, GeneratorContext context, ClassNode classNode) {
        if (classNode == null) { // TODO is this even possible? CpsTransformer implies it is not.
            return;
        }

        ClassCodeExpressionTransformer visitor = createVisitor(source, classNode);

        processConstructors(visitor, classNode);
        for (MethodNode m : classNode.getMethods()) {
            visitor.visitMethod(m);
        }
        for (Statement s : classNode.getObjectInitializerStatements()) {
            s.visit(visitor);
        }
        for (FieldNode f : classNode.getFields()) {
            visitor.visitField(f);
        }
    }

    /** Do not care about {@code super} calls for classes extending these types. */
    private static final Set<String> TRIVIAL_CONSTRUCTORS = new HashSet<>(Arrays.asList(
        Object.class.getName(),
        Script.class.getName(),
        "com.cloudbees.groovy.cps.SerializableScript",
        "org.jenkinsci.plugins.workflow.cps.CpsScript"));
    /**
     * Apply SECURITY-582 fix to constructors.
     */
    public void processConstructors(final ClassCodeExpressionTransformer visitor, ClassNode classNode) {
        ClassNode superClass = classNode.getSuperClass();
        List<ConstructorNode> declaredConstructors = classNode.getDeclaredConstructors();
        if (TRIVIAL_CONSTRUCTORS.contains(superClass.getName())) {
            for (ConstructorNode c : declaredConstructors) {
                visitor.visitMethod(c);
            }
        } else {
            if (declaredConstructors.isEmpty()) {
                ConstructorNode syntheticConstructor = new ConstructorNode(Modifier.PUBLIC, new BlockStatement());
                declaredConstructors = Collections.singletonList(syntheticConstructor);
                classNode.addConstructor(syntheticConstructor);
            } else {
                declaredConstructors = new ArrayList<>(declaredConstructors);
            }
            for (ConstructorNode c : declaredConstructors) {
                Statement code = c.getCode();
                List<Statement> body;
                if (code instanceof BlockStatement) {
                    body = ((BlockStatement) code).getStatements();
                } else {
                    body = Collections.singletonList(code);
                }
                TupleExpression superArgs = new TupleExpression();
                if (!body.isEmpty() && body.get(0) instanceof ExpressionStatement && ((ExpressionStatement) body.get(0)).getExpression() instanceof ConstructorCallExpression) {
                    ConstructorCallExpression cce = (ConstructorCallExpression) ((ExpressionStatement) body.get(0)).getExpression();
                    if (cce.isThisCall()) { // these are fine as is
                        visitor.visitMethod(c);
                        continue;
                    } else if (cce.isSuperCall()) {
                        body = body.subList(1, body.size());
                        superArgs = ((TupleExpression) cce.getArguments());
                    }
                }
                List<Expression> thisArgs = new ArrayList<>();
                final TupleExpression _superArgs = superArgs;
                final AtomicReference<Expression> superArgsTransformed = new AtomicReference<>();
                ((ScopeTrackingClassCodeExpressionTransformer) visitor).withMethod(c, new Runnable() {
                    @Override
                    public void run() {
                        superArgsTransformed.set(((VisitorImpl) visitor).transformArguments(_superArgs));
                    }
                });
                thisArgs.add(((VisitorImpl) visitor).makeCheckedCall("checkedSuperConstructor", new ClassExpression(superClass), superArgsTransformed.get()));
                Parameter[] origParams = c.getParameters();
                for (Parameter p : origParams) {
                    thisArgs.add(new VariableExpression(p));
                }
                c.setCode(new BlockStatement(new Statement[] {new ExpressionStatement(new ConstructorCallExpression(ClassNode.THIS, new TupleExpression(thisArgs)))}, c.getVariableScope()));
                Parameter[] params = new Parameter[origParams.length + 1];
                params[0] = new Parameter(new ClassNode(Checker.SuperConstructorWrapper.class), "$scw");
                System.arraycopy(origParams, 0, params, 1, origParams.length);
                List<Expression> scwArgs = new ArrayList<>();
                int x = 0;
                for (Expression superArg : superArgs) {
                    scwArgs.add(/*new CastExpression(superArg.getType(), */new MethodCallExpression(new VariableExpression("$scw"), "arg", new ConstantExpression(x++))/*)*/);
                }
                List<Statement> body2 = new ArrayList<>();
                body2.add(0, new ExpressionStatement(new ConstructorCallExpression(ClassNode.SUPER, new ArgumentListExpression(scwArgs))));
                for (final Statement s : body) {
                    ((ScopeTrackingClassCodeExpressionTransformer) visitor).withMethod(c, new Runnable() {
                        @Override
                        public void run() {
                            s.visit(visitor);
                        }
                    });
                    body2.add(s);
                }
                ConstructorNode c2 = new ConstructorNode(Modifier.PRIVATE, params, c.getExceptions(), new BlockStatement(body2, c.getVariableScope()));
                // perhaps more misleading than helpful: c2.setSourcePosition(c);
                classNode.addConstructor(c2);
            }
        }
    }

    @Deprecated
    public ClassCodeExpressionTransformer createVisitor(SourceUnit source) {
        return createVisitor(source, null);
    }
    
    public ClassCodeExpressionTransformer createVisitor(SourceUnit source, ClassNode clazz) {
        return new VisitorImpl(source, clazz);
    }

    class VisitorImpl extends ScopeTrackingClassCodeExpressionTransformer {
        private final SourceUnit sourceUnit;
        /**
         * Invocation/property access without the left-hand side expression (for example {@code foo()}
         * as opposed to {@code something.foo()} means {@code this.foo()} in Java, but this is not
         * so in Groovy.
         *
         * In Groovy, {@code foo()} inside a closure uses the closure object itself as the lhs value,
         * whereas {@code this} in closure refers to a nearest enclosing non-closure object.
         *
         * So we cannot always expand {@code foo()} to {@code this.foo()}.
         *
         * To keep track of when we can expand {@code foo()} to {@code this.foo()} and when we can't,
         * we maintain this flag as we visit the expression tree. This flag is set to true
         * while we are visiting the body of the closure (the part between { ... }), and switched
         * back to false as we visit inner classes.
         *
         * To correctly expand {@code foo()} in the closure requires an access to the closure object itself,
         * and unfortunately Groovy doesn't seem to have any reliable way to do this. The hack I came up
         * with is {@code asWritable().getOwner()}, but even that is subject to the method resolution rule.
         *
         */
        private boolean visitingClosureBody;

        /**
         * Current class we are traversing.
         */
        private ClassNode clazz;

        VisitorImpl(SourceUnit sourceUnit, ClassNode clazz) {
            this.sourceUnit = sourceUnit;
            this.clazz = clazz;
        }

        @Override
        public void visitMethod(MethodNode node) {
            if (clazz == null) { // compatibility
                clazz = node.getDeclaringClass();
            }
            super.visitMethod(node);
        }

        /**
         * Transforms the arguments of a call.
         * Groovy primarily uses {@link ArgumentListExpression} for this,
         * but the signature doesn't guarantee that. So this method takes care of that.
         */
        Expression transformArguments(Expression e) {
            List<Expression> l;
            if (e instanceof TupleExpression) {
                List<Expression> expressions = ((TupleExpression) e).getExpressions();
                l = new ArrayList<>(expressions.size());
                for (Expression expression : expressions) {
                    l.add(transform(expression));
                }
            } else {
                l = Collections.singletonList(transform(e));
            }

            // checkdCall expects an array
            return withLoc(e,new MethodCallExpression(new ListExpression(l),"toArray",new ArgumentListExpression()));
        }
        
        Expression makeCheckedCall(String name, Expression... arguments) {
            return new StaticMethodCallExpression(checkerClass,name,
                new ArgumentListExpression(arguments));
        }
    
        @Override
        public Expression transform(Expression exp) {
            Expression o = innerTransform(exp);
            if (o!=exp) {
                o.setSourcePosition(exp);
            }
            return o;
        }

        private Expression innerTransform(Expression exp) {
            if (exp instanceof ClosureExpression) {
                // ClosureExpression.transformExpression doesn't visit the code inside
                ClosureExpression ce = (ClosureExpression)exp;
                try (StackVariableSet scope = new StackVariableSet(this)) {
                    Parameter[] parameters = ce.getParameters();
                    if (parameters != null) {
                        // Explicitly defined parameters, i.e., ".findAll { i -> i == 'bar' }"
                        if (parameters.length > 0) {
                            for (Parameter p : parameters) {
                                declareVariable(p);
                            }
                        } else {
                            // Implicit parameter - i.e., ".findAll { it == 'bar' }"
                            declareVariable(new Parameter(ClassHelper.DYNAMIC_TYPE, "it"));
                        }
                    }
                    boolean old = visitingClosureBody;
                    visitingClosureBody = true;
                    try {
                        ce.getCode().visit(this);
                    } finally {
                        visitingClosureBody = old;
                    }
                }
            }

            if (exp instanceof MethodCallExpression && interceptMethodCall) {
                // lhs.foo(arg1,arg2) => checkedCall(lhs,"foo",arg1,arg2)
                // lhs+rhs => lhs.plus(rhs)
                // Integer.plus(Integer) => DefaultGroovyMethods.plus
                // lhs || rhs => lhs.or(rhs)
                MethodCallExpression call = (MethodCallExpression) exp;

                Expression objExp;
                if (call.isImplicitThis() && visitingClosureBody && !isLocalVariableExpression(call.getObjectExpression()))
                    objExp = CLOSURE_THIS;
                else
                    objExp = transform(call.getObjectExpression());

                Expression arg1 = call.getMethod();
                Expression arg2 = transformArguments(call.getArguments());

                if (call.getObjectExpression() instanceof VariableExpression && ((VariableExpression) call.getObjectExpression()).getName().equals("super")) {
                    if (clazz == null) {
                        throw new IllegalStateException("owning class not defined");
                    }
                    return makeCheckedCall("checkedSuperCall", new ClassExpression(clazz), objExp, arg1, arg2);
                } else {
                    return makeCheckedCall("checkedCall",
                            objExp,
                            boolExp(call.isSafe()),
                            boolExp(call.isSpreadSafe()),
                            arg1,
                            arg2);
                }
            }
            
            if (exp instanceof StaticMethodCallExpression && interceptMethodCall) {
                /*
                    Groovy doesn't use StaticMethodCallExpression as much as it could in compilation.
                    For example, "Math.max(1,2)" results in a regular MethodCallExpression.

                    Static import handling uses StaticMethodCallExpression, and so are some
                    ASTTransformations like ToString,EqualsAndHashCode, etc.
                 */
                StaticMethodCallExpression call = (StaticMethodCallExpression) exp;
                return makeCheckedCall("checkedStaticCall",
                            new ClassExpression(call.getOwnerType()),
                            new ConstantExpression(call.getMethod()),
                            transformArguments(call.getArguments())
                    );
            }

            if (exp instanceof MethodPointerExpression && interceptMethodCall) {
                MethodPointerExpression mpe = (MethodPointerExpression) exp;
                return new ConstructorCallExpression(
                        new ClassNode(SandboxedMethodClosure.class),
                        new ArgumentListExpression(mpe.getExpression(), mpe.getMethodName())
                );
            }

            if (exp instanceof ConstructorCallExpression && interceptConstructor) {
                if (!((ConstructorCallExpression) exp).isSpecialCall()) {
                    // creating a new instance, like "new Foo(...)"
                    return makeCheckedCall("checkedConstructor",
                            new ClassExpression(exp.getType()),
                            transformArguments(((ConstructorCallExpression) exp).getArguments())
                    );
                } else {
                    // we can't really intercept constructor calling super(...) or this(...),
                    // since it has to be the first method call in a constructor.
                    // but see SECURITY-582 fix above
                }
            }

            if (exp instanceof AttributeExpression && interceptAttribute) {
                AttributeExpression ae = (AttributeExpression) exp;
                return makeCheckedCall("checkedGetAttribute",
                    transform(ae.getObjectExpression()),
                    boolExp(ae.isSafe()),
                    boolExp(ae.isSpreadSafe()),
                    transform(ae.getProperty())
                );
            }

            if (exp instanceof PropertyExpression && interceptProperty) {
                PropertyExpression pe = (PropertyExpression) exp;
                return makeCheckedCall("checkedGetProperty",
                    transformObjectExpression(pe),
                    boolExp(pe.isSafe()),
                    boolExp(pe.isSpreadSafe()),
                    transform(pe.getProperty())
                );
            }

            if (exp instanceof VariableExpression && interceptProperty) {
                VariableExpression vexp = (VariableExpression) exp;
                if (isLocalVariable(vexp.getName()) || vexp.getName().equals("this") || vexp.getName().equals("super")) {
                    // We don't care what sandboxed code does to itself until it starts interacting with outside world
                    return super.transform(exp);
                } else {
                    // if the variable is not in-scope local variable, it gets treated as a property access with implicit this.
                    // see AsmClassGenerator.visitVariableExpression and processClassVariable.
                    PropertyExpression pexp = new PropertyExpression(VariableExpression.THIS_EXPRESSION, vexp.getName());
                    pexp.setImplicitThis(true);
                    withLoc(exp,pexp);
                    return transform(pexp);
                }
            }

            if (exp instanceof DeclarationExpression) {
                handleDeclarations((DeclarationExpression) exp);
            }

            if (exp instanceof BinaryExpression) {
                BinaryExpression be = (BinaryExpression) exp;
                // this covers everything from a+b to a=b
                if (ofType(be.getOperation().getType(),ASSIGNMENT_OPERATOR)) {
                    // simple assignment like '=' as well as compound assignments like "+=","-=", etc.

                    // How we dispatch this depends on the type of left expression.
                    // 
                    // What can be LHS?
                    // according to AsmClassGenerator, PropertyExpression, AttributeExpression, FieldExpression, VariableExpression

                    Expression lhs = be.getLeftExpression();
                    if (lhs instanceof VariableExpression) {
                        VariableExpression vexp = (VariableExpression) lhs;
                        if (isLocalVariable(vexp.getName()) || vexp.getName().equals("this") || vexp.getName().equals("super")) {
                            // We don't care what sandboxed code does to itself until it starts interacting with outside world
                            return super.transform(exp);
                        } else {
                            // if the variable is not in-scope local variable, it gets treated as a property access with implicit this.
                            // see AsmClassGenerator.visitVariableExpression and processClassVariable.
                            PropertyExpression pexp = new PropertyExpression(VariableExpression.THIS_EXPRESSION, vexp.getName());
                            pexp.setImplicitThis(true);
                            pexp.setSourcePosition(vexp);

                            lhs = pexp;
                        }
                    } // no else here
                    if (lhs instanceof PropertyExpression) {
                        PropertyExpression pe = (PropertyExpression) lhs;
                        String name = null;
                        if (lhs instanceof AttributeExpression) {
                            if (interceptAttribute)
                                name = "checkedSetAttribute";
                        } else {
                            Expression receiver = pe.getObjectExpression();
                            if (receiver instanceof VariableExpression && ((VariableExpression) receiver).getName().equals("this")) {
                                FieldNode field = clazz != null ? clazz.getField(pe.getPropertyAsString()) : null;
                                if (field != null) { // could also verify that it is final, but not necessary
                                    // cf. BinaryExpression.transformExpression; super.transform(exp) transforms the LHS to checkedGetProperty
                                    return new BinaryExpression(lhs, be.getOperation(), transform(be.getRightExpression()));
                                } // else this is a property which we need to check
                            }
                            if (interceptProperty)
                                name = "checkedSetProperty";
                        }
                        if (name==null) // not intercepting?
                            return super.transform(exp);

                        return makeCheckedCall(name,
                                transformObjectExpression(pe),
                                pe.getProperty(),
                                boolExp(pe.isSafe()),
                                boolExp(pe.isSpreadSafe()),
                                intExp(be.getOperation().getType()),
                                transform(be.getRightExpression())
                        );
                    } else
                    if (lhs instanceof FieldExpression) {
                        // while javadoc of FieldExpression isn't very clear,
                        // AsmClassGenerator maps this to GETSTATIC/SETSTATIC/GETFIELD/SETFIELD access.
                        // not sure how we can intercept this, so skipping this for now
                        return super.transform(exp);
                    } else
                    if (lhs instanceof BinaryExpression) {
                        BinaryExpression lbe = (BinaryExpression) lhs;
                        if (lbe.getOperation().getType()==Types.LEFT_SQUARE_BRACKET && interceptArray) {// expression of the form "x[y] = z"
                            return makeCheckedCall("checkedSetArray",
                                    transform(lbe.getLeftExpression()),
                                    transform(lbe.getRightExpression()),
                                    intExp(be.getOperation().getType()),
                                    transform(be.getRightExpression())
                            );
                        }
                    } else
                        throw new AssertionError("Unexpected LHS of an assignment: " + lhs.getClass());
                }
                if (be.getOperation().getType()==Types.LEFT_SQUARE_BRACKET) {// array reference
                    if (interceptArray)
                        return makeCheckedCall("checkedGetArray",
                                transform(be.getLeftExpression()),
                                transform(be.getRightExpression())
                        );
                } else
                if (be.getOperation().getType()==Types.KEYWORD_INSTANCEOF) {// instanceof operator
                    return super.transform(exp);
                } else
                if (Ops.isLogicalOperator(be.getOperation().getType())) {
                    return super.transform(exp);
                } else
                if (be.getOperation().getType()==Types.KEYWORD_IN) {// membership operator: JENKINS-28154
                    // This requires inverted operand order:
                    // "a in [...]" -> "[...].isCase(a)"
                    if (interceptMethodCall)
                        return makeCheckedCall("checkedCall",
                                transform(be.getRightExpression()),
                                boolExp(false),
                                boolExp(false),
                                stringExp("isCase"),
                                transform(be.getLeftExpression())

                        );
                } else
                if (Ops.isRegexpComparisonOperator(be.getOperation().getType())) {
                    if (interceptMethodCall)
                        return makeCheckedCall("checkedStaticCall",
                                classExp(ScriptBytecodeAdapterClass),
                                stringExp(Ops.binaryOperatorMethods(be.getOperation().getType())),
                                transform(be.getLeftExpression()),
                                transform(be.getRightExpression())
                        );
                } else
                if (Ops.isComparisionOperator(be.getOperation().getType())) {
                    if (interceptMethodCall) {
                        return makeCheckedCall("checkedComparison",
                                transform(be.getLeftExpression()),
                                intExp(be.getOperation().getType()),
                                transform(be.getRightExpression())
                        );
                    }
                } else
                if (interceptMethodCall) {
                    // normally binary operators like a+b
                    // TODO: check what other weird binary operators land here
                    return makeCheckedCall("checkedBinaryOp",
                            transform(be.getLeftExpression()),
                            intExp(be.getOperation().getType()),
                            transform(be.getRightExpression())
                    );
                }
            }

            if (exp instanceof PostfixExpression) {
                PostfixExpression pe = (PostfixExpression) exp;
                return prefixPostfixExp(exp, pe.getExpression(), pe.getOperation(), "Postfix");
            }
            if (exp instanceof PrefixExpression) {
                PrefixExpression pe = (PrefixExpression) exp;
                return prefixPostfixExp(exp, pe.getExpression(), pe.getOperation(), "Prefix");
            }

            if (exp instanceof CastExpression) {
                CastExpression ce = (CastExpression) exp;
                return makeCheckedCall("checkedCast",
                        classExp(exp.getType()),
                        transform(ce.getExpression()),
                        boolExp(ce.isIgnoringAutoboxing()),
                        boolExp(ce.isCoerce()),
                        boolExp(ce.isStrict())
                );
            }

            return super.transform(exp);
        }

        private Expression prefixPostfixExp(Expression whole, Expression atom, Token opToken, String mode) {
            String op = opToken.getText().equals("++") ? "next" : "previous";

            // a[b]++
            if (atom instanceof BinaryExpression && ((BinaryExpression) atom).getOperation().getType()==Types.LEFT_SQUARE_BRACKET && interceptArray) {
                return makeCheckedCall("checked" + mode + "Array",
                        transform(((BinaryExpression) atom).getLeftExpression()),
                        transform(((BinaryExpression) atom).getRightExpression()),
                        stringExp(op)
                );
            }

            // a++
            if (atom instanceof VariableExpression) {
                VariableExpression ve = (VariableExpression) atom;
                if (isLocalVariable(ve.getName())) {
                    if (mode.equals("Postfix")) {
                        // a trick to rewrite a++ without introducing a new local variable
                        //     a++ -> [a,a=a.next()][0]
                        return transform(withLoc(whole,new BinaryExpression(
                                new ListExpression(Arrays.asList(
                                    atom,
                                    new BinaryExpression(atom, ASSIGNMENT_OP,
                                        withLoc(atom,new MethodCallExpression(atom,op,EMPTY_ARGUMENTS)))
                                )),
                                new Token(Types.LEFT_SQUARE_BRACKET, "[", -1,-1),
                                new ConstantExpression(0)
                        )));
                    } else {
                        // ++a -> a=a.next()
                        return transform(withLoc(whole,new BinaryExpression(atom,ASSIGNMENT_OP,
                                withLoc(atom,new MethodCallExpression(atom,op,EMPTY_ARGUMENTS)))
                        ));
                    }
                } else {
                    // if the variable is not in-scope local variable, it gets treated as a property access with implicit this.
                    // see AsmClassGenerator.visitVariableExpression and processClassVariable.
                    PropertyExpression pexp = new PropertyExpression(VariableExpression.THIS_EXPRESSION, ve.getName());
                    pexp.setImplicitThis(true);
                    pexp.setSourcePosition(atom);

                    atom = pexp;
                    // fall through to the "a.b++" case below
                }
            }

            // a.b++
            if (atom instanceof PropertyExpression && interceptProperty) {
                PropertyExpression pe = (PropertyExpression) atom;
                return makeCheckedCall("checked" + mode + "Property",
                        transformObjectExpression(pe),
                        pe.getProperty(),
                        boolExp(pe.isSafe()),
                        boolExp(pe.isSpreadSafe()),
                        stringExp(op)
                );
            }

            return whole;
        }

        /**
         * Decorates an {@link ASTNode} by copying source location from another node.
         */
        private <T extends ASTNode> T withLoc(ASTNode src, T t) {
            t.setSourcePosition(src);
            return t;
        }

        /**
         * See {@link #visitingClosureBody} for the details of what this method is about.
         */
        private Expression transformObjectExpression(PropertyExpression exp) {
            if (exp.isImplicitThis() && visitingClosureBody && !isLocalVariableExpression(exp.getObjectExpression())) {
                return CLOSURE_THIS;
            } else {
                return transform(exp.getObjectExpression());
            }
        }

        private boolean isLocalVariableExpression(Expression exp) {
            if (exp != null && exp instanceof VariableExpression) {
                return isLocalVariable(((VariableExpression) exp).getName());
            }

            return false;
        }

        ConstantExpression boolExp(boolean v) {
            return v ? ConstantExpression.PRIM_TRUE : ConstantExpression.PRIM_FALSE;
        }

        ConstantExpression intExp(int v) {
            return new ConstantExpression(v,true);
        }

        ClassExpression classExp(ClassNode c) {
            return new ClassExpression(c);
        }

        ConstantExpression stringExp(String v) {
            return new ConstantExpression(v);
        }

        @Override
        public void visitExpressionStatement(ExpressionStatement es) {
            Expression exp = es.getExpression();
            if (exp instanceof DeclarationExpression) {
                DeclarationExpression de = (DeclarationExpression) exp;
                Expression leftExpression = de.getLeftExpression();
                if (leftExpression instanceof VariableExpression) {
                    // Only cast and transform if the RHS is *not* an EmptyExpression, i.e., "String foo;" would not be cast/transformed.
                    if (!(de.getRightExpression() instanceof EmptyExpression) &&
                            mightBePositionalArgumentConstructor((VariableExpression) leftExpression)) {
                        CastExpression ce = new CastExpression(leftExpression.getType(), de.getRightExpression());
                        ce.setCoerce(true);
                        es.setExpression(transform(new DeclarationExpression(leftExpression, de.getOperation(), ce)));
                        return;
                    }
                } else {
                    throw new UnsupportedOperationException("not supporting tuples yet"); // cf. "Unexpected LHS of an assignment" above
                }
            }
            super.visitExpressionStatement(es);
        }

        @Override
        protected SourceUnit getSourceUnit() {
            return sourceUnit;
        }
    }

    /**
     * Checks if a {@link DeclarationExpression#getVariableExpression} might induce {@link DefaultTypeTransformation#castToType} to call a constructor.
     * If so, {@link Checker#checkedCast} should be run.
     * Will be false for example if the declared type is an array, {@code abstract}, or unspecified (just {@code def}).
     * Not yet supporting {@link DeclarationExpression#getTupleExpression} on LHS;
     * and currently ignoring {@link DeclarationExpression#getRightExpression} though some might not possibly be arrays, {@link Collection}s, or {@link Map}s.
     */
    public static boolean mightBePositionalArgumentConstructor(VariableExpression ve) {
        ClassNode type = ve.getType();
        if (type.isArray()) {
            return false; // do not care about componentType
        }
        Class clazz;
        try {
            clazz = type.getTypeClass();
        } catch (GroovyBugError x) {
            return false; // "ClassNode#getTypeClass for … is called before the type class is set" when assigning to a type defined in Groovy source
        }
        return clazz != null && clazz != Object.class && !Modifier.isAbstract(clazz.getModifiers());
    }

    static final Token ASSIGNMENT_OP = new Token(Types.ASSIGN, "=", -1, -1);

    static final ClassNode checkerClass = new ClassNode(Checker.class);
    static final ClassNode ScriptBytecodeAdapterClass = new ClassNode(ScriptBytecodeAdapter.class);

    /**
     * Expression that accesses the closure object itself from within the closure.
     *
     * Currently a hacky "asWritable().getOwner()"
     */
    static final Expression CLOSURE_THIS;

    static {
        MethodCallExpression aw = new MethodCallExpression(new VariableExpression("this"),"asWritable",EMPTY_ARGUMENTS);
        aw.setImplicitThis(true);

        CLOSURE_THIS = new MethodCallExpression(aw,"getOwner",EMPTY_ARGUMENTS);
    }
}
