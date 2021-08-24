<%@ tag language="java" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>      
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>   


<table class="table table-bordered table-hover">
	<thead>
	    <tr>
	      <th scope="col">Year</th>
	      <th scope="col">P/L</th>
	    </tr>
  </thead>
  <tbody>
	<c:forEach var="entry" items="${yearlyUnrealisedProfitTotalMap}">
		<tr>
			
			<fmt:parseNumber var = "amount" type = "number" value = "${entry.value}" />
			<c:if test="${amount lt 0}">
					<td class="table-danger table-primary">${entry.key}</td>
					<td class="table-danger">${entry.value}</td>
			</c:if>
			<c:if test="${amount gt 0}">
					<td class="table-primary">${entry.key}</td>
					<td>${entry.value}</td>
			</c:if>
			
		</tr>
	</c:forEach>
	</tbody>
</table>
