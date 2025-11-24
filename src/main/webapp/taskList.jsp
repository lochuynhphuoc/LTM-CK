<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:forEach var="task" items="${tasks}">
    <tr>
        <td>${task.id}</td>
        <td>
            <c:out value="${task.sourceContent.length() > 20 ? task.sourceContent.substring(0, 20) : task.sourceContent}" />
            ...
            <button type="button" class="view-btn" onclick="showPreview('Source Content', 'source-content-${task.id}')">View</button>
            <div id="source-content-${task.id}" style="display:none;">
                <c:out value="${task.sourceContent}" />
            </div>
        </td>
        <td>
            <c:out value="${task.targetContent.length() > 20 ? task.targetContent.substring(0, 20) : task.targetContent}" />
            ...
            <button type="button" class="view-btn" onclick="showPreview('Target Content', 'target-content-${task.id}')">View</button>
            <div id="target-content-${task.id}" style="display:none;">
                <c:out value="${task.targetContent}" />
            </div>
        </td>
        <td>
            <c:choose>
                <c:when test="${not empty task.comparisonDetails}">
                    <c:set var="comparisonSnippet" value="${task.comparisonDetails.length() > 20 ? task.comparisonDetails.substring(0, 20) : task.comparisonDetails}" />
                    <c:out value="${comparisonSnippet}" /> ...
                    <button type="button" class="view-btn" onclick="showPreview('Comparison Details', 'comparison-details-${task.id}')">View</button>
                    <div id="comparison-details-${task.id}" style="display:none;">
                        <c:out value="${task.comparisonDetails}" />
                    </div>
                </c:when>
                <c:otherwise>
                    <span>Pending...</span>
                </c:otherwise>
            </c:choose>
        </td>
        <td>
            <c:set var="statusClass" value="status-${task.status.toLowerCase()}" />
            <span class="${statusClass}">${task.status}</span>
        </td>
        <td>${task.result}</td>
        <td>${task.createdAt}</td>
    </tr>
</c:forEach>
