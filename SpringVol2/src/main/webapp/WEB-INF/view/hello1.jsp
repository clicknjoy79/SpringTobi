<%--
  Created by IntelliJ IDEA.
  User: okh
  Date: 2017-08-24
  Time: 오후 3:37
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<style>
    .errorMessage { border: 2px solid red; }
</style>
<html>
<head>
    <title>Title</title>
</head>
<body>
    <form method="post">
    <spring:bind path="user.name">
        <label for="name"
               <c:if test="${status.errorMessage != ''}">class="errorMessage"</c:if>
        >Name</label> :
        <input type="text" id="name" name="${status.expression}" size="30"
               value="${status.value}"/>
        <span class="errorMessage">
            <c:forEach var="errorMessage" items="${status.errorMessages}">
                ${errorMessage}
            </c:forEach>
        </span>
    </spring:bind>
        <br/>
        <input type="submit" value="SEND"/>
    </form>
</body>
</html>
