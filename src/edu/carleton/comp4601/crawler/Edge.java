package edu.carleton.comp4601.crawler;

public class Edge {
	private Integer nodeOne;
	private Integer nodeTwo;
	
	public Edge(Integer nodeOne, Integer nodeTwo) {
		this.nodeOne = nodeOne;
		this.nodeTwo = nodeTwo;
	}

	public Integer getNodeOne() {
		return nodeOne;
	}

	public void setNodeOne(Integer nodeOne) {
		this.nodeOne = nodeOne;
	}

	public Integer getNodeTwo() {
		return nodeTwo;
	}

	public void setNodeTwo(Integer nodeTwo) {
		this.nodeTwo = nodeTwo;
	}
	
}
