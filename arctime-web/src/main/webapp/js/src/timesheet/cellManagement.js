
// The flag used to keep track of whether the timesheet needs saved.
var needsSaved = { };

// Keep track of any reasons specified for changing values.
var reasons = { };

// Initialize the cell management for all available timesheets.
function initCellManagement() {
	// Iterate over all the available timesheets.
	for (var t = 0; t < timesheetIds.length; t++) {
		// Iterate over all the available days.
		for (var d = 0; d < days.length; d++) {
			// Iterate over all the available contracts.
			for (var c = 0; c < assignmentIds.length; c++) {
				// Build the id for this contract/day.
				var id = 'cell' + timesheetIds[t] + '_' + assignmentIds[c] + '_' + days[d];

				// Get the cell.
				var cell = document.getElementById(id);

				// Make sure the cell was found.
				if (cell) {
					// Add the focus and blur listeners.
					cell.onfocus = cellFocus;
					cell.onblur = cellBlur;
				}
			}
		}
	}
}

// Retrieve all the data contained in this timesheet.
function getCellData(timesheetId) {
	if (typeof(timesheetId) == "undefined")
		alert("timesheet cell management: getCellData needs a timesheet id.");

	// This array will hold all the data.
	var data = [ ];

	// Iterate over all the available days.
	for (var d = 0; d < days.length; d++) {
		// Iterate over all the available contracts.
		for (var c = 0; c < assignmentIds.length; c++) {
			// Build the id for this contract/day.
			var id = 'cell' + timesheetId + '_' + assignmentIds[c] + '_' + days[d];

			// Get the cell.
			var cell = document.getElementById(id);

			var contractId = getContractIdFromCellId(id);
			var assignmentId = getAssignmentIdFromCellId(id);

			// Make sure the cell was found.
			if (cell && !isNaN(parseFloat(cell.value))) {
				// Get the associated reason.
				var reason = reasons[timesheetId + ':' + contractId + ':' + assignmentId + ':' + days[d]];

				// Add this data item to the array.
				data.push(assignmentIds[c] + ':' + days[d] + ':' + cell.value +
						(reason ? ':' + reason : ''));
			}
		}
	}

	// Return the identified cell data all joined together.
	return data.join(';');
}

// Keep track of the previous value in a cell before it was modified.
var previousValue = undefined;

// This function is invoked when a timesheet cell gains the focus.
function cellFocus(event) {
	// Reset the activity timer.
	resetTimer();

	// Get the cell.
	var cell = event ? event.target : this;

	// Save the previous value.
	previousValue = cell.value;
}

