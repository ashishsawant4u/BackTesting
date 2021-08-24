<%@ tag language="java" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>      
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>   
<div class="row">

<div class="table-responsive col-md-6">
<table class="table table-bordered table-hover">
	 <caption class="caption-top">Monthly Investment</caption>
	<thead>
	    <tr>
	      <th scope="col">Month</th>
	      <th scope="col">Invested Amount</th>
	    </tr>
  </thead>
  <tbody>
	<c:forEach var="entry" items="${monthlyInvestmentMap}">
		<tr>
			
			<td class="table-primary"><fmt:formatDate value="${entry.key}" pattern="MMM-yyyy"/></td>
			<td>${entry.value}</td>
			
		</tr>
	</c:forEach>
	</tbody>
</table>
</div>

<div class="table-responsive col-md-6">
<table class="table table-bordered table-hover">
	<caption class="caption-top">Monthly Profit&Loss</caption>
	<thead>
	    <tr>
	      <th scope="col">Month</th>
	      <th scope="col">P/L</th>
	    </tr>
  </thead>
  <tbody>
	<c:forEach var="entry" items="${montlyUnrealisedProfitTotalMap}">
		<tr>
			
			<fmt:parseNumber var = "amount" type = "number" value = "${entry.value}" />
			<c:if test="${amount lt 0}">
					<td class="table-danger table-primary"><fmt:formatDate value="${entry.key}" pattern="MMM-yyyy"/></td>
					<td class="table-danger">${entry.value}</td>
			</c:if>
			<c:if test="${amount gt 0}">
					<td class="table-primary"><fmt:formatDate value="${entry.key}" pattern="MMM-yyyy"/></td>
					<td>${entry.value}</td>
			</c:if>
			
		</tr>
	</c:forEach>
	</tbody>
</table>
</div>

</div>