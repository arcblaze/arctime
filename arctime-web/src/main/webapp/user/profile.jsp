<!DOCTYPE html>
<%@ page import="com.arcblaze.arctime.model.User" %>
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
	<script src="/js/src/data/model/User.js"></script>
	<script src="/js/src/data/model/Supervisor.js"></script>
	<script src="/js/src/data/store/SupervisorStore.js"></script>
	<script src="/js/src/action/user/DoProfileUpdate.js"></script>
	<script src="/js/src/ui/panel/user/ProfileUpdatePanel.js"></script>
	<script src="/js/src/ui/grid/SupervisorGrid.js"></script>
	<script>
	  // Invoked when the page is ready.
	  Ext.onReady(function() {
		  // Create the user.
		  <% User user = (User) request.getUserPrincipal(); %>
		  var user = {
			  data: {
				  id: <%= user.getId() %>,
				  firstName: '<%= StringEscapeUtils.escapeJavaScript(user.getFirstName()) %>',
				  lastName: '<%= StringEscapeUtils.escapeJavaScript(user.getLastName()) %>',
				  login: '<%= StringEscapeUtils.escapeJavaScript(user.getLogin()) %>',
				  email: '<%= StringEscapeUtils.escapeJavaScript(user.getEmail()) %>'
			  }
		  }

		  // Create the panel.
		  new ui.panel.user.ProfileUpdatePanel({
			  // Specify where the panel will be rendered.
			  renderTo: 'profile-update-panel',

			  // Provide the user data.
			  user: user
		  });

		  // Create the supervisor grid.
		  new ui.grid.SupervisorGrid({
			  // Specify where the grid will be rendered.
			  renderTo: 'supervisor-grid',

			  // Don't include the supervisor toolbar.
			  includeToolbar: false,

			  // Set the height.
			  height: 140,

			  // Provide the user data.
			  user: user
		  });
	  });
	</script>


    <%@ include file="/ssi/footer.jspf" %>
  </body>
</html>
