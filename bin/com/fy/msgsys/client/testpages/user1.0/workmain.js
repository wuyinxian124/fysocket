var imagelal = null;
//var msg = document.getElementById("msg");
if (!!window.Worker) {
	var myWorker = new Worker("websocketwork.js");
	
	myWorker.onmessage = function(e) {
		//result.textContent = e.data;
		var msg = document.getElementById("msg");
		console.log('Message received from worker :' + e.data );
		if(e.data.data == undefined){
			msg.innerHTML += "<p>" + e.data + "</p>";
		}else{
			if(e.data.data instanceof ArrayBuffer ){
				console.log('图片图片图片图片');
				//drawImage(e.data.data);
				//drawImageBinary(e.data.data);
				//drawimage11(e.data.data);
				drawimage112(e.data.data);
			}
		}

		
	}
	
	
	function connect(){
		myWorker.postMessage(['connect']);
	}
	
	function  login1(){
		// 登录
		console.log('点击登录');
		var u='user1';
		var v = 'verify1';
		myWorker.postMessage(['login',u,v]);
	
	}
	function send(){
		var msg = document.getElementById("msg");
		var text = 'chatroom1##0##'+ document.getElementById("text").value;
		if(text == ""){
			msg.innerHTML += "<p>请输入一些文字</p>";
			return;
		}
		
		try{
			//websocket.send(text);
			myWorker.postMessage(['send',text]);
			msg.innerHTML += "<p>发送数据:  " + text + "</p>";
		}catch(exception){
			msg.innerHTML += "<p>发送数据出错</p>";
		}
		document.getElementById("text").value = "";
		
	}
	 
	function sendimage2(){
		//websocket.send(imagelal);
		myWorker.postMessage(['sendimage2',imagelal]);
	}

	function disconnect(){
		myWorker.postMessage(['disconnect']);
	}
}

window.onload = function(){
	 var input = document.getElementById("demo_input");

	 var img_area = document.getElementById("img_area");
	 if ( typeof(FileReader) === 'undefined' ){
			 msg.innerHTML = "抱歉，你的浏览器不支持 FileReader，请使用现代浏览器操作！";
			 input.setAttribute( 'disabled','disabled' );
	 } else {
			 input.addEventListener( 'change',readFile,false );}
}

function drawImageBinary(blob) {

	var imageWidth = 73, imageHeight = 73; // hardcoded width & height. 
	//var byteArray = new Uint8Array(data);

	var canvas = document.createElement('canvas');
	canvas.width = imageWidth;
	canvas.height = imageHeight;
	var ctx = canvas.getContext('2d');
	var bytes = new Uint8Array(blob);
	//alert('received '+ bytes.length);
	var imageData = ctx.createImageData(canvas.width, canvas.height);

	for (var i=8; i<imageData.data.length; i++) {
		imageData.data[i] = bytes[i];
	}
	context.putImageData(imageData, 0, 0);

	var img = document.createElement('img');
	img.height = canvas.height;
	img.width = canvas.width;
	img.src = canvas.toDataURL();
	
	var div = document.getElementById('img11');
	div.appendChild(image);
}	
// 回显图片
function drawimage11(data){
  
    var bytes = new Uint8Array(data);

    var image = document.getElementById('image1');
    image.src = "data:image/png;base64," + encode(bytes); //window.btoa(str);

}
// 从流中截取互动室视图ID，回显图片
function drawimage112(data){
  
  
	//var dataview = new DataView(data);
	var positonb = 0;
	var timeb = 0;
	var bytes = new Uint8Array(data);//var bytes =  new Uint8Array(data.byteLength);
	for (var i = 0; i < bytes.length; i++) {
		
		if(bytes[i] == 35){
			timeb= timeb +1;
			
			if(timeb == 2){
				positonb = i;
				break;
			}
			continue;
			
		}
		timeb = 0;
	}
    var header = new Uint8Array(data,0,positonb+1);
	console.log('获取图片消息头：' + bin2String(header));
	var msg = document.getElementById("msg");
	msg.innerHTML += "<p>" + '获取图片消息头：' + bin2String(header) + "</p>";
	
	
	var contents = new Uint8Array(data,positonb+1,data.byteLength-positonb-1);

    var image = document.getElementById('image1');
    image.src = "data:image/png;base64," + encode(contents); //window.btoa(str);

}

function bin2String(array) {
  return String.fromCharCode.apply(String, array);
}
	
