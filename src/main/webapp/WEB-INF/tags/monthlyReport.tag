<%@ tag language="java" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>      
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>   
<div class="row">

<div class="input-group mb-3 mt-2">
  <span class="input-group-text" id="basic-addon1">Filter</span>
  <input id="monthlyTableFilter" type="text" class="form-control" placeholder="Type here..." aria-describedby="basic-addon1">
</div>

<table class="table table-bordered table-hover" id="monthlyTradeReportTable">
	<thead>
	    <tr>
	      <th scope="col">Month</th>
	      <th scope="col">Trade Count</th>
	      <th scope="col">Investment</th>
	      <th scope="col">P/L</th>
	      <th scope="col">% Gain</th>
	    </tr>
  </thead>
  <tbody class="searchable">
	<c:forEach var="report" items="${monthlyReportForTrades}">
		<tr>
			<td class="table-primary"><fmt:formatDate value="${report.month}" pattern="MMM-yyyy"/></td>
			<td>${report.tradesCount}</td>
			<td><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${report.investment}" /></td>
			<td><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${report.profitAndLoss}" /></td>
			<td><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${report.percentageGain}" />%</td>
		</tr>
	</c:forEach>
	</tbody>
</table>

</div>