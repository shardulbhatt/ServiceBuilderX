package com.tools.codegeneration.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tools.codegeneration.api.model.relationships.Relation;
import com.tools.codegeneration.api.model.relationships.Relation.RelationType;

public class RelationsHelper {

	/**
	 * This method categorises the specified <code>relations</code> based on the
	 * relationship they have with another entity as specified in the
	 * <i>relations definition XML</i>.
	 * 
	 * @param relations the {@link List} holding instances of type {@link Relation}
	 * representing the relations specified in <i>relations definition XML</i>. 
	 * 
	 * @return a {@link Map} holding {@link RelationType}
	 * as the key and a {@link List}, holding instance(s) of type {@link Relation}
	 * having its relation type specified (in the <i>relations definition XML</i>.) 
	 * as the one matching with the key of this map, as the value
	 */
	public static final Map<RelationType, List<Relation>> categorizeRelationsByRelationType(
			List<Relation> relations) {
		
		Map<RelationType, List<Relation>> categorizedRelationsMap = 
				new HashMap<Relation.RelationType, List<Relation>>(
						RelationType.values().length);
		
		boolean isBidirectional = false;
		boolean isOneToOne = false;
		boolean isManyToMany = false;
		
		List<Relation> incorrectRelations = new ArrayList<Relation>();
		
		RelationType relationType = null;
		
		for (Relation relation : relations) {

			isOneToOne = relation.isManyToMany();
			isManyToMany = relation.isOneToOne();
			isBidirectional = relation.isBidirectional();
			
			// Identify incorrect relations

			// a relation cannot be one-to-one and many-to-many simultaneously
			// this is a wrong definition.This incorrect relation should not be 
			// processed and the user should be informed about such incorrect
			// relations
			
			// a relation which is specified as one-to-one does not need 
			// bidirectional set to true.Similar is the case with many-to-many  
			// and bidirectional
			
			boolean flag = 
					(isOneToOne && isManyToMany) || 
					(isOneToOne && isBidirectional) || 
					(isManyToMany && isBidirectional);
					
			if (flag) {
				incorrectRelations.add(relation);
			} else {
				
				if (isOneToOne) {
					relationType = RelationType.ONE_TO_ONE;
				} else if (isManyToMany) {
					relationType = RelationType.MANY_TO_MANY;
				} else {
					relationType = RelationType.ONE_TO_MANY;
				} 

				addToCategorizedRelationsMap(
						relationType, relation, categorizedRelationsMap);
				
				if (isBidirectional) {
					addToCategorizedRelationsMap(
							RelationType.MANY_TO_ONE, relation, 
							categorizedRelationsMap);
				}
			}
		}
		
		if (!incorrectRelations.isEmpty()) {
			System.out.println("Following relations have incorrect definitions " +
					"so could not process these:-");
			System.out.println(incorrectRelations.toString());
		}
		return categorizedRelationsMap;
	}
	
	/**
	 * Adds/Appends an entry to the specified <code>categorizedRelationsMap</code> 
	 * 
	 * @param relationType the {@link RelationType} enum value
	 * @param relation the {@link Relation} instance
	 * 
	 * @param categorizedRelationsMap a {@link Map} holding {@link RelationType}
	 * as the key and a {@link List}, holding instance(s) of type {@link Relation}
	 * having its relation type specified (in the <i>relations definition XML</i>.) 
	 * as the one matching with the key of this map, as the value
	 */
	private static void addToCategorizedRelationsMap(
			RelationType relationType,
			Relation relation, 
			Map<RelationType, List<Relation>> categorizedRelationsMap) {
		
		boolean isRelationTypeEntryAvailable = categorizedRelationsMap.containsKey(relationType);
		
		List<Relation> relations = null;
		
		if (isRelationTypeEntryAvailable) {
			relations = categorizedRelationsMap.get(relationType);
			relations.add(relation);
		} else {
			relations = new ArrayList<Relation>(1);
			relations.add(relation);
			categorizedRelationsMap.put(relationType, relations);
		}
	}
}
