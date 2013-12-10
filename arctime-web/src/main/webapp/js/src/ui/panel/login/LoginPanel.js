
Ext.namespace("ui.panel.login");

ui.panel.login.LoginPanel = Ext.extend(Ext.form.FormPanel, {
	constructor: function(c) {
		var form = this;

		// Use the cookie provider.
		Ext.state.Manager.setProvider(new Ext.state.CookieProvider({
			expires: new Date(new Date().getTime() + (1000*60*60*24*90)) // 90 days
		}));

		// Should we save login info?
		this.saveLogin = ('' + Ext.state.Manager.get('saveLogin')) == 'true';

		this.encode = function(str) {
			var encoded = "";
			for (i = 0; i < str.length; i++) {
				var a = str.charCodeAt(i);
				var b = a ^ 123;
				encoded = encoded + encodeURIComponent(String.fromCharCode(b));
			}
			return "enc{" + encoded + "}";
		};

		this.decode = function(str) {
			if (!str)
				return undefined;

			if (str.match(/enc{.*}/)) {
				str = str.substring(4, str.length -1);
				str = decodeURIComponent(str);
				var decoded = "";
				for (i = 0; i < str.length; i++) {
					var a = str.charCodeAt(i);
					var b = a ^ 123;
					decoded = decoded + String.fromCharCode(b);
				}
				return decoded;
			} else
				return str;
		};

		this.login = new Ext.form.TextField({
			fieldLabel: 'Login or Email',
			name:       'j_username',
			allowBlank: false,
			width:      300,
			value:      Ext.state.Manager.get('loginValue'),
			listeners: {
				specialkey: function(tf, evt) {
					// Listen for the Enter key.
					if (evt.ENTER == evt.getKey()) {
						// Get the form values.
						var vals = form.getForm().getValues();

						// Submit the form if the password has a value.
						if (vals.j_password) {
							// Get the login action.
							var login = Ext.getCmp(
								'action.login.dologin');

							// Invoke the handler.
							if (login)
								login.handler();
						} else
							// Focus on the password field if it doesn't
							// have a value.
							form.getForm().findField('j_password').focus();
					}
				},
				change: function(field, value) {
					if (form.saveLogin)
						Ext.state.Manager.set('loginValue', value);
				}
			}
		});
		this.passwd = new Ext.form.TextField({
			inputType:  'password',
			fieldLabel: 'Password',
			name:       'j_password',
			width:      300,
			value:      form.decode(Ext.state.Manager.get('passwordValue')),
			listeners: {
				specialkey: function(tf, evt) {
					// Listen for the Enter key.
					if (evt.ENTER == evt.getKey()) {
						// Get the form values.
						var vals = form.getForm().getValues();

						// Submit the form if the password has a value.
						if (vals.j_username) {
							// Get the login action.
							var login = Ext.getCmp(
								'action.login.dologin');

							// Invoke the handler.
							if (login)
								login.handler();
						} else
							// Focus on the login field if it doesn't
							// have a value.
							form.getForm().findField('j_username').focus();
					}
				},
				change: function(field, value) {
					if (form.saveLogin)
						Ext.state.Manager.set('passwordValue', form.encode(value));
				}
			}
		});

		this.save = new Ext.form.RadioGroup({
			fieldLabel: 'Save Login Info',
			columns:    2,
			items: [
				{
					xtype:      'radio',
					boxLabel:   'Yes',
					id:         'savelogin-yes',
					name:       'savelogin',
					inputValue: 'true',
					checked:    form.saveLogin
				}, {
					xtype:      'radio',
					boxLabel:   'No',
					id:         'savelogin-no',
					name:       'savelogin',
					inputValue: 'false',
					checked:    !form.saveLogin
				}
			],
			listeners: {
				change: function(group, checked) {
					form.saveLogin = '' + (checked.savelogin == 'true');
					Ext.state.Manager.set('saveLogin', form.saveLogin);

					if (form.saveLogin === 'true') {
						Ext.state.Manager.set('loginValue', form.j_username.getValue());
						Ext.state.Manager.set('passwordValue', form.encode(form.j_password.getValue()));
					} else {
						Ext.state.Manager.set('loginValue', '');
						Ext.state.Manager.set('passwordValue', '');
					}
				}
			}
		});

		var config = Ext.applyIf(c || {}, {
			id:             'ui.panel.login.loginpanel',
			title:          'Login',
			width:          330,
			autoHeight:     true,
			bodyStyle:      'padding: 10px;',
			labelWidth:     140,
			standardSubmit: true,
			items: [
				form.login,
				form.passwd,
				form.save
			],
			buttons: [
				new Ext.Button(new action.login.DoLogin()),
				new Ext.Button(new action.login.ResetPassword())
			]
		});

		ui.panel.login.LoginPanel.superclass.constructor.call(this, config);
	},

	setInitialFocus: function() {
		this.login.focus();
	}
});

