$package("com.kidscademy.atlas.sync");

/**
 * Controller.
 */
com.kidscademy.atlas.sync.Controller = {
	/**
	 * On page loaded.
	 *
	 * @param boolean browserSupported,
	 * @param Function callback function to invoke on RMI completion,
	 * @param Object scope optional callback run-time scope, default to global scope.
	 * @return com.kidscademy.atlas.sync.Release
	 * @assert callback is a {@link Function} and scope is an {@link Object}.
	 */
	 onPageLoaded: function(browserSupported) {
		$assert(typeof browserSupported !== "undefined", "com.kidscademy.atlas.sync.Controller#onPageLoaded", "Browser supported argument is undefined.");
		$assert(js.lang.Types.isBoolean(browserSupported), "com.kidscademy.atlas.sync.Controller#onPageLoaded", "Browser supported argument is not a boolean.");

		var __callback__ = arguments[1];
		$assert(js.lang.Types.isFunction(__callback__), "com.kidscademy.atlas.sync.Controller#onPageLoaded", "Callback is not a function.");
		var __scope__ = arguments[2];
		$assert(typeof __scope__ === "undefined" || js.lang.Types.isObject(__scope__), "com.kidscademy.atlas.sync.Controller#onPageLoaded", "Scope is not an object.");
		if(!js.lang.Types.isObject(__scope__)) {
			__scope__ = window;
		}

		var rmi = new js.net.RMI();
		rmi.setMethod("com.kidscademy.atlas.sync.Controller", "onPageLoaded");
		rmi.setParameters(browserSupported);
		rmi.exec(__callback__, __scope__);
	}
};

if(typeof Controller === 'undefined') {
	Controller = com.kidscademy.atlas.sync.Controller;
}
