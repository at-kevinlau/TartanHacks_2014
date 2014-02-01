package th2014_parser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class ReqTree implements Serializable
{
	private static final long serialVersionUID = 6062494209339114482L;

	enum TreeType
	{
		OR("OR"), AND("AND");
		private final String name;

		private TreeType(String s)
		{
			name = s;
		}

		public boolean equalsName(String otherName)
		{
			return (otherName == null) ? false : name.equals(otherName);
		}

		public String toString()
		{
			return name;
		}
	};

	public static Node treeFromReqString(String courseNum, String prereqStr,
			UUID parentUUID, String description, String title)
	{
		// System.out.println("treeFromPrereqString: " + prereqStr);
		TreeType rootType = Node.DEFAULT_TYPE;
		ArrayList<Node> children = new ArrayList<Node>();
		Node root = new Node(courseNum, parentUUID, children);

		int parenCount = 0;
		String currString = "";
		for (char c : prereqStr.toCharArray())
		{
			// parens aren't closed properly
			if (parenCount < 0)
			{
				return null;
			} else if (parenCount == 0)
			{
				// if at top level, then process ors/ands
				switch (c)
				{
				// found a space - process the last word and clear it
				case ' ':
					// hope there aren't mixed 'and'/'or's without parens
					if (currString.equalsIgnoreCase("or")
							|| currString.equals(","))
					{
						rootType = TreeType.OR;
					} else if (currString.equalsIgnoreCase("and"))
					{
						rootType = TreeType.AND;
					} else if (currString.length() > 0)
					{
						Node n = new Node(currString, root.uuid,
								new ArrayList<Node>());
						children.add(n);
						Edge.edgeList.add(new Edge(root.nodeId, n.nodeId));
					}
					currString = "";
					break;
				case '(':
					currString = "";
					parenCount++;
					break;
				case ')':
					// shouldn't find a close paren if pcount=0
					return null;
				default:
					// add more to a word
					currString += c;
				}
			} else
			{
				// if parenCount > 0
				// then copy until matching close paren is encountered
				switch (c)
				{
				case '(':
					parenCount++;
					break;
				case ')':
					parenCount--;
					if (parenCount <= 0)
					{
						if (currString.length() > 5)
						{
							Node n = treeFromReqString(null, currString,
									root.uuid, null, null);
							children.add(n);
							Edge.edgeList.add(new Edge(root.nodeId, n.nodeId));
							currString = "";
						} else
						{
							Node n = new Node(currString, root.uuid,
									new ArrayList<Node>());
							children.add(n);
							Edge.edgeList.add(new Edge(root.nodeId, n.nodeId));
							currString = "";
						}
					}
					break;
				default:
					currString += c;
				}
			}
		}
		if (parenCount == 0 && currString.length() > 0)
		{
			Node n = new Node(currString, root.uuid, new ArrayList<Node>());
			children.add(n);
			Edge.edgeList.add(new Edge(root.nodeId, n.nodeId));
		}

		int rootId = root.nodeId;
		Node.allNodes.get(rootId).treeType = rootType;
		Node.allNodes.get(rootId).description = description;
		Node.allNodes.get(rootId).title = title;
		return root;
	}
}
