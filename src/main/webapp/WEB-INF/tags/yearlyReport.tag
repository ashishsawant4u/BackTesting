<%@ tag language="java" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>      
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>   


<div class="input-group mb-3 mt-2">
  <span class="input-group-text" id="basic-addon1">Filter</span>
  <input id="yearlyTableFilter" type="text" class="form-control" placeholder="Type here..." aria-describedby="basic-addon1">
</div>

<table class="table table-bordered table-hover" id="yearlyReportTable">
	<thead>
	    <tr>
	      <th scope="col">Year</th>
	      <th scope="col">P/L</th>
	    </tr>
  </thead>
  <tbody class="searchable">
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
