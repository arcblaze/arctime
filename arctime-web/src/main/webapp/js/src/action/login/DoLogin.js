
Ext.namespace("action.login");

action.login.DoLogin = function() {
	return new Ext.Action({
		id:      'action.login.dologin',
		text:    'Login',
		iconCls: 'icon-login',
		handler: function() {
			// Get the panel containing the form data.
			var formPanel = Ext.getCmp('ui.panel.login.loginpanel');

			// Make sure the form is valid.
			if (!formPanel.getForm().isValid()) {
				// Display an error message.
				Ext.Msg.alert('Form Incomplete', 'Please enter a valid ' +
					'login (or email) and password.');
				return;
			}

			// Save the login info again.
			var saveLogin = ('' + Ext.state.Manager.get('saveLogin')) == 'true';
			if (saveLogin) {
				var vals = formPanel.getForm().getValues();
				Ext.state.Manager.set('loginValue', vals.j_username);
				Ext.state.Manager.set('passwordValue', vals.j_password);
			}

			// Show the progress bar while the login happens.
			Ext.Msg.progress('Verifying Login',
				'Please wait while your login information is processed...');

			// Create a new ServerIO object.
			var io = new util.io.ServerIO();

			// Submit the form.
			io.doFormRequest(formPanel, {
				// Set the URL.
				url: 'j_security_check'
			});
		}
	});
}

