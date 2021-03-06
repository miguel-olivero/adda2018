package us.lsi.graphs.examples;

import java.io.PrintWriter;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.HamiltonianCycleAlgorithm;
import org.jgrapht.alg.tour.HeldKarpTSP;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.io.DOTExporter;
import org.jgrapht.io.IntegerComponentNameProvider;

import us.lsi.common.Files2;
import us.lsi.common.Sets2;
import us.lsi.common.Strings2;
import us.lsi.grafos.datos.Carretera;
import us.lsi.grafos.datos.Ciudad;
import us.lsi.graphcolors.GraphColors;
import us.lsi.graphs.GraphsReader;
import us.lsi.graphs.views.CompleteGraphView;
import us.lsi.graphs.views.SubGraphView;

public class Views {

	public static void main(String[] args) {
		Graph<Ciudad, Carretera> graph = GraphsReader.newGraph("ficheros/andalucia.txt", 
				Ciudad::create,
				Carretera::create, 
				() -> new SimpleWeightedGraph<Ciudad, Carretera>(Ciudad::create, Carretera::create),
				Carretera::getKm);
		DOTExporter<Ciudad, Carretera> de = new DOTExporter<Ciudad, Carretera>(new IntegerComponentNameProvider<>(),
				x -> x.getNombre(), 
				x -> String.format("%.2f",x.getKm())
				);		
		PrintWriter f1 = Files2.getWriter("ficheros/andalucia1.gv");
		de.exportGraph(graph, f1);
		Graph<Ciudad, Carretera> graph2 = CompleteGraphView.of(graph,
				Carretera::create,
				Double.valueOf(1000.),
				c->c.getSource(),
				c->c.getTarget(),
				c->c.getKm());
		HamiltonianCycleAlgorithm<Ciudad, Carretera> a = new HeldKarpTSP<>();
		GraphPath<Ciudad, Carretera> r = a.getTour(graph2);
		DOTExporter<Ciudad, Carretera> de2 = new DOTExporter<Ciudad, Carretera>(new IntegerComponentNameProvider<>(),
				x -> x.getNombre(), 
				x -> String.format("%.2f",x.getKm()), null,
				e -> GraphColors.getStyleIf("bold", e, x -> r.getEdgeList().contains(x)));
		PrintWriter f2 = Files2.getWriter("ficheros/tspCompleteAndalucia.gv");
		de2.exportGraph(graph2, f2);
		Strings2.toConsole(r.getEdgeList(), "Camino");
		Graph<Ciudad, Carretera> graph3 = SubGraphView.of(graph,
				Sets2.newSet(Ciudad.create("Sevilla"),Ciudad.create("Cadiz"),Ciudad.create("Huelva"),Ciudad.create("Almeria")),
				c->c.getSource(), 
				c->c.getTarget());
		PrintWriter f3 = Files2.getWriter("ficheros/subGrafoAndalucia.gv");
		de.exportGraph(graph3, f3);
	}

}
