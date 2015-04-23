//监听bg的消息
chrome.runtime.onMessage.addListener(function (request, sender, response) {
	console.log("内容脚本接收到gb传来的消息");
    switch (request.msg) {
        case 'screenshotOK':
			console.log('进入 screenshotOK');
            window.postMessage({ from: 'cs', image1: request.image11 ,msg:'screenshotOK', callBack: 'screenshotOKCallBack' }, '*')
            break;
		case 'clearCacheOK':
            window.postMessage({ from: 'cs', msg: 'clearCacheOK', callBack: 'clearCacheCallBack' }, '*')
            break;
    }
});

//监听页面发送的消息
window.addEventListener("message", function (event) {
	console.log('进入插件content ');
    if (event.source != window){
        return;
    }

    if (event.data.from == 'page') {
        console.log("内容脚本接收到页面传来的消息：" + event.data.msg);
        switch (event.data.msg) {
            case 'screenshot':
                chrome.runtime.sendMessage({ msg: 'screenshot' }); break;
			case 'clearCache':
				chrome.runtime.sendMessage({ msg: 'clearCache' });	break;		
        }
    }

}, false);
