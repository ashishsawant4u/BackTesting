<%@ tag language="java" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>      
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>   


<div class="input-group mb-3 mt-2">
  <span class="input-group-text" id="basic-addon1">Filter</span>
  <input id="yearlyTableFilter" type="text" class="form-control" placeholder="Type here..." aria-describedby="basic-addon1">
</div>


<table class="table table-bordered table-hover" id="yearlyReportTable">
	<thead style="position: sticky;top: 0" class="table-dark">
	    <tr>
	      <th scope="col">Year</th>
	      <th scope="col">Trade Count</th>
	      <th scope="col">Investment</th>
	      <th scope="col">P/L</th>
	      <th scope="col">% Gain</th>
	    </tr>
  </thead>
  <tbody class="searchable">
	<c:forEach var="report" items="${yearlyReportForTrades}">
		<c:if test="${report.profitAndLoss gt 0}">
				<tr>
		</c:if>
		<c:if test="${report.profitAndLoss lt 0}">
				<tr class="table-danger">
		</c:if>
			<td class="table-primary"><fmt:formatDate value="${report.year}" pattern="yyyy"/></td>
			<td>${report.tradesCount}</td>
			<td><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${report.investment}" /></td>
			<td><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${report.profitAndLoss}" /></td>
			<td><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${report.percentageGain}" />%</td>
		</tr>
	</c:forEach>
	</tbody>
</table>
