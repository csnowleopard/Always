package alwaysBindingDemo;

import com.palantir.ptoss.cinch.core.Bindings;
import java.util.List;

import com.google.common.collect.Lists;
import com.palantir.ptoss.cinch.core.BindingWiring;

public class AlwaysExtensionBindings extends Bindings {

	
   public static Bindings alwaysExtensionBindings() {
       // start with standard set of bindings
       List<BindingWiring> wirings = Lists.newArrayList(Bindings.STANDARD_BINDINGS);
       // add in all additions
       wirings.add(new Always.Wiring());
       return new Bindings(wirings);
   }
}
	
	
	
