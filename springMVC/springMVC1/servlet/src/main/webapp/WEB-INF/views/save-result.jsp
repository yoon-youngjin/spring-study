<%--<%@ page import="hello.servlet.domain.member.Member" %>--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8">
</head>
<body>
성공
<ul>
    <%--    <li>id=<%=((Member)request.getAttribute("member")).getId()%></li>--%>
    <li>id=${member.id}</li>
    <%--    <li>username=<%=((Member)request.getAttribute("member")).getUsername()%></li>--%>
    <li>username=${member.username}</li>
    <%--    <li>age=<%=((Member)request.getAttribute("member")).getAge()%></li>--%>
    <li>age=${member.age}</li>
</ul>
</html>
<a href="/index.html">메인</a>
</body>