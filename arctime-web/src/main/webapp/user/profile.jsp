<!DOCTYPE html>
<%@ page import="com.arcblaze.arctime.model.Employee" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<html>
  <head>
    <%@ include file="/ssi/meta.jspf" %>
  </head>
  <body>
    <%@ include file="/ssi/header.jspf" %>
    <%@ include file="/ssi/scripts.jspf" %>


	<!-- This is where the profile information will be rendered. -->
	<table width="100%">
	  <tr>
		<td valign="top">
		  <div id="profile-update-panel"></div>
		</td>
		<td valign="top" style="padding-left:20px;">
		  <div id="supervisor-grid"></div>
		</td>
	  </tr>
	</table>

	<!-- Add the profile management scripts. -->
	<script src="/js/src/util/io/ServerIO.js"></script>
	<script src="/js/src/data/model/Employee.js"></script>
	<script src="/js/src/data/model/Supervisor.js"></script>
	<script src="/js/src/data/store/SupervisorStore.js"></script>
	<script src="/js/src/action/employee/DoProfileUpdate.js"></script>
	<script src="/js/src/ui/panel/employee/ProfileUpdatePanel.js"></script>
	<script src="/js/src/ui/grid/SupervisorGrid.js"></script>
	<script>
	  // Invoked when the page is ready.
	  Ext.onReady(function() {
		  // Create the employee.
		  <% Employee employee = (Employee) request.getUserPrincipal(); %>
		  var employee = {
			  data: {
				  id: <%= employee.getId() %>,
				  firstName: '<%= StringEscapeUtils.escapeJavaScript(employee.getFirstName()) %>',
				  lastName: '<%= StringEscapeUtils.escapeJavaScript(employee.getLastName()) %>',
				  login: '<%= StringEscapeUtils.escapeJavaScript(employee.getLogin()) %>',
				  email: '<%= StringEscapeUtils.escapeJavaScript(employee.getEmail()) %>',
				  division: '<%= StringEscapeUtils.escapeJavaScript(employee.getDivision()) %>',
				  personnelType: '<%= employee.getPersonnelType().name() %>'
			  }
		  }

		  // Create the panel.
		  new ui.panel.employee.ProfileUpdatePanel({
			  // Specify where the panel will be rendered.
			  renderTo: 'profile-update-panel',

			  // Provide the employee data.
			  employee: employee
		  });

		  // Create the supervisor grid.
		  new ui.grid.SupervisorGrid({
			  // Specify where the grid will be rendered.
			  renderTo: 'supervisor-grid',

			  // Don't include the supervisor toolbar.
			  includeToolbar: false,

			  // Set the height.
			  height: 140,

			  // Provide the employee data.
			  employee: employee
		  });
	  });
	</script>


    <%@ include file="/ssi/footer.jspf" %>
  </body>
</html>
