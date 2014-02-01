package th2014_parser;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.google.gson.*;

import th2014_parser.ReqTree.TreeType;

public class Node
{
	String data;
	// All children of a node must have fulfilled prereqs
	TreeType treeType;
	UUID parentUUID;
	UUID uuid;
	List<Node> children;

	public static final TreeType DEFAULT_TYPE = TreeType.OR;

	private static HashMap<String, UUID> courseToUuidMap = new HashMap<String, UUID>();

	public Node(String data, UUID parentUUID, List<Node> children)
	{
		if (data == null)
			data = "";
		this.data = data;
		treeType = DEFAULT_TYPE;
		this.parentUUID = parentUUID;

		UUID uuid = courseToUuidMap.get(data);
		if (uuid == null)
		{
			this.uuid = UUID.randomUUID();
		} else
		{
			this.uuid = uuid;
		}
		this.children = children;
	}

	public String generateJSON()
	{
		Gson gson = new Gson();
		return gson.toJson(this);
		
	}

	@Override
	public String toString()
	{
		// String resultString = "Node [";
		String resultString = "[";
		if (data != null && data.length() > 0)
		{
			resultString += "data=" + data;
			if (children.size() > 0)
			{
				resultString += ", ";
			}
		}
		/*
		 * resultString += "treeType=" + treeType.toString() + ", ";
		 * resultString += "parentUUID=" + parentUUID + ", "; resultString +=
		 * "uuid=" + uuid + ", ";
		 */
		if (children.size() > 0)
		{
			resultString += "c={";
			for (int i = 0; i < children.size(); i++)
			{
				Node n = children.get(i);
				resultString += n.toString();
				if (i < children.size() - 1)
				{
					resultString += ",";
				}
			}
			resultString += "}";
		}
		return resultString + "]";
	}

	public void setUUID(UUID uuid)
	{
		this.uuid = uuid;
	}
}