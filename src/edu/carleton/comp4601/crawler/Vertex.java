// Sahaj Arora 100961220 Jennifer Franklin 100315764
package edu.carleton.comp4601.crawler;

import java.io.Serializable;
import java.util.Set;

public class Vertex implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String name;
	private Set<Edge> edges;
	
	public Vertex(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<Edge> getEdges() {
		return edges;
	}

	public void setEdges(Set<Edge> edges) {
		this.edges = edges;
	}
	
	
	
}