// This function is invoked when a timesheet cell loses the focus.
function cellBlur(event) {
	// Reset the activity timer.
	resetTimer();

	// Get the cell.
	var cell = event ? event.target : this;

	// Validate the cell value.
	if (!validateCell(cell))
		return false;

	// Determine what to do based on the value.
	if (cell.value.match(/^\s*$/)) {
		// Blank out zero values.
		cell.value = "";
	} else {
		// Make sure the cell value is to the quarter-hour.
		cell.value = Math.round(cell.value * 20) / 20;

		// Make sure the correct number of decimals is displayed.
		var pieces = ("" + cell.value).split(/\./);
		if (pieces.length == 1)
			cell.value += ".00";
		else if (pieces[1].length == 1)
			cell.value += "0";
	}

	// Update all the totals.
	var timesheetId = getTimesheetIdFromCellId(cell.id);
	updateContractTotal(timesheetId, getContractIdFromCellId(cell.id), getAssignmentIdFromCellId(cell.id));
	updateDayTotal(timesheetId, getDayFromCellId(cell.id));
	updateWeekTotal(timesheetId, getDayFromCellId(cell.id));
	updateTotal(timesheetId);

	// Check to see if the value was modified.
	if (previousValue != cell.value) {
		// Update the cell's class to show that it is dirty.
		cell.style.backgroundImage = 'url(/images/icons/dirty.gif)';
		cell.style.backgroundRepeat = 'no-repeat';
		cell.style.backgroundPosition = 'left top';

		// Update the save flag.
		needsSaved[timesheetId] = true;

		// Get the cell day.
		var cellDay = getDayFromCellId(cell.id);

		// Get today.
		var today = new Date().format('Ymd');

		// Get the contract id for the cell.
		var contractId = getContractIdFromCellId(cell.id);
		// Get the assignment id for the cell.
		var assignmentId = getAssignmentIdFromCellId(cell.id);

		// Do we need the user to provide a reason for changing the value?
		if (previousValue != '' && cellDay != today) {
			// Create the input field.
			var reasonField = new Ext.form.TextField({
				name: 'reason',
				fieldLabel: 'Reason',
				width: 282
			});

			// Create the reason panel.
			var pnl = new Ext.form.FormPanel({
				border: false,
				frame: false,
				labelWidth: 55,
				bodyStyle: 'padding: 10px;',
				items: [
					new Ext.Panel({
						border: false,
						frame: false,
						bodyStyle: 'padding-bottom: 10px;',
						items: new Ext.form.Label({
							text: 'When changing previously entered hours '
								+ 'in your time sheet, you are required to '
								+ 'provide a reason for making the change. '
								+ 'This is a DCAA auditing requirement. '
								+ 'Please provide a reason below.\n'
						})
					}),
					reasonField
				]
			});

			// Show the reason window.
			var win = new Ext.Window({
				title: 'Time Change Reason',
				width: 380, height: 167,
				layout: 'fit',
				modal: true,
				items: pnl,
				closable: false,
				buttons: [
					{
						text: 'Done',
						handler: function() {
							// Get the reason value.
							var rsn = reasonField.getValue();

							// Make sure the reason is valid.
							if (!rsn || rsn.match(/^\s*$/)) {
								// Display the error message.
								reasonField.markInvalid('A reason must be provided.');
							} else {
								// Remove all colons and semi-colons from the reason.
								rsn = rsn.replace(/:/g, ' ');
								rsn = rsn.replace(/;/g, ' ');

								// Save the reason.
								reasons[timesheetId + ':' + contractId + ':' + assignmentId + ':' + cellDay] = rsn;

								// Close the window.
								win.close();
							}
						}
					}
				]
			});
			win.show();
		}
	}
}

// Clear the dirty flag on all the cells.
function clearDirtyFlags(timesheetId) {
	if (typeof(timesheetId) == "undefined")
		alert("timesheet cell management: clearDirtyFlags needs a timesheet id.");

	// Iterate over the contracts and days.
	for (var c = 0; c < assignmentIds.length; c++)
		for (var d = 0; d < days.length; d++) {
			// Define the id of the cell we are going to update.
			var id = 'cell' + timesheetId + '_' + assignmentIds[c] + '_' + days[d];

			// Get the cell to update.
			var cell = document.getElementById(id);

			// Clear the dirty flag.
			if (cell && cell.style.backgroundImage)
				cell.style.backgroundImage = '';
		}

	// Update the save flag.
	needsSaved[timesheetId] = false;
}

// Update the contract total column.
function updateContractTotal(timesheetId, contractId, assignmentId) {
	if (typeof(timesheetId) == "undefined" || typeof(contractId) == "undefined" || typeof(assignmentId) == "undefined")
		alert("timesheet cell management: updateContractTotal needs timesheet, contract, and assignment ids.");

	// This will keep track of the total.
	var total = 0;

	// Iterate over the available days.
	for (var d = 0; d < days.length; d++) {
		// Build the id for this contract/day.
		var id = 'cell' + timesheetId + '_' + contractId + '_' + assignmentId + '_' + days[d];

		// Get the cell.
		var cell = document.getElementById(id);

		// Make sure the cell was found.
		if (cell) {
			// Get the cell value.
			var cellVal = parseFloat(cell.value);

			// Make sure it was a valid float value.
			if (!isNaN(cellVal))
				// Add to the running total.
				total += cellVal;
		}
	}

	// Make sure the correct number of decimals is displayed.
	total = Math.round(total * 20) / 20;
	var pieces = ("" + total).split(/\./);
	if (pieces.length == 1)
		total += ".00";
	else if (pieces[1].length == 1)
		total += "0";

	// Get the contract total element to update.
	var contot = document.getElementById(
			'contot' + timesheetId + '_' + contractId + '_' + assignmentId);

	// Set the new value.
	contot.innerHTML = total;
}