// public method for encoding an Uint8Array to base64
function encode (input) {
    var keyStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
    var output = "";
    var chr1, chr2, chr3, enc1, enc2, enc3, enc4;
    var i = 0;

    while (i < input.length) {
        chr1 = input[i++];
        chr2 = i < input.length ? input[i++] : Number.NaN; // Not sure if the index 
        chr3 = i < input.length ? input[i++] : Number.NaN; // checks are needed here

        enc1 = chr1 >> 2;
        enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
        enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
        enc4 = chr3 & 63;

        if (isNaN(chr2)) {
            enc3 = enc4 = 64;
        } else if (isNaN(chr3)) {
            enc4 = 64;
        }
        output += keyStr.charAt(enc1) + keyStr.charAt(enc2) +
                  keyStr.charAt(enc3) + keyStr.charAt(enc4);
    }
    return output;
}
	
function drawImage(data) {
	console.log('接收图片长度==='+data.length);
	var imageWidth = 73, imageHeight = 73; // hardcoded width & height. 
	var byteArray = new Uint8Array(data);

	var canvas = document.createElement('canvas');
	canvas.width = imageWidth;
	canvas.height = imageHeight;
	var ctx = canvas.getContext('2d');

	var imageData = ctx.getImageData(0, 0, imageWidth, imageHeight); // total size: imageWidth * imageHeight * 4; color format BGRA
	var dataLen = imageData.data.length;
	for (var i = 0; i < dataLen; i++)
	{
		imageData.data[i] = byteArray[i];
	}
	ctx.putImageData(imageData, 0, 0);

	// create a new element and add it to div
	var image = document.createElement('img');
	image.width = imageWidth;
	image.height = imageHeight;
	image.src = canvas.toDataURL();

	var div = document.getElementById('img11');
	div.appendChild(image);
}
function readFile(){
	
	var file = this.files[0];
	//这里我们判断下类型如果不是图片就返回 去掉就可以上传任意文件 
	if(!/image\/\w+/.test(file.type)){ 
			alert("请确保文件为图像类型");
			return false;
	}
	var reader = new FileReader();
	reader.readAsArrayBuffer(file);
	//reader.readAsDataURL(file);
	reader.onload = function(e){
			imagelal = reader.result;
			msg.innerHTML += '<p>上传文件 + +' + file.name +'</p>';
			//image = file;
			//img_area.innerHTML = '<div class="sitetip">图片img标签展示：</div><img src="'+this.result+'" alt=""/>';
	}
	//reader.readAsArrayBuffer(blob);
	
}

window.addEventListener("message", function (event) {
	if (event.source != window)
		return;
	if (event.data.from == 'cs') {
		console.log('页面接收到内容脚本信息：' + event.data.msg);
		switch (event.data.msg) {
			case 'screenshotOK':	
				document.getElementById('sendimag1e').disabled = false;			
				document.getElementById('msg').innerHTML += '<p>截屏成功 <p>';
				document.getElementById("img_area").innerHTML = '<div class="sitetip">图片img标签展示：</div><img src="'+event.data.image1+'" alt=""/>';
				//imagelal = event.data.image1;
				//var reader = new FileReader();
				//reader.readAsArrayBuffer(event.data.image1);
				//imagelal = new Image(event.data.image1);//reader.result;
				//imagelal = dataURItoBlob(event.data.image1);
				console.log('截取图片长度'+event.data.image1.length);
				imagelal = dataURItoBlob1('chatroom1##0###',event.data.image1);
//				var binary_str = atob(event.data.image1); //转为原始编码
//				var array = new Uint8Array(new ArrayBuffer(binary_str.length));
//				for ( var i = 0;  i < binary_str.length; i++) {
//					array[i] = bb.charCodeAt(i);
//				}
//				imagelal = new DataView(array.buffer);
				
				break;
			case 'clearCacheOK':
                document.getElementById('btn').disabled = false;
                document.getElementById('msg').innerHTML += '<p>缓存清除成功<p>';
                break;
                    
			default:
		}
	}
});

function screenshot(){
	//console.log(window);
	console.log(' 发送截屏 命令给插件 ');
	document.getElementById('sendimag1e').disabled = true;
    document.getElementById('msg').innerHTML += '<p>截屏中。。。<p>';
	window.postMessage({ from: 'page', msg: 'screenshot'}, '*');
}

