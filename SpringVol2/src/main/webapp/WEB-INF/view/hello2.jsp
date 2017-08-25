<%--
  Created by IntelliJ IDEA.
  User: okh
  Date: 2017-08-24
  Time: 오후 4:52
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<style>
    .errorMessage { border: solid 2px red; }
</style>
<html>
<head>
    <title>Title</title>
</head>
<body>
    <form:form commandName="user">
        <form:label path="name" cssErrorClass="errorMessage">Name</form:label> :
        <form:input path="name" size="30"/>
        <form:errors path="name" cssClass="errorMessage"/><br/>

        <%--체크박스들을 제대로 동작시키려면 user.interests 필드가 String[] 이어야 한다.
        해당 필드에 value 값이 배열로 세팅되어야 한다.--%>
        <%--프라퍼티 값으로 객체가 할당된 경우에는 객체의 equals 메서드로 동일성 비교를 한다.(컬랙션의 요소들과 동일성 비교를 함)
        그래서 동일한 경우에 checked 를 표기해준다.--%>
        <form:checkboxes path="interests" items="${interests}" delimiter="|"/><br/>
        <form:radiobuttons path="userType" items="${types}" itemValue="id" itemLabel="name"/><br/>
        <form:select path="userLocal">
            <form:options items="${locals}" itemLabel="name" itemValue="id"/>
        </form:select><br/>

        <spring:eval expression="user.userType"/><br/>
        <spring:eval expression="user.userType.name"/><br/>
        <spring:eval expression="user.userLocal"/><br/>
        <spring:eval expression="user.userLocal.name"/><br/>
        <input type="submit" value="SEND"/>
    </form:form>

    <img src="/ui/images/cat.jpg"/>
</body>
</html>
