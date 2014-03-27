/*
 * Copyright 2009-14 www.scribble.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.scribble.projection.rules;

import org.scribble.context.ModuleContext;
import org.scribble.logging.IssueLogger;
import org.scribble.model.ModelObject;
import org.scribble.model.RoleDecl;
import org.scribble.model.global.GRecursion;
import org.scribble.model.local.LBlock;
import org.scribble.model.local.LRecursion;

/**
 * This class implements the GRecursion projection rule.
 *
 */
public class GRecursionProjectionRule implements ProjectionRule {

	/**
	 * {@inheritDoc}
	 */
	public Object project(ModuleContext context, ModelObject mobj,
								RoleDecl role, IssueLogger logger) {
		LRecursion projected=null;
		GRecursion source=(GRecursion)mobj;
		
		if (source.isRoleInvolved(role)) {
			projected = new LRecursion();
			
			projected.derivedFrom(source);
	        
	        projected.setLabel(source.getLabel());
	        
			ProjectionRule rule=ProjectionRuleFactory.getProjectionRule(source.getBlock());
			
			if (rule != null) {
				LBlock lb=(LBlock)rule.project(context, source.getBlock(), role, logger);
				
				if (lb != null) {
					projected.setBlock(lb);
				}
			}
		}
        
        return (projected);
	}
}
