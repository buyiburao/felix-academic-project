<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ page import="label.LabelManager" %>

<%
            LabelManager.removeUserName(response);
            response.sendRedirect("login.jsp");
%>
