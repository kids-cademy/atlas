$package("sync");

sync.ConservationView = function(ownerDoc, node) {
	this.$super(ownerDoc, node);
	this._activeItem = null;
};

sync.ConservationView.prototype = {
	setObject : function(conservation) {
		if (!conservation) {
			this.hide();
			return;
		}
		this.show();

		if (this._activeItem != null) {
			this._activeItem.removeCssClass("active");
		}
		this._activeItem = this.getByCssClass(conservation);
		if (this._activeItem != null) {
			this._activeItem.addCssClass("active");
		}
	},

	toString : function() {
		return "sync.ConservationView";
	}
};
$extends(sync.ConservationView, js.dom.Element);
