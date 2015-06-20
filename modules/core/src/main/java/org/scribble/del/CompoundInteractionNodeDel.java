package org.scribble.del;

import org.scribble.ast.CompoundInteractionNode;
import org.scribble.ast.ScribNode;
import org.scribble.main.ScribbleException;
import org.scribble.visit.WellFormedChoiceChecker;
import org.scribble.visit.env.WellFormedChoiceEnv;

/* Unlike global/local interaction model nodes that extend the base constructs (choice/recursion/etc) that extend simple/compound,
 * each interaction construct delegate extends base global/local delegates that extend simple/compound
 * e.g. CompoundInteractionNode -> Choice -> Global/LocalChoice
 *      CompoundInteractionNodeDelegate -> Global/LocalCompoundInteractionNodeDelegate -> [Global/Local]ChoiceDelegate
 * works better for each that way: global/local model nodes share the base node visiting pattern, while global/local delegates share Env handling based on simple/compound (i.e. Env merging) for passes according to global or local
 */
public abstract class CompoundInteractionNodeDel extends CompoundInteractionDel implements InteractionNodeDel
{
	public CompoundInteractionNodeDel()
	{

	}

	@Override
	public CompoundInteractionNode<?> leaveWFChoiceCheck(ScribNode parent, ScribNode child, WellFormedChoiceChecker checker, ScribNode visited) throws ScribbleException
	{
		// Override super routine (in CompoundInteractionDel, which just does base popAndSet) to do merging of child context into parent context
		WellFormedChoiceEnv visited_env = checker.popEnv();
		WellFormedChoiceEnv parent_env = checker.popEnv();
		setEnv(visited_env);
		parent_env = parent_env.mergeContext(visited_env);
		checker.pushEnv(parent_env);
		return (CompoundInteractionNode<?>) visited;
	}
}
