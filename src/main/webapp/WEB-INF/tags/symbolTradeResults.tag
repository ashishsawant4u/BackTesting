<%@ tag language="java" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>      
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>   


<table class="table table-bordered table-hover">
	<thead>
	    <tr>
	      <th scope="col">Instrument</th>
	      <th scope="col">Trades Found</th>
	      <th scope="col">Target Exit Count</th>
	      <th scope="col">Stop Loss Count</th>
	      <th scope="col">Overall P/L</th>
	    </tr>
  </thead>
  <tbody>
	<c:forEach var="candle" items="${tradeResults}">
		<tr>
			<td class="table-primary">${candle.instrument}</td>
			<td>${candle.tradesCount}</td>
			<td>${candle.targetExistCount}</td>
			<td>${candle.stopLossCount}</td>
			<td>${candle.profitableTrades}</td>
		</tr>
	</c:forEach>
	</tbody>
</table>
