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
            $('#monthlyTradeReportTable .searchable tr').hide();
            $('#monthlyTradeReportTable .searchable tr').filter(function () {
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

		$('#symbolwisetraderesultTable').DataTable({
		        dom: 'Bfrtip',
		        buttons: [
		            'copyHtml5',
		            'excelHtml5',
		            'csvHtml5',
					'pageLength'
		        ],
				"ordering": false,
				lengthMenu: [
           			 [ 10, 25, 50, -1 ],
            		 [ '10 rows', '25 rows', '50 rows', 'Show all' ]
        		],
    	});

		
		$('#ipoHistoricalData').DataTable({
		        dom: 'Bfrtip',
		        buttons: [
		            'copyHtml5',
		            'excelHtml5',
		            'csvHtml5'
		        ],
				"paging":   false,
				"ordering": false,
    	});

});

