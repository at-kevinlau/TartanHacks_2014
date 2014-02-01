package th2014_parser;

import java.io.*;
import java.util.HashMap;
import java.util.regex.Pattern;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

public class Main
{
	//@formatter:off
	final static String[] socURLStrings = {
"https://enr-apps.as.cmu.edu/open/SOC/SOCServlet?Formname=GET_CLASSES&SUBMIT=Retrieve+Schedule&SEMESTER=S14&GRAD_UNDER=All&MINI=NO&DEPT=ECE"
	};
	//@formatter:on
	final static Pattern matchCourseNumber = Pattern.compile("^[0-9]{5,5}$");
	final static Pattern matchPrereq = Pattern.compile("<b>Prerequisites:</b>");
	final static Pattern matchCoreq = Pattern.compile("<b>Corequisites:</b>.*");
	final static Pattern matchNone = Pattern.compile("None.*");
	final static String socPrefix = "https://enr-apps.as.cmu.edu/open/SOC/";

	public static void main(String[] args) throws IOException
	{
		//@formatter:off
		
		/*
		 System.out.println(PrereqTree .treeFromPrereqString( "18510",
		 "(18300 and 18320) or (18300 and 18491) or (18310 and 18320) or (18310 and 18491) or (18491 and 18320) or (18300 and 18421) or (18310 and 18421) or (18"
		 , null));
		*/
		/*
		ReqTree
				.treeFromReqString(
						"18510",
						"(18300 and 18320) or (18300 and 18491) or (18310 and 18320) or (18310 and 18491) or (18491 and 18320) or (18300 and 18421) or (18310 and 18421) or (18",
						null).generateJSON();
		if (true)
		{
			return;
		}
		*/
		//@formatter:on
		File prereqFile = new File("../../prereq.json");
		File coreqFile = new File("../../coreq.json");
		prereqFile.createNewFile();
		coreqFile.createNewFile();
		FileOutputStream prereqFOut = new FileOutputStream(prereqFile);
		FileOutputStream coreqFOut = new FileOutputStream(coreqFile);
		OutputStreamWriter prereqFWriter = new OutputStreamWriter(prereqFOut);
		OutputStreamWriter coreqFWriter = new OutputStreamWriter(coreqFOut);

		for (String s : socURLStrings)
		{
			try
			{
				Document deptPage = Jsoup.connect(s).get();

				// TODO: hacky way to get this section
				Elements infoSection = deptPage.getElementsByAttributeValue(
						"cellpadding", "5");
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

							String pr = getReqString(coursePage, matchPrereq);
							String cr = getReqString(coursePage, matchCoreq);
							System.out.println(linkText + " prereqs: " + pr);
							System.out.println(linkText + " coreqs: " + cr);
							prereqFWriter.append(ReqTree.treeFromReqString(
									linkText, pr, null).generateJSON());
							coreqFWriter.append(ReqTree.treeFromReqString(
									linkText, cr, null).generateJSON());

						} catch (IOException e)
						{
							System.out
									.println("Failed to load course page:\ncoursePageLink");
							continue;
						}

						try
						{
							// don't do too many requests at once
							Thread.sleep(100);
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
		prereqFWriter.close();
		coreqFWriter.close();
		prereqFOut.close();
		coreqFOut.close();
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
				return fontTaggedEles.get(i + 1).html();
			}
		}
		return null;
	}
}
