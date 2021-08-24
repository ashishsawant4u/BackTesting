<%@ tag language="java" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>      
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>   
<div class="row">

<div class="input-group mb-3 mt-2">
  <span class="input-group-text" id="basic-addon1">Filter</span>
  <input id="monthlyTableFilter" type="text" class="form-control" placeholder="Type here..." aria-describedby="basic-addon1">
</div>

<div class="table-responsive col-md-4">
<table class="table table-bordered table-hover" id="monthlyInvestmentTable">
	 <caption class="caption-top">Monthly Investment</caption>
	<thead>
	    <tr>
	      <th scope="col">Month</th>
	      <th scope="col">Invested Amount</th>
	    </tr>
  </thead>
  <tbody class="searchable">
	<c:forEach var="entry" items="${monthlyInvestmentMap}">
		<tr>
			
			<td class="table-primary"><fmt:formatDate value="${entry.key}" pattern="MMM-yyyy"/></td>
			<td>${entry.value}</td>
			
		</tr>
	</c:forEach>
	</tbody>
</table>
</div>

<div class="table-responsive col-md-4">
<table class="table table-bordered table-hover" id="montlyUnrealisedProfitTable">
	<caption class="caption-top">Monthly Profit&Loss</caption>
	<thead>
	    <tr>
	      <th scope="col">Month</th>
	      <th scope="col">P/L</th>
	    </tr>
  </thead>
  <tbody class="searchable">
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

<div class="table-responsive col-md-4">
<table class="table table-bordered table-hover" id="monthlyTradeCountTable">
	 <caption class="caption-top">Monthly Trades Count</caption>
	<thead>
	    <tr>
	      <th scope="col">Month</th>
	      <th scope="col">Trade Count</th>
	    </tr>
  </thead>
  <tbody class="searchable">
	<c:forEach var="entry" items="${monthlyTradesCountMap}">
		<tr>
			
			<td class="table-primary"><fmt:formatDate value="${entry.key}" pattern="MMM-yyyy"/></td>
			<td>${entry.value}</td>
			
		</tr>
	</c:forEach>
	</tbody>
</table>
</div>

</div>