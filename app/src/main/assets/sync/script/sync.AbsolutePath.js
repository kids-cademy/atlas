$package("sync");

sync.AbsolutePath = function() {
};

sync.AbsolutePath.prototype = {
	format : function(src) {
		return "/" + src;
	},

	parse : function(value) {
		return null;
	},

	test : function(value) {
		return !!value;
	},

	toString : function() {
		return "sync.AbsolutePath";
	}
};
$extends(sync.AbsolutePath, Object);
