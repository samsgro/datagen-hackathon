package com.thomsonreuters.graph.generator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.graphstream.algorithm.generator.RandomGenerator;
import org.graphstream.algorithm.util.RandomTools;
import org.graphstream.graph.Node;

public class TwoWayExclusiveRandomGenerator extends RandomGenerator {
    // TODO: omg, hackathon refactor needed.
	
	private boolean canASelfLink = true;
	
	private boolean canBSelfLink = true;
	
	private List<String> aSet = new ArrayList<String>();
	
	private List<String> bSet = new ArrayList<String>();
	
	private int setAMaxSize = 100;
	
	private int setBMaxSize = 100;
	
	private String[] setAFile;
	
	private String[] setBFile;
	
	private double abRatio = new Double("0.50");
	
	static String ABATTR = "distribution";
	static String IDENTIFIERATTR = "identifier";
	
	public TwoWayExclusiveRandomGenerator(double averageDegree, boolean allowRemove,
			boolean directed, String edgeAttribute, Boolean canASelfLink, Boolean canBSelfLink, Double abRatio, String[] setAFile, String[] setBFile) {
		super(averageDegree, allowRemove, directed, ABATTR,
				edgeAttribute);
		this.setUseInternalGraph(true);
		this.canASelfLink = canASelfLink;
		this.canBSelfLink = canBSelfLink;
		this.setAFile = setAFile;
		this.setBFile = setBFile;
		this.setAMaxSize = setAFile.length;
		this.setBMaxSize = setBFile.length;
		this.abRatio = abRatio;
	}

	/**
	 * Starts the generator. A clique of size equal to the average degree is
	 * added.
	 * 
	 * @see org.graphstream.algorithm.generator.Generator#begin()
	 * @complexity O(k<sup>2</sup>) where k is the average degree
	 */
	public void begin() {
		Set<String> setMember;
		if (allowRemove)
			edgeIds = new ArrayList<String>();
		subset = new HashSet<Integer>();
		
		String nodeId = null;
		
		for (nodeCount = 0; nodeCount <= (int) averageDegree; nodeCount++) {
			nodeId=nodeCount + "";
			double binaryValue;
			double value = (random.nextDouble() * (nodeAttributeRange[1] - nodeAttributeRange[0])) + nodeAttributeRange[0];
			
			if (value < abRatio) 
			{ 
				if(aSet.size() < setAMaxSize) {
					aSet.add(nodeId); 
					binaryValue = 0;
					addNodeWithDistributionAttribute(nodeId, binaryValue, setAFile[aSet.size() - 1]);
					
				}
			} 
			else 
			{ 
				if(bSet.size() < setBMaxSize) {
					bSet.add(nodeId); 
					binaryValue = 1; 
					addNodeWithDistributionAttribute(nodeId, binaryValue, setAFile[bSet.size() - 1]);
				}
			}
		}
		
	}


	/**
	 * If edge removing is allowed, removes a small fraction of the existing
	 * edges. Then adds a new node and connects it randomly with some of the
	 * existing nodes.
	 * @return <code>true</code>
	 * 
	 * @see org.graphstream.algorithm.generator.Generator#nextEvents()
	 * @complexity Each call of this method takes on average O(k) steps, where k
	 *             is the average degree. Thus generating a graph with n nodes
	 *             will take O(nk) time. The space complexity is O(nk) if edge
	 *             removing is allowed and O(k) otherwise.
	 */
	public boolean nextEvents() {

		//System.out.println("cycle nodecount " + nodeCount);

		
		double binaryValue;
		double value = (random.nextDouble() * (nodeAttributeRange[1] - nodeAttributeRange[0])) + nodeAttributeRange[0];
		
		if (value < abRatio) 
		{ 
			if(aSet.size() < setAMaxSize) {
				if (allowRemove)
					removeExistingEdges(1.0 / nodeCount);
				
				List<String> oppositeSet = null;
				Boolean isSetA = null;
				String nodeId=nodeCount + "";

				double addProbability = averageDegree / nodeCount;
				if(!allowRemove) addProbability /= 2; 
				aSet.add(nodeId);
				isSetA=true;  
				oppositeSet = bSet; 
				binaryValue = 0;
				addNodeWithDistributionAttribute(nodeId, binaryValue, setAFile[aSet.size() - 1]);
				
				if((canASelfLink == false)) {
					addNewEdgesExclusive(addProbability, oppositeSet, nodeId);
				} else {
					addNewEdges(addProbability);
				}
				
				nodeCount++;
				
			}
		} 
		else 
		{ 
			if(bSet.size() < setAMaxSize) {
				if (allowRemove)
					removeExistingEdges(1.0 / nodeCount);
				
				List<String> oppositeSet = null;
				Boolean isSetA = null;
				String nodeId=nodeCount + "";

				double addProbability = averageDegree / nodeCount;
				if(!allowRemove) addProbability /= 2; 
				bSet.add(nodeId);
				isSetA=false; 
				oppositeSet = aSet; 
				binaryValue = 1; 
				addNodeWithDistributionAttribute(nodeId, binaryValue, setBFile[bSet.size() - 1]);
				
				if(canBSelfLink == false) {
					addNewEdgesExclusive(addProbability, oppositeSet, nodeId);
				} else {
					addNewEdges(addProbability);
				}
				
				nodeCount++;
			}
		}
		return true;
	}
	
	protected void addNewEdgesExclusive(double p, List<String> coreSet, String nodeId) {
		if(coreSet.size() > 0) { 
			RandomTools.randomPsubset(coreSet.size(), p, subset, random); 
		} 
		else { return; }
		for (int i : subset) {
				String edgeId = coreSet.get(i) + "_" + nodeId;
				addEdge(edgeId, coreSet.get(i) + "", nodeId);
			if (allowRemove) edgeIds.add(edgeId);
		}
	}
	
	protected void addNodeWithDistributionAttribute(String nodeId, double binaryValue, String identifier) {
		addNode(nodeId);
		sendNodeAttributeAdded(sourceId, nodeId, ABATTR, binaryValue);
		sendNodeAttributeAdded(sourceId, nodeId, IDENTIFIERATTR, identifier);
		if (isUsingInternalGraph()) internalGraph.getNode(nodeId).addAttribute(ABATTR, binaryValue);
		if(binaryValue==1 )	sendNodeAttributeAdded(sourceId, nodeId, "ui.class", "isSetB"); 
	}	
	
}
