package th2014_parser;

import java.util.List;

import th2014_parser.PrereqTree.TreeType;

public class Node
{
	String data;
	Node parent;
	// All children of a node must have fulfilled prereqs
	List<Node> children;
	String uuidString;
	TreeType treeType;

	public Node(String data, Node parent, List<Node> children, String uuidString)
	{
		this.data = data;
		this.parent = parent;
		this.children = children;
		this.uuidString = uuidString;
	}
}