// Update the day total column.
function updateDayTotal(timesheetId, day) {
	if (typeof(timesheetId) == "undefined" || typeof(day) == "undefined")
		alert("timesheet cell management: updateDayTotal needs timesheet id and day.");

	// This will keep track of the total.
	var total = 0;

	// Iterate over the available contracts.
	for (var c = 0; c < assignmentIds.length; c++) {
		// Build the id for this contract/day.
		var id = 'cell' + timesheetId + '_' + assignmentIds[c] + '_' + day;

		// Get the cell.
		var cell = document.getElementById(id);

		// Make sure the cell was found.
		if (cell) {
			// Get the cell value.
			var cellVal = parseFloat(cell.value);

			// Make sure it was a valid float value.
			if (!isNaN(cellVal))
				// Add to the running total.
				total += cellVal;
		}
	}

	// Make sure the correct number of decimals is displayed.
	total = Math.round(total * 20) / 20;
	var pieces = ("" + total).split(/\./);
	if (pieces.length == 1)
		total += ".00";
	else if (pieces[1].length == 1)
		total += "0";

	// Get the day total element to update.
	var daytot = document.getElementById('daytot' + timesheetId + '_' + day);

	// Set the new value.
	daytot.innerHTML = total;
}


// Convert a day string like "20120101" into a Date object.
function dayToDate(day) {
	var date = day.substring(4, 6) + '/' + day.substring(6, 8)
			+ '/' + day.substring(0, 4);

	return new Date(Date.parse(date));
}


// Update the weekly total cell.
function updateWeekTotal(timesheetId, day) {
	if (typeof(timesheetId) == "undefined" || typeof(day) == "undefined")
		alert("timesheet cell management: updateWeekTotal needs timesheet id and day.");

	// Make sure we aren't on a weekend.
	var dayDate = dayToDate(day);
	if (dayDate.getDay() == 0 || dayDate.getDay() == 6)
		return;

	// Iterate over all the available days to find which one changed.
	var idx = -1;
	for (var d = 0; idx < 0 && d < days.length; d++)
		if (day == days[d])
			idx = d;

	// Determine the week beginning and ending days.
	var begin = dayToDate(day);
	var beginIdx = idx;
	var end = dayToDate(day);
	var endIdx = idx;
	while (begin.getDay() > 1 && beginIdx > 0)
		begin = dayToDate(days[--beginIdx]);
	while (end.getDay() < 5 && endIdx < days.length - 1)
		end = dayToDate(days[++endIdx]);

	// Determine the total hours for the week.
	total = 0;
	for (var d = beginIdx; d <= endIdx; d++) {
		val = document.getElementById('daytot' + timesheetId + '_' + days[d]).innerHTML;
		total += parseFloat(val);
	}

	// Make sure the correct number of decimals is displayed.
	total = Math.round(total * 20) / 20;
	var pieces = ("" + total).split(/\./);
	if (pieces.length == 1)
		total += ".00";
	else if (pieces[1].length == 1)
		total += "0";

	// Get the day total element to update.
	var weektot = document.getElementById('weektot' + timesheetId + '_' + days[endIdx]);

	// Set the new value.
	if (weektot)
		weektot.innerHTML = total;
}

