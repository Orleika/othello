import java.io.*;
import java.net.*;

public class Server implements Runnable, AutonomousServerSocketInterface{

	public final int		_constBlackPort = 9998;
	public final int		_constWhitePort = 9999;

	private int						_blackPort;
	private int						_whitePort;
	private AutonomousServerSocket	_serverSocketForBlack = null;
	private AutonomousServerSocket	_serverSocketForWhite = null;
	private OthelloSocket			_socketForBlack = null;
	private OthelloSocket			_socketForWhite = null;

	private volatile Thread						_blinker;		/* 自律用スレッド */

	/*
	 *  < main文 >
	 */
	public static void main(String args[]) {
		try{
			Server server = new Server();
			server.start();
		}catch(Exception e){
			System.out.println("Error"+e);
		}
	}


	/*
	 *  < コンストラクタ1 >
	 */
	public Server() throws Exception {
		try{
			_serverSocketForBlack = new AutonomousServerSocket(_constBlackPort,this);
			_serverSocketForWhite = new AutonomousServerSocket(_constWhitePort,this);
		}catch(Exception e){
			throw(e);
		}
	}


	/*
	 *  < コンストラクタ2 >
	 */
	public Server(int blackPort,int whitePort) throws Exception {
		try{
			_serverSocketForBlack = new AutonomousServerSocket(blackPort,this);
			_serverSocketForWhite = new AutonomousServerSocket(whitePort,this);
		}catch(Exception e){
			throw(e);
		}
	}


	/**
	 *  スレッド開始
	 */
	public void start(){
		_blinker = new Thread(this);
		_blinker.start();
		_serverSocketForBlack.start();
		_serverSocketForWhite.start();
	}


	/**
	 *  スレッド終了
	 */
	public void stop(){
		_blinker = null;
	}


	/**
	 *  オセロの運営
	 */
	public void run(){
		Thread thisThread = Thread.currentThread();
		while(_blinker == thisThread){
			/*
			 *  接続フェーズ
			 *
			 *  白の黒が揃うまで待つ
			 */
			System.out.println("Waiting connect .....");
			while(!(_socketForBlack != null && _socketForWhite != null)){
				try{
					Thread.sleep(1000);
				}catch(InterruptedException  e){}
			}

			System.out.println("Game Start");
			/*
			 *  ゲームフェーズ
			 */
			try{
				while(true){
					int signalFromBlack = _socketForBlack.recive();
					_socketForWhite.send(signalFromBlack);
					int blackX = signalFromBlack / 10;
					int blackY = signalFromBlack % 10;
					System.out.println("Black put ("+blackX+","+blackY+")");

					int signalFromWhite = _socketForWhite.recive();
					_socketForBlack.send(signalFromWhite);
					int whiteX = signalFromWhite / 10;
					int whiteY = signalFromWhite % 10;
					System.out.println("White put ("+whiteX+","+whiteY+")");
				}
			}catch(IOException e){
				System.out.println("disconnected .......");
				try{
					_socketForBlack.close();
					_socketForWhite.close();
				}catch(IOException e2) {}
				System.exit(1);
			}
		}
	}


	/**
	 *
	 */
	public synchronized void conect(Socket socket,AutonomousServerSocket server) {
		try{
			if(server == _serverSocketForBlack) {
				if(_socketForBlack == null) {
					System.out.println("Black is conected.");
					System.out.println("   from "+ socket.getInetAddress() );
					_socketForBlack = new OthelloSocket(socket);
					return;
				}
			}else {
				if(_socketForWhite == null) {
					System.out.println("White is conected.");
					System.out.println("   from "+ socket.getInetAddress() );
					_socketForWhite = new OthelloSocket(socket);
					return;
				}
			}
			socket.close();
		}catch(Exception e) {}
	}
}

/*---------------------------------------------------------------
 *  AutonomousServerSocketInterface
 *---------------------------------------------------------------*/
interface AutonomousServerSocketInterface{
	/**
	 *
	 * @param	Socket					client		
	 * @param	AutonomousServerSocket	server		
	 */
	public void conect(Socket client,AutonomousServerSocket server);
}


/*---------------------------------------------------------------
 *  AutonomousServerSocket
 *---------------------------------------------------------------*/
class AutonomousServerSocket extends ServerSocket implements Runnable{

	private AutonomousServerSocketInterface		_interfacer;	/* 受信を通知するオブジェクトへのインタフェース */
	private volatile Thread						_blinker;		/* 自律用スレッド */

	/**
	 *  < コンストラクタ >
	 *  ポート番号とサーバーを指定して、オブジェクトを生成
	 *
	 * @param	int								port		受信を待つポート番号
	 * @param	AutonomousServerSocketInterface	ASSinterface	受信を通知するオブジェクトへのインタフェース
	 */
	public AutonomousServerSocket(int port,AutonomousServerSocketInterface ASSinterface) throws IOException{
		super(port);
		_interfacer = ASSinterface;
	}

	/**
	 *  スレッド開始
	 */
	public void start() {
		_blinker = new Thread(this);
		_blinker.start();
	}

	/**
	 *  スレッド終了
	 */
	public void stop() {
		_blinker = null;
	}

	/**
	 *  接続を待ちつづける
	 */
	public void run() {
		Thread thisThread = Thread.currentThread();
		try{
			while(_blinker == thisThread) {
				Socket client = this.accept();
				_interfacer.conect(client,this);
			}
		}catch(Exception e) {
			System.out.println("Error ReadyConect.run:"+e);
			System.exit(0);
		}
	}
}


/*---------------------------------------------------------------
 *  OthelloSocket
 *---------------------------------------------------------------*/
class OthelloSocket {

	private	Socket				_socket = null;
	private	DataInputStream		_dataInputStream = null;
	private	DataOutputStream	_dataOutputStream = null;

	/**
	 *  < コンストラクタ >
	 *
	 * @param	Socket		socket		対応するソケット
	 */
	 public OthelloSocket(Socket socket) throws IOException {
		try{
			_socket = socket;
			_dataInputStream = new DataInputStream( new BufferedInputStream( _socket.getInputStream() ) );
			_dataOutputStream = new DataOutputStream( new BufferedOutputStream( _socket.getOutputStream() ) );
		}catch(IOException e) {
			throw(e);
		}
	}

	/**
	 *  通信終了
	 */
	public void close() throws IOException {
		if(_dataInputStream != null){
			_dataInputStream.close();
			_dataInputStream = null;
		}
		if(_dataOutputStream != null){
			_dataOutputStream.close();
			_dataOutputStream = null;
		}
		if(_socket != null){
			_socket.close();
			_socket = null;
		}
	}

	/**
	 *  データ送信
	 *
	 * @param	int		message		送信する信号
	 */
	public void send(int message) throws IOException {
		try{
			_dataOutputStream.writeInt(message);
			_dataOutputStream.flush();
		}catch(IOException e) {
			throw(e);
		}
	}

	/**
	 *  データ受信
	 */
	public int recive() throws IOException {
		try{
			return( _dataInputStream.readInt() );
		}catch(Exception e) {
			throw(new IOException() );
		}
	}
}
