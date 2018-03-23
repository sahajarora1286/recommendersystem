package edu.carleton.comp4601.crawler;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Multigraph;

import edu.carleton.comp4601.crawler.Vertex;

public class CrawlGraph implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ConcurrentHashMap<Long, Vertex> vertices;
	private String name;
	private Multigraph<Vertex, DefaultEdge> g;
	
	public CrawlGraph(String name) {
		this.name = name;
		this.vertices = new ConcurrentHashMap<Long, Vertex>();
		this.g = new Multigraph<Vertex, DefaultEdge>(DefaultEdge.class);
	}
	
	public synchronized boolean addVertex(Vertex v) {
		vertices.put(v.getId(), v);
		return g.addVertex(v);
	}
	
	public synchronized boolean removeVertex(Vertex v) {
		vertices.remove(v.getId());
		return g.removeVertex(v);
	}
	
	public synchronized DefaultEdge addEdge(Vertex v1, Vertex v2) {
		return g.addEdge(v1, v2);
	}
	
	public synchronized boolean removeEdge(DefaultEdge e) {
		return g.removeEdge(e);
	}
	
	public synchronized Vertex find(long id) {
		return vertices.get(id);
	}
}
