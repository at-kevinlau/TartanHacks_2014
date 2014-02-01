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
	//UUID postreqUUID;
	UUID uuid;
	List<Node> prereqs;

	public static final TreeType DEFAULT_TYPE = TreeType.OR;

	private static HashMap<String, UUID> courseToUuidMap = new HashMap<String, UUID>();

	public Node(String data, UUID parentUUID, List<Node> children)
	{
		if (data == null)
			data = "";
		this.data = data;
		treeType = DEFAULT_TYPE;
		//this.postreqUUID = parentUUID;

		UUID uuid = courseToUuidMap.get(data);
		if (uuid == null)
		{
			this.uuid = UUID.randomUUID();
		} else
		{
			this.uuid = uuid;
		}
		this.prereqs = children;
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
			if (prereqs.size() > 0)
			{
				resultString += ", ";
			}
		}
		/*
		 * resultString += "treeType=" + treeType.toString() + ", ";
		 * resultString += "parentUUID=" + parentUUID + ", "; resultString +=
		 * "uuid=" + uuid + ", ";
		 */
		if (prereqs.size() > 0)
		{
			resultString += "c={";
			for (int i = 0; i < prereqs.size(); i++)
			{
				Node n = prereqs.get(i);
				resultString += n.toString();
				if (i < prereqs.size() - 1)
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