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
	      <th scope="col">Listing Close</th>
	      <th scope="col">Listing Gains(%)</th>
	      <th scope="col">Date after ${monthsAfterListing} months</th>
	      <th scope="col">Price after ${monthsAfterListing} months</th>
	      <th scope="col">Gains(%) after ${monthsAfterListing} months</th>
	    </tr>
  </thead>
  <tbody class="searchable">
	<c:forEach var="ipo" items="${ipoHistoricalData}">
		<c:if test="${ipo.issueSize le 2500}">
				<tr>
					<td class="table-primary">${ipo.symbol}</td>
					<td>${ipo.ipoName}</td>
					<td><fmt:formatDate value="${ipo.listingDate}" pattern="dd-MMM-yyyy"/></td>
					<td><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${ipo.issueSize}" /></td>
					<td><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${ipo.issuePrice}" /></td>
					<td><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${ipo.listingOpenPrice}" /></td>
					<td><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${ipo.listingClosePrice}" /></td>
					<td ${ipo.listingGainPecentage > 40 ? "class='bg-success text-white'" : ""}><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${ipo.listingGainPecentage}" /></td>
					<td><fmt:formatDate value="${ipo.futureDate}" pattern="dd-MMM-yyyy"/></td>
					<td><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${ipo.futurePrice}" /></td>
					<td ${ipo.futureGainPecentage > 40 ? "class='bg-success text-white'" : ""}><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${ipo.futureGainPecentage}" /></td>
				</tr>
		
		
		</c:if>
	</c:forEach>
	</tbody>
</table>
</body>
</html>