<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ page import="label.LabelManager"%>
<%@ page import="label.PageInfo"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Map"%>

<%
            String user = LabelManager.getUserName(request);
            if (user == null) {
                response.sendRedirect("index.jsp");
            }

            PageInfo info = null;
            try {
                info = LabelManager.findNewTrainingItem(user);
                if (info == null) {
                    response.sendRedirect("done.jsp");
                    return;
                }
            } catch (Exception e) {
            	System.out.println(e);
                response.sendError(
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.toString());
            }
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="label.css" rel="stylesheet" type="text/css">
        <script type="text/javascript" src="http://www.google.com/jsapi"></script>
        <script type="text/javascript" src="label.js"></script>
        <title>Label</title>
    </head>
    <body>
        <input type="hidden" id="query" value="<%= info.getRecord().getQuery()%>" />
        <input type="hidden" id="url" value="<%= info.getRecord().getUrl()%>" />
        <input type="hidden" id="title" value="<%= info.getRecord().getTitle()%>" />
        <input type="hidden" id="user" value="<%= user%>" />

        <div id="header">
            <div id="header_user">
                <span class="text"><%=user%></span>
                <span><a href="logout.jsp">Log Out</a></span>
            </div>
            <div id="header_query">
                <span class="text">Query:</span>
                <span id="query_en"><%= info.getRecord().getQuery()%></span>
                <span id="query_cn"></span>
            </div>
            <div id="header_title">
                <span class="text">Title:</span>
                <span id="title_en"><%= info.getRecord().getTitle()%></span>
                <span id="title_cn"></span>
            </div>
            <hr />
        </div>

        <div>
            <div id="left">
                <div id="sentences">
                    <%
                                List<String> candidates = info.getCandidate(9);
                                for (String sentence : info.getSentences()) {
                                    String className = (candidates.contains(sentence) ? "selecting " : "") + "sentence";

                                    String chn = info.getTranslation().get(sentence);
                                    if (chn == null) {
                                        chn = "";
                                    }
                    %>
                    <div class="<%= className%>" title="<%= chn%>"><%= sentence%></div>
                    <%
                                }
                    %>
                </div>
                &nbsp;
                <div class="buttons">
                    <input type="button" value="Select" class="button" id="btnSelect" />
                </div>
            </div>
            <div id="right">
                <div id="results">
                </div>
                &nbsp;
                <div class="buttons">
                    <input type="button" value="Up" class="button" id="btnUp" />
                    <input type="button" value="Down" class="button" id="btnDown" />
                    <input type="button" value="Remove" class="button" id="btnRemove" />
                </div>
            </div>
            <div id="submit" class="right">
                <input type="button" value="OK" class="button" id="ok" />
                <a href="">Skip&gt;</a>
            </div>
        </div>

        <div id="footer">
            <hr />
            <div id="google_branding" class="center"></div>
        </div>
    </body>
</html>