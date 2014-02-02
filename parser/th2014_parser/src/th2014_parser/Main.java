package th2014_parser;

import java.io.*;
import java.util.regex.Pattern;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

import com.google.gson.Gson;

public class Main
{
	//@formatter:off
	final static String socURLPrefix = "https://enr-apps.as.cmu.edu/open/SOC/SOCServlet?Formname=GET_CLASSES&SUBMIT=Retrieve+Schedule&SEMESTER=S14&GRAD_UNDER=All&MINI=NO&DEPT=";
	final static String[] socURLStrings = {
		/*"AFR",
		"ARC",
		"ART",
		"BXA",
		"BSC",
		"BMD",
		"BA+",
		"CFA",
		"CIT",
		"CMU",
		"CAS",
		"CNB",
		"CHE",
		"CMY",
		"CEE",
		"CB+",
		*/"CS+",/*
		"BCA",
		"CRM",
		"DES",
		"ISH",
		"HSS",
		"DRA",
		"ECO",*/
		"ECE"/*,
		"IAE",
		"EPP",
		"ENG",
		"ETC",
		"H00",
		"HC+",
		"HIS",
		"HCI",
		"BHA",
		"ICT",
		"INI",
		"ISM",
		"ISR",
		"LTI",
		"MCS",
		"MLG",
		"MSE",
		"MSC",
		"MEG",
		"MED",
		"MST",
		"ML+",
		"MUS",
		"NVS",
		"PHI",
		"PE+",
		"PHY",
		"PSY",
		"PMP",
		"PPP",
		"ROB",
		"BSA",
		"SV+",
		"SDS",
		"SE+",
		"STA",
		"STU",
		"IA+"*/
	};
	//@formatter:on
	final static Pattern matchCourseNumber = Pattern.compile("^[0-9]{5,5}$");
	final static Pattern matchPrereq = Pattern.compile("<b>Prerequisites:</b>");
	final static Pattern matchCoreq = Pattern.compile("<b>Corequisites:</b>.*");
	final static Pattern matchDesc = Pattern.compile("<b>Description:</b>.*");
	final static Pattern matchTitle = Pattern.compile(".*&nbsp;&nbsp;.*");
	final static Pattern matchNone = Pattern.compile("None.");
	final static String socPrefix = "https://enr-apps.as.cmu.edu/open/SOC/";

	public static void main(String[] args) throws IOException
	{
		//@formatter:off
		
		/*
		 System.out.println(ReqTree.treeFromReqString( "18510",
		 "(18300 and 18320) or (18300 and 18491) or (18310 and 18320) or (18310 and 18491) or (18491 and 18320) or (18300 and 18421) or (18310 and 18421) or (18"
		 , null));
		 */
		/*
		System.out.println(ReqTree
				.treeFromReqString(
						"18510",
						"(18300 and 18320) or (18300 and 18491) or (18310 and 18320) or (18310 and 18491) or (18491 and 18320) or (18300 and 18421) or (18310 and 18421) or (18",
						null).generateJSON());
		if (true)
		{
			return;
		}
		*/
		//@formatter:on

		File edgeFile = new File("../../edges.json");
		edgeFile.createNewFile();
		FileOutputStream edgeFOut = new FileOutputStream(edgeFile);
		OutputStreamWriter edgeWriter = new OutputStreamWriter(edgeFOut);

		for (String s : socURLStrings)
		{
			s = socURLPrefix + s;
			try
			{
				Document deptPage = Jsoup.connect(s).get();

				// TODO: hacky way to get this section
				Elements infoSection = deptPage.getElementsByAttributeValue(
						"cellpadding", "5");
				// don't crash on missed page
				if (infoSection.size() <= 0)
				{
					continue;
				}
				Elements links = infoSection.get(0).getElementsByTag("a");
				for (Element link : links)
				{
					String linkText = link.text();
					if (matchCourseNumber.matcher(linkText).matches())
					{
						String linkHref = link.attr("onclick");
						String coursePageLink = urlFromOnclick(linkHref);
						try
						{
							Document coursePage = Jsoup.connect(coursePageLink)
									.get();

							String desc = getReqString(coursePage, matchDesc);
							String title = getTitleString(coursePage);
							String pr = getReqString(coursePage, matchPrereq);
							System.out.println(linkText + " prereqs: " + pr);
							ReqTree.treeFromReqString(linkText, pr, null, desc,
									title);
							String cr = getReqString(coursePage, matchCoreq);
							System.out.println(linkText + " coreqs: " + cr);
							ReqTree.treeFromReqString(linkText, cr, null, desc,
									title);
						} catch (IOException e)
						{
							System.out.println("Failed to load course page:\n"
									+ coursePageLink);
							continue;
						}

						try
						{
							// don't do too many requests at once
							Thread.sleep(200);
						} catch (InterruptedException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			} catch (IOException e)
			{
				System.out.println("Failed to load dept page: ");
				System.out.println(s);
				// e.printStackTrace();
			}
		}
		Edge.writeEdgesToNodes();
		Node.writeAllNodesToFile();
		Node.prereqFWriter.append("]");

		Gson gson = new Gson();
		String edgeJsonString = gson.toJson(Edge.edgeList);
		edgeWriter.append(edgeJsonString);
		
		Edge.edgeList.clear();

		Node.prereqFWriter.close();
		edgeWriter.close();
		Node.prereqFOut.close();
		edgeFOut.close();
		System.out.println("Completed saving JSON pre/co-reqs to files");
	}

	static String urlFromOnclick(String onclickStr)
	{
		// TODO: parse this instead of using length
		return socPrefix + onclickStr.substring(13, 74);
	}

	static String getReqString(Document coursePage, Pattern reqMatcher)
	{
		Elements fontTaggedEles = coursePage.getElementsByTag("font");

		for (int i = 0; i < fontTaggedEles.size(); i++)
		{
			Element fontEle = fontTaggedEles.get(i);
			if (reqMatcher.matcher(fontEle.html()).matches()
					&& i < fontTaggedEles.size() - 1)
			{
				// if 'None.', return null
				if (!matchNone.matcher(fontTaggedEles.get(i + 1).html())
						.matches())
				{
					return fontTaggedEles.get(i + 1).html();
				} else
				{
					return "";
				}
			}
		}
		return "";
	}

	static String getTitleString(Document coursePage)
	{
		Elements bTaggedEles = coursePage.getElementsByTag("b");

		for (int i = 0; i < bTaggedEles.size(); i++)
		{
			Element bEle = bTaggedEles.get(i);
			if (matchTitle.matcher(bEle.html()).matches()
					&& i < bTaggedEles.size())
			{
				// if 'None.', return null
				if (!matchNone.matcher(bTaggedEles.get(i).html()).matches())
				{
					return bTaggedEles.get(i).html().substring(17);
				} else
				{
					return "";
				}
			}
		}
		return "";
	}
}
