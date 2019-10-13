$package("sync");

sync.HDateFormat = function() {
};

sync.HDateFormat.Format = [ "DATE", "YEAR", "DECADE", "CENTURY", "MILLENNIA", "KYA", "MYA", "BYA" ];

sync.HDateFormat.Period = [ "FULL", "BEGINNING", "MIDDLE", "END" ];

sync.HDateFormat.Era = [ "CE", "BCE" ];

sync.HDateFormat.RomanSymbols = {
	M : 1000,
	CM : 900,
	D : 500,
	CD : 400,
	C : 100,
	XC : 90,
	L : 50,
	XL : 40,
	X : 10,
	IX : 9,
	V : 5,
	IV : 4,
	I : 1
};

sync.HDateFormat.prototype = {
	format : function(hdate) {

		switch (this.getFormat(hdate)) {
		case "YEAR":
			return $format("%d %s", hdate.value, this.getEra(hdate));

		case "CENTURY":
			return $format("%d %s", (hdate.value - 1) * 100, this.getEra(hdate));

		default:
			break;
		}

		return "";

		return "date";

		switch (this.getFormat(hdate)) {
		case "YEAR":
			return $format("%d %s", hdate.value, this.getEra(hdate));

		case "CENTURY":
			const value = this.roman(hdate.value);
			const era = this.getEra(hdate);
			const suffix = this.suffix(hdate.value);

			switch (this.getPeriod(hdate)) {
			case "BEGINNING":
				return $format("Beginning of %d-%s Century, %s", value, suffix, era);

			case "MIDDLE":
				return $format("Middle of %d-%s Century, %s", value, suffix, era);

			case "END":
				return $format("End of %d-%s Century, %s", value, suffix, era);

			default:
				return $format("%d-%s Century, %s", value, suffix, era);
			}
			break;

		case "KYA":
			return $format("%d kilo years ago", hdate.value);

		case "MYA":
			return $format("%d million years ago", hdate.value);

		case "BYA":
			return $format("%d billion years ago", hdate.value);

		default:
			break;
		}

		return "";
	},

	roman : function(number) {
		var symbols = sync.HDateFormat.RomanSymbols;
		var value = '';

		var keys = Object.keys(symbols);
		for (var i = 0; i < keys.length; i++) {
			var q = Math.floor(number / symbols[i]);
			number -= q * symbols[i];
			value += i.repeat(q);
		}

		return value;
	},

	suffix : function(value) {
		switch (value) {
		case 1:
			return "st";

		case 2:
			return "nd";

		case 3:
			return "rd";

		default:
			return "th";
		}
	},

	getFormat : function(hdate) {
		if (!hdate.mask) {
			return null;
		}
		return sync.HDateFormat.Format[hdate.mask & 0x000000FF];
	},

	getEra : function(hdate) {
		if (!hdate.mask) {
			return null;
		}
		return sync.HDateFormat.Era[(hdate.mask & 0x00FF0000) >> 16];
	},

	parse : function(value) {
		return null;
	},

	test : function(value) {
		return !!value;
	},

	toString : function() {
		return "sync.HDateFormat";
	}
};
$extends(sync.HDateFormat, Object);
