<%@ tag language="java" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>      
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>   


<!-- <div class="input-group mb-3 mt-2">
  <span class="input-group-text" id="basic-addon1">Filter</span>
  <input id="symbolwisetraderesultTableFilter" type="text" class="form-control" placeholder="Type here..." aria-describedby="basic-addon1">
</div> -->

<table class="table table-bordered table-hover" id="symbolwisetraderesultTable">
	<thead style="position: sticky;top: 0" class="table-dark">
	    <tr>
	      <th scope="col" >Instrument</th>
	      <th scope="col">Trades Found</th>
	      <th scope="col">Target Exit Count</th>
	      <th scope="col">Stop Loss Count</th>
	      <th scope="col">Profitable Trades Count</th>
	    </tr>
  </thead>
  <tbody class="searchable">
	<c:forEach var="candle" items="${tradeResults}">
		<tr>
			<td class="table-primary">${candle.instrument}</td>
			<td>${candle.tradesCount}</td>
			<td>${candle.targetExistCount}</td>
			<td>${candle.stopLossCount}</td>
			<c:if test="${candle.profitableTrades lt 0 or candle.profitableTrades eq 0}">
				<td class="table-danger">${candle.profitableTrades}</td>
			</c:if>
			<c:if test="${candle.profitableTrades gt 0}">
				<td>${candle.profitableTrades}</td>
			</c:if>
		</tr>
	</c:forEach>
	</tbody>
</table>
