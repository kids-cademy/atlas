$package("sync");

$include("com.kidscademy.atlas.sync.Controller");

sync.App = function() {
	this.$super();

	this._coverSection = this.getByCssClass("cover");

	var browserSupported = this._detectBrowserSupport();
	com.kidscademy.atlas.sync.Controller.onPageLoaded(browserSupported, function(release) {
		this._coverSection.setObject(release);
	}, this);
	if (!browserSupported) {
		return;
	}

	this._readerSection = this.getByCssClass("reader");
	this._scrollView = this.getByCssClass("h-scroll");
	this._objectView = this.getByCssClass("object-view");
	this._paragraphsCache = this.getByCss(".paragraphs-cache");

	this._events = new EventSource("/events");
	this._events.addEventListener("PageLoadEvent", this._onPageLoadEvent.bind(this));
	this._events.addEventListener("PageScrollEvent", this._onPageScrollEvent.bind(this));
	this._events.addEventListener("ItemRevealEvent", this._onItemRevealEvent.bind(this));
	this._events.addEventListener("DisconnectEvent", this._onDisconnectEvent.bind(this));
	this._events.addEventListener("KeepAliveEvent", this._onKeepAliveEvent.bind(this));

	/**
	 * FIFO queue for scroll events arrived from mobile device via events stream. Because of process scheduler from
	 * mobile scroll events tend to arrive in bursts (group of events) with about 50 msec period. If inject these events
	 * directly to user interface, scroll is not smooth but jumping with small steps.
	 * 
	 * To compensate this bursts we use a FIFO queue to store events from mobile device and read the queue at browser
	 * graphics system speed.
	 */
	this._scrollEventsQueue = new Array();

	this._FRAME_REQUEST_CALLBACK = this._scroll.bind(this);
	this._scroll();
};

sync.App.prototype = {
	setAtlasObject : function(atlasObject) {
	},

	_onPageLoadEvent : function(event) {
		var data = js.lang.JSON.parse(event.data);
		this.setObject(data.atlasObject);
	},

	setObject : function(atlasObject) {
		this._paragraphsCache.setHTML(atlasObject.description);
		atlasObject.paragraphs = this._paragraphsCache;

		this._coverSection.hide();
		this._readerSection.show();

		// reset scroll events queue and move page at beginning when load a new object
		this._scrollEventsQueue.length = 0;
		this._scrollView._node.scrollLeft = 0;

		this._readerSection.setObject(atlasObject);
		// object view should be visible when measure scrollable width
		this._scrollableWidth = parseInt(this._objectView._node.scrollWidth) - WinMain.getWidth();
	},

	_onPageScrollEvent : function(event) {
		var data = JSON.parse(event.data);
		this._scrollEventsQueue.push(data.scrollPercent * this._scrollableWidth);
	},

	_onItemRevealEvent : function(event) {
		var data = JSON.parse(event.data);
		var listView;

		switch (data.type) {
		case "DESCRIPTION":
			listView = this.getByCss(".section.description .list");
			break;

		case "FACT":
			listView = this.getByCss(".section.facts .list");
			break;

		case "FEATURE":
			listView = this.getByCss(".section.features");
			break;

		default:
			$error("sync.App#_onItemRevealEvent", "Not handled item type |%s|.", data.type);
			return;
		}

		var itemView = listView.getByIndex(data.index);
		itemView.scrollIntoView();
	},

	_onDisconnectEvent : function(event) {
		$debug("sync.App#_onDisconnectEvent", "Close server sent events stream.");
		this._events.close();
		this._coverSection.show();
		this._readerSection.hide();
	},

	_onKeepAliveEvent : function(event) {
		$debug("sync.App#_onKeepAliveEvent", "Keep alive event.");
	},

	_scroll : function() {
		var scrollLeft = this._scrollEventsQueue.shift();
		if (typeof scrollLeft !== "undefined") {
			this._scrollView._node.scrollLeft = scrollLeft;
		}

		// apparent not optimal solution: keep requesting animation frame even if no scroll events from mobile device
		// it is 'apparent' because on tests there is no performance penalty
		// probably browsers are smart enough to deal with this condition

		requestAnimationFrame(this._FRAME_REQUEST_CALLBACK);
	},

	_detectBrowserSupport : function() {
		if (typeof EventSource === "undefined") {
			return false;
		}

		return true;
	},

	toString : function() {
		return "sync.App";
	}
};
$extends(sync.App, js.ua.Page);
