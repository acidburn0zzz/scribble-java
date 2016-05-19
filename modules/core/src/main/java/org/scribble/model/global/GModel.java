package org.scribble.model.global;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.scribble.model.local.EndpointGraph;
import org.scribble.model.local.EndpointState;
import org.scribble.model.local.LGraphBuilder;
import org.scribble.sesstype.name.Role;

public class GModel
{
	public final GModelState init;
	public final GModelState term;
	
	public GModel(GModelState init, GModelState term)
	{
		this.init = init;
		this.term = term;
	}
	
	public EndpointGraph project(Role self)
	{
		LGraphBuilder graph = new LGraphBuilder();
		graph.reset();
		Map<GModelState, EndpointState> map = new HashMap<>();
		map.put(this.init, graph.getEntry());
		map.put(this.term, graph.getExit());
		project(new HashSet<>(), map, this.init, self, graph);
		return new EndpointGraph(graph.getEntry(), graph.getExit());
	}

	private static void project(Set<GModelState> seen, Map<GModelState, EndpointState> map, GModelState gcurr, Role self, LGraphBuilder builder)
	{
		if (seen.contains(gcurr))
		{
			return;
		}
		seen.add(gcurr);
		for (GModelAction a : gcurr.getAcceptable())
		{
			GModelState gsucc = gcurr.accept(a);
			if (a.containsRole(self))
			{
				EndpointState lsucc = map.get(gsucc);
				if (lsucc == null)
				{
					//lsucc = builder.newState(gcurr.getLabels());  // Needs every rec to be "guarded" by an action (because inside .contains role)
					// Actually, maybe rec labels aren't (semantically) important anymore
					lsucc = builder.newState(Collections.emptySet());  // Needs every rec to be "guarded" by an action (because inside .contains role)
					map.put(gsucc, lsucc);
				}
				builder.addEdge(map.get(gcurr), a.project(self), lsucc);
			}
			else
			{
				// ... FIXME: unguarded choice-recs (do something like add labels to existing node, but not exactly this)
				map.put(gsucc, map.get(gcurr));
			}
			project(seen, map, gsucc, self, builder);
		}
	}
	
	@Override
	public String toString()
	{
		return this.init.toDot();
	}
}
