package th2014_parser;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.google.gson.*;
import com.google.gson.annotations.Expose;

import th2014_parser.ReqTree.TreeType;

public class Node
{
	@Expose
	String courseId;
	// All children of a node must have fulfilled prereqs
	@Expose
	TreeType treeType;
	// UUID postreqUUID;
	UUID uuid;
	@Expose
	int nodeId;
	private List<Node> prereqs;

	// make uuids zero-indexed
	// array of pairs of edges
	public static final TreeType DEFAULT_TYPE = TreeType.OR;
	private static int idNumCounter = 0;

	private static HashMap<String, Integer> courseToIdMap = new HashMap<String, Integer>();
	private static HashMap<String, UUID> courseToUuidMap = new HashMap<String, UUID>();

	public Node(String data, UUID parentUUID, List<Node> children)
	{
		if (data == null)
			data = "";
		this.courseId = data;
		treeType = DEFAULT_TYPE;
		// this.postreqUUID = parentUUID;
		Integer id = courseToIdMap.get(data);
		if (id == null || this.courseId.length() <= 0)
		{
			this.nodeId = idNumCounter;
			idNumCounter++;
			if (this.courseId.length() > 0)
				courseToIdMap.put(this.courseId, this.nodeId);
		} else
		{
//			System.out.println("Found old id. id=" + id + ", courseId=" + courseId);
			this.nodeId = id;
		}
		// UUID uuid = courseToUuidMap.get(data);
		// if (uuid == null || this.courseId.length() <= 0)
		// {
		// this.uuid = UUID.randomUUID();
		// if (this.courseId.length() > 0)
		// courseToUuidMap.put(this.courseId, this.uuid);
		// } else
		// {
		// this.uuid = uuid;
		// }
		this.prereqs = children;
	}

	/**
	 * 
	 * @return comma-separated list of this node and all child nodes with lesser
	 *         nodeIds
	 */
	public String generateJSON()
	{
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
				.create();
		String finalJsonString = gson.toJson(this);

		for (Node e : prereqs)
		{
			if (e.courseId.equals(""))
			{
				finalJsonString += "," + e.generateJSON();
			}
		}
		return finalJsonString;
	}

	@Override
	public String toString()
	{
		// String resultString = "Node [";
		String resultString = "[";
		if (courseId != null && courseId.length() > 0)
		{
			resultString += "data=" + courseId;
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
}