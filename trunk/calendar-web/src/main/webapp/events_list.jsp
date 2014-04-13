<%--
  Created by IntelliJ IDEA.
  User: pavol
  Date: 13.4.2014
  Time: 14:33
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- Formatovanie klasickeho DATE v jave--%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>

<html>
<head>
    <title>Events</title>
</head>
<body>

<table border="1">
    <thead>
    <tr>
        <th>Title</th>
        <th>Description</th>
        <th>Location</th>
        <th>Start date</th>
        <th>End date</th>
        <th>Action</th>
    </tr>
    </thead>
    <c:forEach items="${events}" var="e">
        <tr>
            <td><c:out value="${e.title}"/></td>
            <td><c:out value="${e.description}"/></td>
            <td><c:out value="${e.location}"/></td>
            <td><joda:format value="${e.startDate}" pattern="HH:mm:ss dd.MM.yyyy"/></td>
            <td><joda:format value="${e.endDate}" pattern="HH:mm:ss dd.MM.yyyy"/></td>
            <td><form method="post" action="${pageContext.request.contextPath}/events/delete?id=${e.id}"
                      style="margin-bottom: 0;"><input type="submit" value="Delete"></form></td>
            <td><form method="post" action="${pageContext.request.contextPath}/events/update?id=${e.id}"
                      style="margin-bottom: 0;"><input type="submit" value="Update"></form></td>
        </tr>
    </c:forEach>
</table>

<h2>
    <c:choose>
        <c:when test="${not empty event.id }">
            Updating Event
        </c:when>
        <c:otherwise>
            Adding Event
        </c:otherwise>
    </c:choose>
</h2>
<c:if test="${not empty error}">
    <div style="border: solid 1px red; background-color: yellow; padding: 10px">
        <c:out value="${error}"/>
    </div>
</c:if>

<form action="${pageContext.request.contextPath}/events/add" method="post">
    <table>
        <tr>
            <th>Title</th>
            <td><input type="text" name="title" value="${event.title}"/></td>
        </tr>
        <tr>
            <th>Description</th>
            <td><input type="text" name="description" value="<c:out value='${event.description}'/>"/></td>
        </tr>
        <tr>
            <th>Location</th>
            <td><input type="text" name="location" value="<c:out value='${event.location}'/>"/></td>
        </tr>
            <td><input type="hidden" name="id" value="<c:out value='${event.id}'/>"/></td>
        </tr>
    </table>
    <input type="Submit" value="Add" />
</form>
</body>
</html>
