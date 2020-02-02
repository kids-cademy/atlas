$package("sync");

sync.TextColumn = function(ownerDoc, node) {
	this.$super(ownerDoc, node);

	this._availableHeight = this.getParent().style.getHeight();
};

sync.TextColumn.prototype = {
	setObject : function(atlasObject) {
		this.removeChildren();
		var paragraphMargin = 0
		for (;;) {
			const paragraphElement = atlasObject.paragraphs.getFirstChild();
			if (paragraphElement == null) {
				break;
			}
			if (paragraphMargin === 0) {
				paragraphMargin = paragraphElement.style.getMargin().bottom;
			}
			const paragraphHeight = paragraphElement.style.getHeight();
			const columnHeight = this.style.getHeight();
			if (columnHeight + paragraphHeight + paragraphMargin > this._availableHeight) {
				break;
			}
			this.addChild(paragraphElement);
		}
		this.getParent().show(this.getChildrenCount());
	},

	/**
	 * Class string representation.
	 * 
	 * @return this class string representation.
	 */
	toString : function() {
		return "sync.TextColumn";
	}
};
$extends(sync.TextColumn, js.dom.Element);
