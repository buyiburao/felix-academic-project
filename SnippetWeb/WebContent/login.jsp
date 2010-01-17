<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ page import="label.LabelManager" %>

<%
            String user = request.getParameter("user");
            if (user != null) {
                LabelManager.updateUserName(response, user);
                response.sendRedirect("label.jsp");
                return;
            }
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="label.css" rel="stylesheet" type="text/css">
        <script type="text/javascript">
            function login() {
                user = document.getElementById("user").value.replace(/^\s+|\s+$/g, "");
                if (user == "") {
                    alert("Please enter your username.");
                    document.getElementById("user").focus();
                } else {
                    form = document.createElement("form");
                    form.action = "";
                    form.method = "POST";

                    input = document.createElement("input");
                    input.name = "user";
                    input.value = user;
                    form.appendChild(input);
                    document.body.appendChild(form);
                    form.submit();
                }
            }

            function login_on_load() {
                document.getElementById("login").onclick = login;
                document.getElementById("user").onkeypress = function(event) {
                    if (event.keyCode == 13) {
                        login();
                    }
                };
                document.getElementById("user").focus();
            }
        </script>
        <style type="text/css">
            #frame {
                margin-top: 15em;
                text-align: center;
            }
        </style>
        <title>Log in</title>
    </head>
    <body>
        <div id="frame">
            <div><input type="text" id="user" name="user" /></div>
            <div><input type="button" value="Log In" id="login" /></div>
        </div>

        <script type="text/javascript">
            login_on_load();
        </script>
    </body>
</html>