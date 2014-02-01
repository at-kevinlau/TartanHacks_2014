package th2014_parser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
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
	private List<Node> prereqs;
	UUID uuid;
	@Expose
	int nodeId; // corresponds to index in allNodes
	@Expose
	boolean isStarter = false;
	@Expose
	public List<Integer> prereqIndices;
	@Expose
	public List<Integer> postreqIndices;
	@Expose
	public List<Integer> prereqLinks;
	@Expose
	public List<Integer> postreqLinks;
	@Expose
	public String description;
	@Expose
	public String title;

	// make uuids zero-indexed
	// array of pairs of edges
	public static final TreeType DEFAULT_TYPE = TreeType.OR;
	private static int idNumCounter = 0;

	private static HashMap<String, Integer> courseToIdMap = new HashMap<String, Integer>();
	// private static HashMap<String, UUID> courseToUuidMap = new
	// HashMap<String, UUID>();

	public static File prereqFile;
	public static OutputStreamWriter prereqFWriter;
	public static FileOutputStream prereqFOut;
	public static ArrayList<Node> allNodes;
	static
	{
		allNodes = new ArrayList<Node>();
		try
		{
			prereqFile = new File("../../courses.json");
			prereqFile.createNewFile();
			prereqFOut = new FileOutputStream(prereqFile);
			prereqFWriter = new OutputStreamWriter(prereqFOut);

			prereqFWriter.append("[");
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void writeAllNodesToFile()
	{
		for (Node n : allNodes)
		{
			if (n.prereqs.size() <= 0)
			{
				n.isStarter = true;
			}
			try
			{
				prereqFWriter.append(n.generateJSON() + ",");
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public Node(String cid, UUID parentUUID, List<Node> children)
	{
		if (cid == null)
			cid = "";
		this.courseId = cid;

		description = "";
		title = "";

		treeType = DEFAULT_TYPE;
		this.prereqs = children;
		if (prereqs.size() <= 0)
		{
			isStarter = true;
		}
		prereqIndices = new ArrayList<Integer>();
		postreqIndices = new ArrayList<Integer>();

		prereqLinks = new ArrayList<Integer>();
		postreqLinks = new ArrayList<Integer>();

		// this.postreqUUID = parentUUID;
		Integer id = courseToIdMap.get(cid);
		if (id == null || this.courseId.length() <= 0)
		{
			this.nodeId = idNumCounter;
			idNumCounter++;
			if (this.courseId.length() > 0)
			{
				courseToIdMap.put(this.courseId, this.nodeId);
			}
			allNodes.add(this);
		} else
		{
			// System.out.println("Found old id. id=" + id + ", courseId=" +
			// courseId);
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

		/*
		 * if (nodeId == 5) { System.out.println(generateJSON()); }
		 */
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
		return gson.toJson(this);// + "," + generateChildJSON();
	}

	public String generateChildJSON()
	{
		String finalJsonString = "";
		for (Node e : prereqs)
		{
			// if (e.courseId == null || e.courseId.equals(""))
			// {
			if (e.nodeId < this.nodeId)
			{
				finalJsonString += e.generateJSON();
			}
			// }
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