<%--
  Created by IntelliJ IDEA.
  User: Mario
  Date: 17.4.2014
  Time: 11:34
  To change this template use File | Settings | File Templates.
--%>
<%@page contentType="text/html;charset=utf-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<body>

<table border="1">
    <thead>
    <tr>
        <th>Id</th>
        <th>Login</th>
        <th>Email</th>
        <th>Action</th>
    </tr>
    </thead>
    <c:forEach items="${users}" var="u">
        <tr>
            <td><c:out value="${u.id}"/></td>
            <td><c:out value="${u.login}"/></td>
            <td><c:out value="${u.email}"/></td>
            <td><form method="post" action="${pageContext.request.contextPath}/users/delete?id=${u.id}"
                      style="margin-bottom: 0;"><input type="submit" value="Delete"></form></td>
            <td><form method="post" action="${pageContext.request.contextPath}/users/update?id=${u.id}"
                      style="margin-bottom: 0;"><input type="submit" value="Update"></form></td>
        </tr>
    </c:forEach>
</table>

<h2>
    <c:choose>
        <c:when test="${not empty user.id }">
            Updating User
        </c:when>
        <c:otherwise>
            Adding User
        </c:otherwise>
    </c:choose>
</h2>
<c:if test="${not empty error}">
    <div style="border: solid 1px red; background-color: yellow; padding: 10px">
        <c:out value="${error}"/>
    </div>
</c:if>
<form action="${pageContext.request.contextPath}/users/add" method="post">
    <table>
        <tr>
            <th>User login:</th>
            <td><input type="text" name="login" value="<c:out value='${user.login}'/>"/></td>
        </tr>
        <tr>
            <th>User password</th>
            <td><input type="password" name="password" value="<c:out value='${user.password}'/>"/></td>
        </tr>
        <tr>
            <th>User email:</th>
            <td><input type="text" name="email" value="<c:out value='${user.email}'/>"/></td>
        </tr>
        <tr>
        <td><input type="hidden" name="id" value="<c:out value='${user.id}'/>"/></td>
        </tr>
    </table>
    <input type="Submit" value="Add" />
</form>
</body>
</html>