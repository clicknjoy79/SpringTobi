<%--
  Created by IntelliJ IDEA.
  User: okh
  Date: 2017-08-30
  Time: 오전 7:49
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
<head>
    <title>LoadTimeWeavingTest</title>
</head>
<body>
    Member 클래스에 로드타임 위빙이 적용 되었나???<br/>
    <spring:eval expression="member.service.helloService()"/>
</body>
</html>
