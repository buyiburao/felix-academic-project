<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ page import="label.LabelManager"%>

<%
            String user = LabelManager.getUserName(request);
            String next = (user == null ? "login.jsp" : "label.jsp");
            response.sendRedirect(next);
%>