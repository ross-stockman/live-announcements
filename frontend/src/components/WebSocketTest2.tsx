// import { useState } from 'react';
// import SockJsClient from 'react-stomp';
//
// const SOCKET_URL = 'http://localhost:8080/ws'
//
// const WebSocketTest2 = () => {
//     const [message, setMessage] = useState('You server message here.');
//
//     const onConnected = () => {
//         console.log("Connected!!")
//     }
//
//     const onMessageReceived = (msg: string) => {
//         setMessage(msg);
//     }
//
//     return (
//         <div>
//             <SockJsClient
//                 url={SOCKET_URL}
//                 topics={['/topic/message']}
//                 onConnect={onConnected}
//                 onDisconnect={console.log("Disconnected!")}
//                 onMessage={(msg: string) => onMessageReceived(msg)}
//                 debug={false}
//             />
//             <div>{message}</div>
//         </div>
//     );
// }
//
// export default WebSocketTest2