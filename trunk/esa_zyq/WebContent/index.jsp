<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="org.apexlab.*"%>
<%@ page import="java.io.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Google &amp; esa comparison</title>
</head>
<body>
<table>
	<tr>
		<td>
		<form method="get" action=""><span>Query:&nbsp;</span> <input
			type="text" name="query" id="query"></input> <input type="submit"></input>
		</form>
		</td>
	</tr>
	<%
		ApexSearcher googleSearcher = new GoogleSearcher();
		ApexSearcher esaSearcher = new EsaSearcher();
		String query = request.getParameter("query");
		if (query != null) {
			List<SearchResultEntry> googleResults = googleSearcher.search(
					query, 100);
			List<SearchResultEntry> esaResults = esaSearcher.search(query,
					100);
			int max = Math.max(googleResults.size(), esaResults.size());
			for (int i = 0; i < max; ++i) {
				String google = (i < googleResults.size() ? googleResults.get(i).toHTML() : "&nbsp;");
				String esa = (i < esaResults.size() ? esaResults.get(i).toHTML() : "&nbsp;");
	%>
	<tr>
		<td width="50%">
			<%= google %>
		</td>
		<td width="50%">
			<%= esa %>
		</td>
	</tr>
	<%
			}
		}
	%>
</table>
</body>
</html>