package th2014_parser;

import java.io.*;
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
	final static Pattern matchCoreq = Pattern.compile("Corequisite");
	final static String socPrefix = "https://enr-apps.as.cmu.edu/open/SOC/";

	public static void main(String[] args)
	{
		// ArrayList<URL> socURLs = new ArrayList<URL>();
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
							System.out.println(linkText + " prereqs: "
									+ getPrereqString(coursePage));
						} catch (IOException e)
						{
							System.out
									.println("Failed to load course page:\ncoursePageLink");
							continue;
						}

						try
						{
							// don't do too many requests at once
							Thread.sleep(250);
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
	}

	static String urlFromOnclick(String onclickStr)
	{
		// TODO: parse this instead of using length
		return socPrefix + onclickStr.substring(13, 74);
	}

	static String getPrereqString(Document coursePage)
	{
		Elements fontTaggedEles = coursePage.getElementsByTag("font");

		for (int i = 0; i < fontTaggedEles.size(); i++)
		{
			Element fontEle = fontTaggedEles.get(i);
			if (matchPrereq.matcher(fontEle.html()).matches()
					&& i < fontTaggedEles.size() - 1)
			{
				return fontTaggedEles.get(i + 1).html();
			}
		}
		return null;
	}
}
