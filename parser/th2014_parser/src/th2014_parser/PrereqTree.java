package th2014_parser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PrereqTree implements Serializable
{
	private static final long serialVersionUID = 6062494209339114482L;

	enum TreeType
	{
		OR, AND
	};

	public static Node treeFromPrereqString(String courseNum, String prereqStr)
	{
		TreeType rootType = TreeType.OR;
		ArrayList<Node> children = new ArrayList<Node>();
		Node root = new Node(courseNum, null, children, UUID.randomUUID()
				.toString());

		int parenCount = 0;
		String currString = "";
		ArrayList<Node> grandchildren = new ArrayList<Node>();
		for (char c : prereqStr.toCharArray())
		{
			if (parenCount < 0)
			{
				return null;
			} else if (parenCount == 0)
			{
				switch (c)
				{
				case ' ':
					// this hopes that there aren't mixed 'and'/'or's without
					// parens
					if (currString.equals("or"))
					{
						rootType = TreeType.OR;
					} else if (currString.equals("and"))
					{
						rootType = TreeType.AND;
					} else
					{
						children.add(new Node(currString, root,
								new ArrayList<Node>(), UUID.randomUUID()
										.toString()));
					}
					currString = "";
					break;
				case '(':
					parenCount++;
					break;
				case ')':
					parenCount--;
					break;
				}
			} else
			{
				// parenCount > 0
				currString += c;
			}
		}

		root.treeType = rootType;
		return root;
	}
}
