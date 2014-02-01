package th2014_parser;

import java.util.ArrayList;

public class Edge
{
	public int source, target;

	public static ArrayList<Edge> edgeList = new ArrayList<Edge>();
	
	public static void writeEdgesToNodes()
	{
		for (int i = 0; i < edgeList.size(); i++)
		{
			Edge e = edgeList.get(i);
			
			Node.allNodes.get(e.source).prereqLinks.add(i);
			Node.allNodes.get(e.source).prereqIndices.add(e.target);
			
			Node.allNodes.get(e.target).postreqLinks.add(i);
			Node.allNodes.get(e.target).postreqIndices.add(e.source);
		}
	}

	public Edge(int s, int t)
	{
		source = s;
		target = t;
	}
}
