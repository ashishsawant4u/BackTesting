<%@ tag language="java" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>      
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>   


<!-- <div class="input-group mb-3 mt-2">
  <span class="input-group-text" id="basic-addon1">Filter</span>
  <input id="tradeListTableFilter" type="text" class="form-control" placeholder="Type here..." aria-describedby="basic-addon1">
</div> -->

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
	      <th scope="col">Max Possible Loss</th>
	      <th scope="col">Exist Date</th>
	      <th scope="col">Trade Status</th>
	      <th scope="col">P/L</th>
	       <th scope="col">Trade Duration</th>
	    </tr>
  </thead>
  <tbody class="searchable">
	<c:forEach var="trade" items="${tradeList}">
		<c:if test="${trade.tradeEntry.tradeStatus eq 'TARGET EXIT'}">
				<tr>
		</c:if>
		<c:if test="${trade.tradeEntry.tradeStatus eq 'STOP LOSS'}">
				<tr class="table-danger">
		</c:if>
		
			<td>${trade.symbol}</td>
			<td><fmt:formatDate value="${trade.marketDate}" pattern="dd-MMM-yyyy"/></td>
			<td><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${trade.tradeEntry.buyPrice}" /></td>
			<td><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${trade.tradeEntry.stopLossPrice}" /></td>
			<td><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${trade.tradeEntry.targetPrice}" /></td>
			<td><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${trade.tradeEntry.lossPerUnit}" /></td>
			<td>${trade.tradeEntry.quantity}</td>
			<td><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${trade.tradeEntry.investment}" /></td>
			<td><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${trade.tradeEntry.lossPerUnit * trade.tradeEntry.quantity}" /></td>
			<td><fmt:formatDate value="${trade.tradeEntry.tradeExitDate}" pattern="dd-MMM-yyyy"/></td>
			<td>${trade.tradeEntry.tradeStatus}</td>
			<td><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${trade.pnlAmount}" /></td>
			<td>${trade.tradeEntry.tradeDuration}</td>
		</tr>
	</c:forEach>
	</tbody>
</table>
