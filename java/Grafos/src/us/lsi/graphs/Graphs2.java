package us.lsi.graphs;

import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.SimpleWeightedGraph;

import us.lsi.common.Preconditions;
import us.lsi.common.TriFunction;

public class Graphs2 {

	
	public static <V,E> SimpleDirectedWeightedGraph<V,E> toDirectedGraph(SimpleWeightedGraph<V,E> graph){
		SimpleDirectedWeightedGraph<V,E> gs = 
				new SimpleDirectedWeightedGraph<V,E>(
						graph.getVertexSupplier(), 
						graph.getEdgeSupplier());
		for(V v:graph.vertexSet()){
			gs.addVertex(v);
		}
		for(E e:graph.edgeSet()){
			gs.addEdge(graph.getEdgeSource(e), graph.getEdgeTarget(e));
			gs.addEdge(graph.getEdgeTarget(e), graph.getEdgeSource(e));
		}
		return gs;
	}
	
	public static <V,E> SimpleDirectedGraph<V,E> toDirectedGraph(SimpleGraph<V,E> graph){
		SimpleDirectedGraph<V,E> gs = 
				new SimpleDirectedGraph<V,E>(
						graph.getVertexSupplier(), 
						graph.getEdgeSupplier(),
						true);
		for(V v:graph.vertexSet()){
			gs.addVertex(v);
		}
		for(E e:graph.edgeSet()){
			gs.addEdge(graph.getEdgeSource(e), graph.getEdgeTarget(e));
			gs.addEdge(graph.getEdgeTarget(e), graph.getEdgeSource(e));
		}
		return gs;
	}
	
	public static <V,E,G extends Graph<V,E>> G subGraph(G graph, 
			Predicate<V> pv, Predicate<E> pe,
			Supplier<G> creator){
		
		Set<V> vertices = null;
	    Set<E> edges = null;
		
	    if (pv!=null) vertices = graph.vertexSet().stream().filter(pv).collect(Collectors.toSet());
		else vertices = graph.vertexSet();
		
		if (pe!=null) edges = graph.edgeSet().stream().filter(pe).collect(Collectors.toSet());
		else edges = graph.edgeSet();
		
		G r = creator.get();
				
		vertices.stream().forEach(x->r.addVertex(x));
		edges.stream().forEach(x->r.addEdge(graph.getEdgeSource(x),graph.getEdgeTarget(x), x));
		
		return r;
	}

	public static <V, E, G extends Graph<V, E>> G explicitCompleteGraph(
			G graph, 
			Double weight,
			Supplier<G> creator, 
			TriFunction<V,V,Double,E> edgeCreator,
			Function<E,Double> edgeWeight) {

		G r = creator.get();

		graph.vertexSet().stream().forEach(x -> r.addVertex(x));
		graph.edgeSet().stream().forEach(x -> r.addEdge(graph.getEdgeSource(x), graph.getEdgeTarget(x), x));

		for (V v1 : graph.vertexSet()) {
			for (V v2 : graph.vertexSet()) {
				if (!v1.equals(v2)) {
					if (!graph.containsEdge(v1, v2)) {
						E e = edgeCreator.apply(v1,v2,weight);
						r.addEdge(v1, v2, e);
					}
				}
			}
		}
		r.edgeSet().forEach(e->r.setEdgeWeight(e, edgeWeight.apply(e)));
		return r;
	}
	
	
	
	public static <V,E extends SimpleEdge<V>>  V getOppositeVertex(Graph<V,E> graph, E edge, V vertex) {
		V r 
		= null;
		if(edge.source.equals(vertex)) r = edge.target;
		if(edge.target.equals(vertex)) r = edge.source;
		Preconditions.checkNotNull(r);
		return r;
	}
	
	public static <V, E, G extends Graph<V, E>> G sustituteEdge(G graph, E edge, GraphPath<V,E> graphPath) {
		Graph<V,E> origin = graphPath.getGraph();
		graph.removeEdge(edge);
		graphPath.getVertexList().stream().forEach(v->{if(!graph.containsVertex(v))graph.addVertex(v);});
		graphPath.getEdgeList().stream()
			.forEach(e->graph.addEdge(origin.getEdgeSource(e), origin.getEdgeTarget(e), e));
		return graph;
	}
	
	
}