// Update the total cell.
function updateTotal(timesheetId) {
	if (typeof(timesheetId) == "undefined")
		alert("timesheet cell management: updateTotal needs a timesheet id.");

	// This will keep track of the total.
	var total = 0;

	// Iterate over the available contracts.
	for (var c = 0; c < assignmentIds.length; c++) {
		// Build the id for this contract total.
		var id = 'contot' + timesheetId + '_' + assignmentIds[c];

		// Get the total element.
		var contot = document.getElementById(id);

		// Make sure the cell was found.
		if (contot) {
			// Get the contract total value.
			var contotVal = parseFloat(contot.innerHTML);

			// Make sure it was a valid float value.
			if (!isNaN(contotVal))
				// Add to the running total.
				total += contotVal;
		}
	}

	// Make sure the correct number of decimals is displayed.
	total = Math.round(total * 20) / 20;
	var pieces = ("" + total).split(/\./);
	if (pieces.length == 1)
		total += ".00";
	else if (pieces[1].length == 1)
		total += "0";

	// Get the total element to update.
	var totalEl = document.getElementById('total' + timesheetId);

	// Set the new value.
	totalEl.innerHTML = total;
}

// Perform validation on a timesheet value.
function validateCell(cell) {
	// Make sure the cell value is numeric.
	if (isNaN(cell.value)) {
		ui.util.ErrorMessage("Invalid Value",
				"You must enter a numeric value.");
		cell.focus();
		cell.value = previousValue;
		return false;
	}

	// Make sure the cell value is numeric.
	if (parseFloat(cell.value) > 24) {
		ui.util.ErrorMessage("Invalid Value",
				"The maximum number of hours for one day is 24.");
		cell.focus();
		cell.value = previousValue;
		return false;
	}

	// Everything seems to look good.
	return true;
}

// Parse the timesheet id from the provided cell id.
function getTimesheetIdFromCellId(cellId) {
	// Cut the 'cell' from the front.
	timesheetId = cellId.substring(4);

	// Find the underscore character.
	var us = timesheetId.indexOf('_');

	// Use only up to the underscore.
	if (us > 0)
		return timesheetId.substring(0, us);

	// Could not find it.
	return undefined;
}

// Parse the contract id from the provided cell id.
function getContractIdFromCellId(cellId) {
	// Find the underscore character.
	var us = cellId.indexOf('_');

	// Make sure we found it.
	if (us < 0)
		return undefined;

	// Start after the underscore.
	contractId = cellId.substring(us + 1);

	// Find the next underscore character.
	var us = contractId.indexOf('_');

	// Use only up to the underscore.
	if (us > 0)
		return contractId.substring(0, us);

	// Could not find it.
	return undefined;
}

// Parse the assignment id from the provided cell id.
function getAssignmentIdFromCellId(cellId) {
	// Find the underscore character.
	var us = cellId.indexOf('_');

	// Make sure we found it.
	if (us < 0)
		return undefined;

	// Find the next underscore character.
	us = cellId.indexOf('_', us + 1);

	// Make sure we found it.
	if (us < 0)
		return undefined;

	// Start after the underscore.
	assignmentId = cellId.substring(us + 1);

	// Find the next underscore character.
	us = assignmentId.indexOf('_');

	// Make sure we found it.
	if (us < 0)
		return assignmentId;

	// Use only up to the underscore.
	return assignmentId.substring(0, us);
}

// Parse the day from the provided cell id.
function getDayFromCellId(cellId) {
	// Find the underscore.
	var us = cellId.indexOf('_');

	// Make sure we found it.
	if (us < 0)
		return undefined;

	// Find the next underscore.
	us = cellId.indexOf('_', us + 1);

	// Make sure we found it.
	if (us < 0)
		return undefined;

	// Find the next underscore.
	us = cellId.indexOf('_', us + 1);

	// Return everything after the underscore.
	if (us > 0)
		return cellId.substring(us + 1);

	// Could not find it.
	return undefined;
}

// Check to see if the timesheet needs to be saved before navigating away from
// the page.
function checkSave() {
	// Check to see if the timesheet data needs to be saved.
	var complain = false;
	for (i in needsSaved)
		if (needsSaved[i])
			complain = true;

	if (!complain)
		return undefined;

	// Hide any open message boxes.
	Ext.Msg.hide();

	// Return an error message.
	return 'Your timesheet contains unsaved information, and leaving this ' +
		   'page without saving the timesheet will cause that information ' +
		   'to be lost.';
}

