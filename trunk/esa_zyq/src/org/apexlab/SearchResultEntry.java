package org.apexlab;

public class SearchResultEntry {
	private String url;
	private String title;
	private String content;
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public SearchResultEntry(String url, String title, String content) {
		super();
		this.url = url;
		this.title = title;
		this.content = content;
	}
	public String toHTML(){
		return "<a href=" + url + "><b>" + title + "</b></a><br>" + this.content; 
	}
	
}
