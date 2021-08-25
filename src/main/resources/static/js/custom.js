$( document ).ready(function() {
  	 
		$('#symbolwisetraderesultTableFilter').keyup(function () {

            var rex = new RegExp($(this).val(), 'i');
            $('#symbolwisetraderesultTable .searchable tr').hide();
            $('#symbolwisetraderesultTable .searchable tr').filter(function () {
                return rex.test($(this).text());
            }).show();

        });
		
		$('#monthlyTableFilter').keyup(function () {

            var rex = new RegExp($(this).val(), 'i');
            $('#monthlyInvestmentTable .searchable tr').hide();
            $('#monthlyInvestmentTable .searchable tr').filter(function () {
                return rex.test($(this).text());
            }).show();

			$('#montlyUnrealisedProfitTable .searchable tr').hide();
            $('#montlyUnrealisedProfitTable .searchable tr').filter(function () {
                return rex.test($(this).text());
            }).show();

			$('#monthlyTradeCountTable .searchable tr').hide();
            $('#monthlyTradeCountTable .searchable tr').filter(function () {
                return rex.test($(this).text());
            }).show();

        });

		$('#yearlyTableFilter').keyup(function () {

            var rex = new RegExp($(this).val(), 'i');
            $('#yearlyReportTable .searchable tr').hide();
            $('#yearlyReportTable .searchable tr').filter(function () {
                return rex.test($(this).text());
            }).show();

        });

		$('#tradeListTableFilter').keyup(function () {

            var rex = new RegExp($(this).val(), 'i');
            $('#tradeListTable .searchable tr').hide();
            $('#tradeListTable .searchable tr').filter(function () {
                return rex.test($(this).text());
            }).show();

        });

		$('#tradeListTable').DataTable({
		        dom: 'Bfrtip',
		        buttons: [
		            'copyHtml5',
		            'excelHtml5',
		            'csvHtml5'
		        ],
				"paging":   true,
				"ordering": false,
    	});

		

});

