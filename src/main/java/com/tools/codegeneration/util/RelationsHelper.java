package com.tools.codegeneration.util;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tools.codegeneration.api.model.Entity;
import com.tools.codegeneration.api.model.relationships.Relation;
import com.tools.codegeneration.api.model.relationships.Relation.RelationType;

public class RelationsHelper {

	private static final String INCORRECT_RELATION_MSG_PATTERN = 
			"[ ONE : {0}, MANY : {1}, one-to-one : {2}, many-to-many : {3}, bidirectional : {4} ]";
	
	private static final String INCORRECT_RELATIONS_FOUND_MSG = 
	"Could not process following relations as these are found have incorrect definitions \n Each of these relations " +
	"found to have relations configured of following types " +
	"at the same time which is not valid/not needed: \n 1. one-to-one AND" +
	" many-to-many \n 2. one-to-one AND bidirectional \n" + 
	"3. many-to-many AND bidirectional \n \n In 2) and 3) case bidirectional " +
	"is not needed. \n";
	
	private static final String IMPROPER_JPA_ENABLED_VALUE_FOUND_ON_EITHER_OF_RELATED_ENTITY_MSG =
		"Could not process following relations as these are found to have only either of the related entities JPA enabled, not both: \n";
	
	private static final String IMPROPER_JPA_ENABLED_VALUE_ON_ENTITIES_MSG_PATTERN = 
			"[ ONE : {0}, MANY : {1} ]";
	
	private static final int INCORRECT_RELATION_ERROR = 1;
	private static final int RELATED_ENTITIES_JPA_ENABLED_STATUS_ERROR = 2;
			
	
	/**
	 * This method categorises the specified <code>relations</code> based on the
	 * relationship they have with another entity as specified in the
	 * <i>relations definition XML</i>.
	 * 
	 * @param entities a {@link List} of {@link Entity} instances representing
	 * entities definitions specified in a predefined format in 
	 * <i>entities definition XML</i>
	 * 
	 * @param relations the {@link List} holding instances of type {@link Relation}
	 * representing the relations specified in <i>relations definition XML</i>. 
	 * 
	 * @param entityFullyQualifiedNameIsJPAEnabledMap a {@link Map} holding
     * the fully qualified name of an {@link Entity}, in {@link String} representation,
     * as the key and a boolean value <code>true</code> if the entity definition 
     * is marked as jpaEnabled (as specified in the 
     * <i>entities definition XML</i>), <code>false</code> otherwise
	 * 
	 * @return a {@link Map} holding {@link RelationType}
	 * as the key and a {@link List}, holding instance(s) of type {@link Relation}
	 * having its relation type specified (in the <i>relations definition XML</i>.) 
	 * as the one matching with the key of this map, as the value
	 */
	public static final Map<RelationType, List<Relation>> categorizeRelationsByRelationType(
			List<Entity> entities,
			List<Relation> relations, 
			Map<String, Boolean> entityFullyQualifiedNameIsJPAEnabledMap) {
		
		Map<RelationType, List<Relation>> categorizedRelationsMap = null;
		
		if (relations != null && !relations.isEmpty()) {
			// Instantiate categorizedRelationsMap
			categorizedRelationsMap = 
					new HashMap<Relation.RelationType, List<Relation>>(
							RelationType.values().length);
			
			boolean isBidirectional = false;
			boolean isOneToOne = false;
			boolean isManyToMany = false;
			
			List<Relation> incorrectRelations = new ArrayList<Relation>(5);
			List<Relation> relatedEntitiesSingleEntityJPAEnabled = 
								new ArrayList<Relation>(5);
			
			RelationType relationType = null;
			boolean flag = false;
			
			boolean oneEndEntityJPAEnabled = false;
			boolean manyEndEntityJPAEnabled = false;
			
			String oneEndEntityType = null;
			String manyEndEntityType = null;
			
			for (Relation relation : relations) {

				isOneToOne = relation.isOneToOne();
				isManyToMany = relation.isManyToMany();
				isBidirectional = relation.isBidirectional();
				oneEndEntityType = relation.getOne().getType();
				manyEndEntityType = relation.getMany().getType();
				
				// Identify invalid relations and filter out the involved entities
				// from getting generated.

				// a relation cannot be one-to-one and many-to-many simultaneously
				// this is a wrong definition.This incorrect relation should not be 
				// processed and the user should be informed about such incorrect
				// relations
				
				// a relation which is specified as one-to-one does not need 
				// bidirectional set to true.Similar is the case with many-to-many  
				// and bidirectional
				
				flag =  (isOneToOne && isManyToMany) || 
						(isOneToOne && isBidirectional) || 
						(isManyToMany && isBidirectional);
						
				if (flag) {
					incorrectRelations.add(relation);
				} else {
					
					// Identify entities in a given relation to check whether 
					// both the entities involved, if JPA enabled, are JPA enabled 
					// or only either of them
					
					oneEndEntityJPAEnabled = 
							entityFullyQualifiedNameIsJPAEnabledMap.get(oneEndEntityType);
					manyEndEntityJPAEnabled = 
							entityFullyQualifiedNameIsJPAEnabledMap.get(manyEndEntityType);
					
					flag = 
						(oneEndEntityJPAEnabled && !manyEndEntityJPAEnabled) ||
						(!oneEndEntityJPAEnabled && manyEndEntityJPAEnabled) ;
							
					if (flag) {
						relatedEntitiesSingleEntityJPAEnabled.add(relation);
					} else {
						// Valid Relation found
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
			}
			
			showErrors(
					INCORRECT_RELATION_ERROR, incorrectRelations, 
					INCORRECT_RELATIONS_FOUND_MSG, INCORRECT_RELATION_MSG_PATTERN);
			
			showErrors(
					RELATED_ENTITIES_JPA_ENABLED_STATUS_ERROR, 
					relatedEntitiesSingleEntityJPAEnabled, 
					IMPROPER_JPA_ENABLED_VALUE_FOUND_ON_EITHER_OF_RELATED_ENTITY_MSG, 
					IMPROPER_JPA_ENABLED_VALUE_ON_ENTITIES_MSG_PATTERN);
			
		} else {
			categorizedRelationsMap = Collections.emptyMap();
		}
		
		return categorizedRelationsMap;
	}
	
	/**
	 * 
	 * @param errorCategory
	 * @param invalidRelations
	 * @param message
	 * @param invalidDataMsgPattern
	 */
	private static void showErrors(
			int errorCategory,
			List<Relation> invalidRelations, String message, String invalidDataMsgPattern ) {
		
		if (!invalidRelations.isEmpty()) {
			StringBuilder sb = new StringBuilder(message);
			
			Object[] invalidDataMsgArgsArr = null; 
			
			for (Relation invalidRelation : invalidRelations) {
				
				if (INCORRECT_RELATION_ERROR == errorCategory) {
					invalidDataMsgArgsArr = new Object[] {
							invalidRelation.getOne().getType(),
							invalidRelation.getMany().getType(),
							invalidRelation.isOneToOne(),
							invalidRelation.isManyToMany(),
							invalidRelation.isBidirectional()	
					};
				} else if (RELATED_ENTITIES_JPA_ENABLED_STATUS_ERROR== errorCategory) {
					invalidDataMsgArgsArr = new Object[] {
							invalidRelation.getOne().getType(),
							invalidRelation.getMany().getType()
					};
				}
				sb.append(
						MessageFormat.format(
								invalidDataMsgPattern, 
								invalidDataMsgArgsArr));
			};
			
			System.out.println(sb.toString());
		}
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
