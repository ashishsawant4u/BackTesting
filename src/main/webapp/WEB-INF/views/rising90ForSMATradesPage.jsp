<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>      
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>        
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Rising90-SMA${simpleMovingAvg}-Trades</title>
</head>
<body>

<tags:scripts/>

<div class="container">
	<h1>Trade Finder SMA${simpleMovingAvg} Rising90</h1>
</div>

<table class="table table-bordered table-hover" id="tradeListTable">
	<thead style="position: sticky;top: 0" class="table-dark">
	    <tr>
	      <th scope="col">Instrument</th>
	      <th scope="col">Entry Date</th>
	      <th scope="col">Buy Price</th>
	      <th scope="col">Stop Loss Price</th>
	      <th scope="col">Target Price</th>
	      <th scope="col">Loss Per Unit</th>
	      <th scope="col">Quantity</th>
	      <th scope="col">Investment</th>
	      <th scope="col">Max Possible Profit</th>
	      <th scope="col">Max Possible Loss</th>
	    </tr>
  </thead>
  <tbody class="searchable">
	<c:forEach var="trade" items="${potentialTradesList}">
		<tr>
			<td>${trade.symbol}</td>
			<td><fmt:formatDate value="${trade.marketDate}" pattern="dd-MMM-yyyy"/></td>
			<td><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${trade.tradeEntry.buyPrice}" /></td>
			<td><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${trade.tradeEntry.stopLossPrice}" /></td>
			<td><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${trade.tradeEntry.targetPrice}" /></td>
			<td><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${trade.tradeEntry.lossPerUnit}" /></td>
			<td>${trade.tradeEntry.quantity}</td>
			<td><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${trade.tradeEntry.investment}" /></td>
			<td><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${(trade.tradeEntry.targetPrice-trade.tradeEntry.buyPrice) * trade.tradeEntry.quantity}" /></td>
			<td><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${trade.tradeEntry.lossPerUnit * trade.tradeEntry.quantity}" /></td>
		</tr>
	</c:forEach>
	</tbody>
</table>


</body>
</html>