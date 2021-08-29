<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>      
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>        
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>IPO-Backtest-${monthsAfterListing}</title>
</head>
<body>

<tags:scripts/>

<div class="container">
	<h1>IPO Backtest ${monthsAfterListing} after listing</h1>
</div>

<table class="table table-bordered table-hover" id="ipoHistoricalData">
	<thead style="position: sticky;top: 0" class="table-dark">
	    <tr>
	      <th scope="col">Symbol</th>
	      <th scope="col">Company Name</th>
	      <th scope="col">Listing Date</th>
	      <th scope="col">Issue Size</th>
	      <th scope="col">Issue Price</th>
	      <th scope="col">Listing Open</th>
	      <th scope="col">Listing Gains(%)</th>
	      <th scope="col">Future Date</th>
	      <th scope="col">Future Price</th>
	      <th scope="col">Future Gains(%)</th>
	    </tr>
  </thead>
  <tbody class="searchable">
	<c:forEach var="ipo" items="${ipoHistoricalData}">
		<c:if test="${ipo.issueSize le 2500}">
		
				<c:if test="${ipo.listingOpenPrice > ipo.issuePrice}">
					<tr class="table-success">
				</c:if>
				<c:if test="${ipo.listingOpenPrice < ipo.issuePrice}">
					<tr class="table-danger">
				</c:if>
					<td>${ipo.symbol}</td>
					<td>${ipo.ipoName}</td>
					<td><fmt:formatDate value="${ipo.listingDate}" pattern="dd-MMM-yyyy"/></td>
					<td><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${ipo.issueSize}" /></td>
					<td><b><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${ipo.issuePrice}" /></b></td>
					<td><b><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${ipo.listingOpenPrice}" /></b></td>
					<td><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${ipo.listingGainPecentage}" /></td>
					<td><fmt:formatDate value="${ipo.futureDate}" pattern="dd-MMM-yyyy"/></td>
					<td><b><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${ipo.futurePrice}" /></b></td>
					<td><b><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${ipo.futureGainPecentage}" /></b></td>
				</tr>
		
		
		</c:if>
	</c:forEach>
	</tbody>
</table>
</body>
</html>