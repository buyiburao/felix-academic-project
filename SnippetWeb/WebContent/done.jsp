<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ page import="label.LabelManager"%>

<%
            String user = LabelManager.getUserName(request);
            if (user == null) {
                response.sendRedirect("index.jsp");
                return;
            }
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <style type="text/css">
            div {
                text-align: center;
                margin: 0.5em;
            }

            #frame {
                margin-top: 15em;
                text-align: center;
            }

            #user {
                font-weight: bold;
                color: blue;
            }
        </style>
        <title>Done</title>
    </head>
    <body>
        <div id="frame">
            <div>Congratulations!&nbsp;Thank you,&nbsp; <span id="user"><%=user%></span>!
            </div>
            <div>You have finished all the label jobs.</div>
            <div><a href="index.jsp">Home Page</a></div>
        </div>
    </body>
</html>