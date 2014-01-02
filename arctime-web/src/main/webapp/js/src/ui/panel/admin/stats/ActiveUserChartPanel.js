
Ext.namespace("ui.panel.admin.stats");

ui.panel.admin.stats.ActiveUserChartPanel = Ext.extend(Ext.Panel, {
	constructor: function(c) {
		var panel = this;

		this.chartWidth = 240;
		this.chartHeight = 180;

		var config = Ext.applyIf(c || {}, {
			id:         'ui.panel.admin.stats.activeuserchartpanel',
			title:      'Active Users',
			width:  panel.chartWidth + 20,
			height: panel.chartHeight + 30,
			items: [
				new Ext.Panel({
					html:   '<div id="active-user-chart"></div>',
					border: false,
					width:  panel.chartWidth,
					height: panel.chartHeight,
					listeners: {
						afterrender: function() {
							var margin = {
								top:    20,
								right:  6,
								bottom: 30,
								left:   35
							};

							var width = panel.chartWidth - margin.left - margin.right;
							var height = panel.chartHeight - margin.top - margin.bottom;

							var x = d3.scale.linear().range([0, width]);
							var y = d3.scale.linear().range([height, 0]);

							var xAxis = d3.svg.axis().scale(x).orient("bottom");
							var yAxis = d3.svg.axis().scale(y).orient("left");

							var line = d3.svg.line()
								.x(function(d) { return x(d.index); })
								.y(function(d) { return y(d.count); });

							var svg = d3.select("#active-user-chart").append("svg")
								.attr("width", width + margin.left + margin.right)
								.attr("height", height + margin.top + margin.bottom)
								.append("g")
								.attr("transform", "translate(" + margin.left + "," + margin.top + ")");

							var begin = new Date(new Date().getTime() - 334*24*60*60*1000);
							var end = new Date(new Date().getTime() + 24*60*60*1000);

							var url = "/rest/admin/stats/users"
									+ "?begin=" + Ext.Date.format(begin, 'Y-m') + '-01'
									+ "&end=" + Ext.Date.format(end, 'Y-m-d');

							d3.tsv(url, function(error, data) {
								data.forEach(function(d) {
									d.index = +d.index;
									d.count = +d.count;
								});

								x.domain(d3.extent(data, function(d) { return d.index; }));
								y.domain(d3.extent(data, function(d) { return d.count; }));

								svg.append("g")
									.attr("class", "x axis")
									.attr("transform", "translate(0," + height + ")")
									.call(xAxis);

								svg.append("g")
									.attr("class", "y axis")
									.call(yAxis);

								svg.append("path")
									.datum(data)
									.attr("class", "line")
									.attr("d", line);
							});
						}
					}
				})
			]
		});

		ui.panel.admin.stats.ActiveUserChartPanel.superclass.constructor.call(this, config);
	}
});

