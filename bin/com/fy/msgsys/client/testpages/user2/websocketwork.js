var imagelal = null;
var websocket = null;
var readyState = new Array("正在连接", "已建立连接", "正在关闭连接"
        , "已关闭连接");
		
var heartbeat_timer = 0;
var last_health = -1;
var health_timeout = 3000;

onmessage = function(e) {
	
	if(e == null) return ;
/*	if(e.data == 'connect'){
		connect();
		return ;
	}else if(e.data == 'disconnect'){
		disconnect();
		return ;
	}
*/
	if(e.data.length == 2){
		if(e.data[0] == 'sendimage2'){
			sendimage2(e.data[1]);
			//return ;
			
		}else if(e.data[0] == 'send'){
			send(e.data[1]);
			//return ;
			
		}
	}else if(e.data.length == 1){
		if(e.data[0] == 'connect'){
			connect();
			//return ;
		}else if(e.data[0] == 'disconnect'){
			disconnect();
			//return ;
		}
	}else if(e.data.length == 3){
		if(e.data[0] == 'login'){
			login();
			//return ;
		}
	}

}

function keepalive( ws ){
	var time = new Date();
	if( last_health != -1 && ( time.getTime() - last_health > health_timeout ) ){
			//此时即可以认为连接断开，可是设置重连或者关闭
			
			postMessage("服务器没有响应." );
			//ws.close();
	}
	else{
		postMessage("连接正常 ." );
		//$("#keeplive_box").html( "连接正常" ).css({"color":"green"});
		if( ws.bufferedAmount == 0 ){
			ws.send( '~H#C~' );
		}
	}
}


function login(u,v,url){
// 
	websocket.send(u+':'+v+':'+url);
	//websocket.send(username + ":" + verify);
}
function connect(){

	clearInterval( heartbeat_timer );
	

    try{
        var host = "ws://localhost:8877";
		
        websocket = new WebSocket(host);
		websocket.binaryType = "arraybuffer";
        websocket.onopen = function(){
        	console.log('connect state:' + websocket.readyState);
			login('user2','verify2','2222');
			heartbeat_timer = setInterval( function(){keepalive(websocket)}, 1000 );
        	postMessage("Socket状态： " + readyState[websocket.readyState] + "");
        }
        websocket.onmessage = function(event){
		

			if( event.data == ( '~H#S~' ) ){
			// 心跳处理
				var time = new Date();			
				last_health = time.getTime();
				return;
			}

			if (event.data == 'ok'){
				console.log("Connected to WebSocket server.");
				postMessage(  "Connected to WebSocket server.");
			}
			else if (event.data == 'ack') {
				postMessage( "can't Connected to WebSocket server.");
				console.log("can't Connected to WebSocket server.");
				websocket.onclose();
			} 
			else if(event.data instanceof ArrayBuffer) {
				console.log('接收到二进制图片');
				postMessage({
					//type:'fyimage11',
					fyimage:'fyimage11',
					data:event.data
				});
			}
			else{
				console.log('receive msg :' + event.data);
				postMessage("接收信息： " + event.data + "");
			} 
        	//console.log('receive msg :' + event.data);
        	//postMessage("接收信息： " + event.data + "");
        }
        websocket.onclose　= function(){
			clearInterval( heartbeat_timer );
        	console.log('Socket状态 :' + websocket.readyState);
        	postMessage("Socket状态： " + readyState[websocket.readyState] + "");
        }
		
		websocket.onerror  = function(){
			clearInterval( heartbeat_timer );
        	console.log('Socket状态（发生异常） :' + websocket.readyState);
        	postMessage("Socket状态： " + readyState[websocket.readyState] + "");
        }
        
        postMessage("Socket状态： " + readyState[websocket.readyState] + "");
    }catch(exception){
		clearInterval( heartbeat_timer );
    	console.log("有错误发生");
    	postMessage(" 有错误发生 ");
    }
    
}
 
 
function send(text){
 
    try{
        websocket.send(text);
       // postMessage(" 发送数据: " + text);
    }catch(exception){
    	postMessage("发送数据出错");
    }
   
    
}
 
function disconnect(){
	websocket.close();
	postMessage("Socket状态： " + readyState[websocket.readyState] + "");
}
  
function sendimage2(imagelal){
	websocket.send(imagelal);
}

