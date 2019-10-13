$package("com.kidscademy.sync");

com.kidscademy.sync.RegionFormat = function () {
};

com.kidscademy.sync.RegionFormat.prototype = {
	templates: {
		ALL: "%s",
		CENTRAL: "Central %s",
		NORTH: "Northern %s",
		NORTH_EAST: "Northeastern %s",
		EAST: "Eastern %s",
		SOUTH_EAST: "Southeastern %s",
		SOUTH: "Southern %s",
		SOUTH_WEST: "Southwestern %s",
		WEST: "Western %s",
		NORTH_WEST: "Northwestern %s"
	},

	format: function (region) {
		const template = this.templates[region.area];
		return $format(template, region.name);
	},

	parse: function (value) {
		return null;
	},

	test: function (value) {
		return !!value;
	},

	toString: function () {
		return "com.kidscademy.sync.RegionFormat";
	}
};
$extends(com.kidscademy.sync.RegionFormat, Object);
