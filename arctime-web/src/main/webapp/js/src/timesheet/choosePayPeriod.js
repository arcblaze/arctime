
// Invoked when the user wants to choose a new timesheet pay period.
function choosePayPeriod() {
	// Create and display the date picker.
	new Ext.DatePicker({
		// Where to draw the picker.
		renderTo: 'date-picker',

		// Invoked after a date is chosen.
		handler: function(picker, chosenDate) {
			// Hide the date picker.
			picker.hide();

			// Show the progress bar while the timesheet is loaded.
			Ext.Msg.progress('Loading Timesheet',
				'Please wait while the specified timesheet is loaded...');

			// Go show the chosen pay period.
			document.location = '/user/timesheet/view/' +
				chosenDate.format('Y-m-d');
		}
	}).show();
}

