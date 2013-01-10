//   Copyright 2011 Palantir Technologies
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.
package alwaysBindingDemo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import javax.swing.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.google.common.collect.ImmutableList;
import com.palantir.ptoss.cinch.core.Binding;
import com.palantir.ptoss.cinch.core.BindingContext;
import com.palantir.ptoss.cinch.core.BindingException;
import com.palantir.ptoss.cinch.core.BindingWiring;
import com.palantir.ptoss.cinch.core.Bindings;
import com.palantir.ptoss.cinch.core.ObjectFieldMethod;
import com.palantir.ptoss.cinch.swing.Action;
import com.palantir.ptoss.cinch.swing.Bound;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Always {
    /**
     * The name of the method to call when the action occurs. Must be accessible in the
     * {@link BindingContext}.
     */
    String call();

    /**
     * Inner utility class used to wire {@link Action} bindings.
     */
    static class Wiring implements BindingWiring {
        private static final Logger logger = Logger.getLogger(Always.class);

        /**
         * Wires all {@link Action} bindings in the passed context.
         * Called by {@link Bindings#createBindings(BindingContext)} as part of runtime wiring
         * process.
         *
         * @param context
         */
        public Collection<Binding> wire(BindingContext context) {
            List<Field> actions = context.getAnnotatedFields(Always.class);
            for (Field field : actions) {
                Always always = field.getAnnotation(Always.class);
                String call = always.call();
                try {
                    wire(call, field, context);
                } catch (Exception e) {
                    throw new BindingException("could not wire up @Always on " +
                            field.getName(), e);
                }
            }
            return ImmutableList.of();
        }

        /**
         * Wires up to any object with an addActionListener method.  Automatically called
         * by {@link #wire(BindingContext)}.
         *
         * @param call name of an {@link ObjectFieldMethod} in the passed {@link BindingContext}.
         * @param field field to bind the call to.
         * @param context the {@link BindingContext}
         */

	        private static void wire(String call, Field field, BindingContext context)
	                throws SecurityException, NoSuchMethodException, IllegalArgumentException,
	                        IllegalAccessException, InvocationTargetException {
	                            Method aalMethod = field.getType().getMethod("addActionListener", ActionListener.class);
	                            Object actionObject = context.getFieldObject(field, Object.class);
	                            final ObjectFieldMethod ofm = context.getBindableMethod(call);
	                            if (ofm == null) {
	                                throw new BindingException("could not find bindable method: " + call);
	                            }
	                            int delay = 500;
	                            ActionListener actionListener = new ActionListener() {
	                                public void actionPerformed(ActionEvent e) {
	                                    try {
	                                        boolean accessible = ofm.getMethod().isAccessible();
	                                        ofm.getMethod().setAccessible(true);
	                                        ofm.getMethod().invoke(ofm.getObject());
	                                        ofm.getMethod().setAccessible(accessible);
	                                    } catch (InvocationTargetException itex) {
	                                        logger.error("exception during action firing", itex.getCause());
	                                    } catch (Exception ex) {
	                                        logger.error("exception during action firing", ex);
	                                    }
	                                }
	                            };
	        					Timer timer = new Timer(delay,actionListener);
	                            timer.start();
	                            aalMethod.invoke(actionObject, actionListener);
	                        }   
    }
}
