
Ext.namespace("action.timesheet");

action.timesheet.DoComplete = function(timesheetId) {
	return new Ext.Action({
		id:      'action.timesheet.docomplete',
		text:    'Complete Timesheet',
		iconCls: 'icon-timesheet-complete',
		handler: function() {
			// Reset the inactivity timer.
			resetTimer();

			// Confirm with the user if they really want to do it.
			Ext.Msg.confirm('Complete Timesheet',
				'Are you sure you want to close out this timesheet?',
				function(answer) {
					// Make sure the user wants to go on.
					if (answer != 'yes')
						return;

					// Show the progress bar while the completion happens.
					Ext.Msg.progress('Completing Timesheet',
						'Please wait while your timesheet is completed...');

					// Create a new ServerIO object.
					var io = new util.io.ServerIO();

					// Submit the form.
					io.doAjaxRequest({
						// Set the URL.
						url: '/user/timesheet/complete',

						// Set the request parameters.
						params: {
							// Add the current timesheet id.
							id: timesheetId,

							// Add the timesheet data.
							data: getCellData(timesheetId)
						},

						// Invoked on a successful completion.
						mysuccess: function(data) {
							// Clear all the dirty flags on the timesheet.
							clearDirtyFlags(timesheetId);

							// Go to the next pay period.
							document.location = '/user/timesheet/next/' +
								data.payPeriod;
						}
					});
				});
		}
	});
}

