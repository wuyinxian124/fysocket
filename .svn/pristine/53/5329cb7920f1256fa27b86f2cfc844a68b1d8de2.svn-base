//监听内容脚本发送的消息
chrome.runtime.onMessage.addListener(function (request, sender, response) {
    
    switch (request.msg) {
        case 'screenshot':	
			// 开始截屏
			chrome.tabs.captureVisibleTab(null, {
				format : "png",
				quality : 100
			}, function(data) {
				chrome.tabs.query({ active: true }, function (tab) {
					chrome.tabs.sendMessage(tab[0].id, { msg: 'screenshotOK',image11:data, callback: request.callback }, null);
				});

			});		
			break;
		case 'clearCache':
			 
            chrome.tabs.query({ active: true }, function (tab) {
                   chrome.tabs.sendMessage(tab[0].id, { msg: 'clearCacheOK', callback: request.callback }, null);
            });
            break;
    }
});
function screenopt(){

}

