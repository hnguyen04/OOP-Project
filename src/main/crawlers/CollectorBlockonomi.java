package crawlers;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CollectorBlockonomi 
extends Collector
implements IGetData {

	@Override
	public void getData() {
		setWebsiteSource("https://blockonomi.com");
		setFileName("blockonomi.csv");
		setPageIndex("/all/page/");
		setOutputFile();
		createOutputFile();
		webTraverse();
	}

	@Override
	public void webTraverse() {
		count = 129;
		String 	webPage = new String(""),
				url = new String("");
		Document doc;
		Elements urlsList;
		do {
			webPage = websiteSource + pageIndex + String.valueOf(count);
			doc = this.request(webPage, true);
			if(doc == null) break;
			urlsList = doc.select("article.l-post.grid-post.grid-base-post div.media a[href]");
			if(urlsList.isEmpty()) break;
			for(Element link: urlsList) {
				url = link.attr("href");
				if(url.isEmpty() == false) {
					pageTraverse(url);
				}
			}
			count++;
			System.out.println(count);
		} while(true);
	}

	@Override
	public void pageTraverse(String url) {
		Document doc = this.request(url, false);
		if (doc == null) return;
		String author, type = "News Article", title, content, dateCreated, category = "News", summary = "", tags = "";
		title = doc.selectFirst(".is-title.post-title").text();
        StringBuilder contentBuilder = new StringBuilder();
        Element contentElement = doc.selectFirst(".post-content.cf.entry-content.content-spacious");
        if (contentElement != null) {
            Elements paragraphs = contentElement.select("p");
            for (Element p : paragraphs) {
                contentBuilder.append(p.text()).append(" ");
            }
        }
        content = contentBuilder.toString().trim().replace('\"', '\'');
        Elements postDatesElements = doc.select(".post-date");
        dateCreated = postDatesElements.isEmpty() ? "" : postDatesElements.first().text();
        dateCreated = dateCreated.replace('\"', '\'');
        Elements categoriesElements = doc.select("[rel=category]");
        category = categoriesElements.isEmpty() ? "" : categoriesElements.text();
        category = category.replace('\"', '\'');
        Element authorElements = doc.selectFirst("[rel=author]");
        author = authorElements != null ? authorElements.text() : "";
        author = author.replace('\"', '\'');
		writeOutputFile(url, type, title, content, dateCreated, author, category, summary, tags);
	}
}