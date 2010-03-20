<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="org.apexlab.*"%>
<%@ page import="org.apexlab.docserver.*"%>
<%@ page import="java.rmi.*"%>
<%@ page import="java.net.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
IWikiDocProvider wdp = (IWikiDocProvider)Naming.lookup(Config.serverString);
String title = URLDecoder.decode(request.getParameter("title"), "utf8");
if (title == null)
	title = "Not found";
String content = wdp.getDocContentByTitle(title).replace("\n", "<BR>");
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><%=title %></title>
</head>
<body>
<%=content %>
</body>
</html>