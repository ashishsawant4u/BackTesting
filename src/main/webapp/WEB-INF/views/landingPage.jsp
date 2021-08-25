<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>      
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>    
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>BacktestTrades</title>
</head>
<body>
<tags:scripts/>

<div class="container">
<h1>Back Testing</h1>

<div class="card bg-warning">
  <div class="card-body">
  		Simple Moving Average : <span class="badge rounded-pill bg-dark">${simpleMovingAvg}</span>
		Rising Scale : <span class="badge rounded-pill bg-dark">${risingScale}</span>
		Total Trades Found : <span class="badge rounded-pill bg-dark">${totalNoOfTrades}</span>
		Target Exit Count : <span class="badge rounded-pill bg-dark">${totalTargetExitCount}</span>
		Stop Loss Count :  <span class="badge rounded-pill bg-dark">${totalStopLossCount}</span>
		Profitable Trades > 9 For Symbol : <span class="badge rounded-pill bg-dark">${profitabelTradesMoreThan9}</span>
  </div>
</div>




<ul class="nav nav-tabs mt-1 mb-2" id="myTab" role="tablist">
  <li class="nav-item" role="presentation">
    <button class="nav-link active" id="symbolTradeSummary-tab" data-bs-toggle="tab" data-bs-target="#symbolTradeSummary" type="button" role="tab" aria-controls="home" aria-selected="true">Symbol Trade Summary</button>
  </li>
  <li class="nav-item" role="presentation">
    <button class="nav-link" id="monthlyReport-tab" data-bs-toggle="tab" data-bs-target="#monthlyReport" type="button" role="tab" aria-controls="profile" aria-selected="false">Monthly Report</button>
  </li>
  <li class="nav-item" role="presentation">
    <button class="nav-link" id="yearlyReport-tab" data-bs-toggle="tab" data-bs-target="#yearlyReport" type="button" role="tab" aria-controls="contact" aria-selected="false">Yearly Report</button>
  </li>
  <li class="nav-item" role="presentation">
    <button class="nav-link" id="tradesList-tab" data-bs-toggle="tab" data-bs-target="#tradesList" type="button" role="tab" aria-controls="contact" aria-selected="false">Trades</button>
  </li>
</ul>
<div class="tab-content" id="myTabContent">
  <div class="tab-pane fade show active" id="symbolTradeSummary" role="tabpanel" aria-labelledby="home-tab"><tags:symbolTradeResults/></div>
  <div class="tab-pane fade" id="monthlyReport" role="tabpanel" aria-labelledby="profile-tab"><tags:monthlyReport/></div>
  <div class="tab-pane fade" id="yearlyReport" role="tabpanel" aria-labelledby="contact-tab"><tags:yearlyReport/></div>
  <div class="tab-pane fade" id="tradesList" role="tabpanel" aria-labelledby="contact-tab"><tags:tradeList/></div>
</div>


</div>

</body>
</html>