function dataURItoBlob1(header,dataURI) {
    // convert base64/URLEncoded data component to raw binary data held in a string
    var byteString;
    if (dataURI.split(',')[0].indexOf('base64') >= 0)
        byteString = atob(dataURI.split(',')[1]);
    else
        byteString = unescape(dataURI.split(',')[1]);

    // separate out the mime component
    var mimeString = dataURI.split(',')[0].split(':')[1].split(';')[0];
	var byteArrays = [];
    // write the bytes of the string to a typed array
    var ia = new Uint8Array(byteString.length);
    for (var i = 0; i < byteString.length; i++) {
        ia[i] = byteString.charCodeAt(i);
    }
	
	var byteArray = new Uint8Array(ia);
	byteArrays.push(header);
	byteArrays.push(byteArray);
	var blob = new Blob(byteArrays, {type: mimeString});
    //return new Blob([ia], {type:mimeString});
	return blob;
}

function login(){
	// 登录
	console.log('点击登录');
}

function sendcommand1(){
	console.log(window);
	//console.log(' 发送截屏 命令给插件 ');
	//window.postMessage({ from: 'page', msg: 'screenshot'}, '*');
	var ev = document.createEvent('HTMLEvents'); 
	ev.initEvent('EventName', false, false);
	document.dispatchEvent(ev);	
}
function test() {
	document.getElementById('btn').disabled = true;
	document.getElementById('msg').innerHTML += '<p>缓存清除中。。。<p>';
	console.log('页面发送消息：clearCache');
	window.postMessage({ from: 'page', msg: 'clearCache'}, '*');
}

function base64DecToArr (sBase64, nBlocksSize) {

  var
    sB64Enc = sBase64.replace(/[^A-Za-z0-9\+\/]/g, ""), nInLen = sB64Enc.length,
    nOutLen = nBlocksSize ? Math.ceil((nInLen * 3 + 1 >> 2) / nBlocksSize) * nBlocksSize : nInLen * 3 + 1 >> 2, taBytes = new Uint8Array(nOutLen);

  for (var nMod3, nMod4, nUint24 = 0, nOutIdx = 0, nInIdx = 0; nInIdx < nInLen; nInIdx++) {
    nMod4 = nInIdx & 3;
    nUint24 |= b64ToUint6(sB64Enc.charCodeAt(nInIdx)) << 18 - 6 * nMod4;
    if (nMod4 === 3 || nInLen - nInIdx === 1) {
      for (nMod3 = 0; nMod3 < 3 && nOutIdx < nOutLen; nMod3++, nOutIdx++) {
        taBytes[nOutIdx] = nUint24 >>> (16 >>> nMod3 & 24) & 255;
      }
      nUint24 = 0;

    }
  }

  return taBytes;
}

function b64ToUint6 (nChr) {

  return nChr > 64 && nChr < 91 ?
      nChr - 65
    : nChr > 96 && nChr < 123 ?
      nChr - 71
    : nChr > 47 && nChr < 58 ?
      nChr + 4
    : nChr === 43 ?
      62
    : nChr === 47 ?
      63
    :
      0;

}

function dataURItoBlob(dataURI) {
    // convert base64/URLEncoded data component to raw binary data held in a string
    var byteString;
    if (dataURI.split(',')[0].indexOf('base64') >= 0)
        byteString = atob(dataURI.split(',')[1]);
    else
        byteString = unescape(dataURI.split(',')[1]);

    // separate out the mime component
    var mimeString = dataURI.split(',')[0].split(':')[1].split(';')[0];

    // write the bytes of the string to a typed array
    var ia = new Uint8Array(byteString.length);
    for (var i = 0; i < byteString.length; i++) {
        ia[i] = byteString.charCodeAt(i);
    }

    return new Blob([ia], {type:mimeString});
}

function dataURItoBuffer(dataURI) {
    // convert base64/URLEncoded data component to raw binary data held in a string
    var byteString;
    if (dataURI.split(',')[0].indexOf('base64') >= 0)
        byteString = atob(dataURI.split(',')[1]);
    else
        byteString = unescape(dataURI.split(',')[1]);

    // separate out the mime component
    var mimeString = dataURI.split(',')[0].split(':')[1].split(';')[0];

    // write the bytes of the string to a typed array
    var ia = new Uint8Array(byteString.length);
    for (var i = 0; i < byteString.length; i++) {
        ia[i] = byteString.charCodeAt(i);
    }

    return ia;
}