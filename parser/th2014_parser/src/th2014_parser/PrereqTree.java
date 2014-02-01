package th2014_parser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 
 * 
 */

public class PrereqTree implements Serializable
{
	private static final long serialVersionUID = 6062494209339114482L;

	public Node root;

	public PrereqTree(String rootData)
	{
		root = new Node(rootData, null, new ArrayList<Node>(), UUID
				.randomUUID().toString());
	}

	class Node
	{
		String data;
		Node parent;
		List<Node> children;
		String uuidString;

		public Node(String data, Node parent, List<Node> children,
				String uuidString)
		{

		}
	}

	public static PrereqTree treeFromPrereqString(String prereqStr)
	{

		return null;
	}
}
