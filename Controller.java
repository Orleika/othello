import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;

/*---------------------------------------------------------------
 *  Controller
 *---------------------------------------------------------------*/
public class Controller 
						implements MouseListener,Runnable{

	private static final int		WIDTH=630;
	private static final int		HEIGHT=450;

	private		Socket				_socket = null;
	private		DataInputStream		_dataInputStream = null;
	private		DataOutputStream	_dataOutputStream = null;

	private		Board				_board = null;
	private		Monitor				_monitor = null;

	private		String				_color;				/* Controllerオブジェクトが担当する色 */
	private		Piece				_myPiece;			/* Controllerオブジェクトが担当する色のピース */
	private		Piece				_enemyPiece;		/* 相手が担当する色のピース */

	private volatile Thread			_blinker = null;	/* Controllerオブジェクトのスレッド */

	private		boolean				_putFlag = false;
	private		Coordinates			_coordinates = null;

	private		int					_myPass;					/* 自分のパスの回数 */
	private		int					_enemyPass;					/* 相手のパスの回数 */

	/**
	 *  main文
	 */
	public static void main(String args[]){
		try{
			Controller controller = new Controller(InetAddress.getByName(args[0]),args[1]);
			controller.start();
		}catch(UnknownHostException e){
			System.out.println("IP Error");
			System.out.println("Can't conect to "+args[0]+" :"+args[1]);
			System.exit(0);
		}catch(Exception e){
			System.out.println("Usage  : java Controller \"IPアドレス\"or\"ホスト名\" \"石の色\"\n");
			System.out.println("Sample : java Controller trombone white");
			System.exit(0);
		}
	}

	/**
	 *  <コンストラクタ>
	 *
	 * @param	InetAddress		piserverAddressece		サーバのInetAddress
	 * @param	int				port					接続するポート
	 * @param	String			color					クライアントの担当する色
	 */
	public Controller(InetAddress serverAddress,String color) throws Exception{
		_color = color.toLowerCase();
		if(_color.compareTo("white") != 0 && _color.compareTo("black") != 0){
			throw(new Exception());
		}
		int port;
		if(_color.compareTo("white") == 0){
			_myPiece = new Piece(Piece.WHITE);
			_enemyPiece = new Piece(Piece.BLACK);
			port = 9999;
		}else{
			_myPiece = new Piece(Piece.BLACK);
			_enemyPiece = new Piece(Piece.WHITE);
			port = 9998;
		}

		/* サーバに接続 */
		try{
			_socket = new Socket(serverAddress,port);
			_dataInputStream = new DataInputStream( _socket.getInputStream() );
			_dataOutputStream = new DataOutputStream( _socket.getOutputStream() );
		}catch(Exception e){
			throw(new UnknownHostException());
		}

		/* ゲーム盤の用意 */
		_board = new Board();
		_board.init();

		/* モニタの用意 */
		_monitor = new Monitor(_board);
		_monitor.addMouseListener(this);
		_monitor.start();

		mouseDisable();
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
		}catch(IOException e){
			throw(e);
		}
	}

	/**
	 *  データ受信
	 */
	public int recive() throws IOException {
		try{
			return( _dataInputStream.readInt() );
		}catch(Exception e){
			throw(new IOException() );
		}
	}

	/**
	 *  スレッド起動
	 */
	public void start(){
		if(_blinker == null){
			_blinker = new Thread(this);
			_blinker.start();
		}
	}

	/**
	 *  スレッド停止
	 */
	public void stop() {
		_blinker = null;
	}

	/**
	 *  主処理
	 */
	public void run(){
		Thread thisThread = Thread.currentThread();
		int x,y;

		try{
			/*
			 *  自分の色が白ならば、相手が打つのを待つ
			 */
			if(_color.compareTo("white") == 0){
				enemyTurn();
			}

			/*
			 *  自分と相手の思考を交互に処理
			 */
			while(_blinker == thisThread){
				myTurn();
				enemyTurn();
				if(_board.checkEND() == true){
					int enemyPieces = _board.getPoints(_enemyPiece);
					int myPieces = _board.getPoints(_myPiece);
					if(enemyPieces > myPieces){
						_monitor.setMessage("負けました");
						_monitor.lock();
					}else if(enemyPieces < myPieces){
						_monitor.setMessage("勝ちました");
						_monitor.lock();
					}else{
						_monitor.setMessage("引き分けました");
						_monitor.lock();
					}
				}
			}
		}catch(IOException e){
			System.out.println("Warning !!");
			System.out.println("  Network session is disconected !");
		}catch(Exception e){
			e.printStackTrace();
		}
	}


	/**
	 *  自分の順番
	 */
	private void myTurn() throws IOException{
		if(_board.checkPass(_myPiece) == true){
			/*
			 *  パス
			 */
			_myPass++;
			if(_myPass == 3){
				_monitor.setMessage("負けました");
				_monitor.lock();
			}else{
				_monitor.setMessage("Pass !!");
				send(88);
			}
		}else{
			_monitor.setMessage("Your Turn !");
			mouseEnable();
			while( getCoordinates() == null ){
				try{
					Thread.sleep(100);
				}catch(InterruptedException  e){}
			}
			Coordinates coordinates = getCoordinates();
			_board.putPiece(coordinates.x,coordinates.y,new Piece(_myPiece));
			int sendSignal = 10 * coordinates.x + coordinates.y;
			send(sendSignal);
		}
	}


	/**
	 *  対戦相手の順番
	 */
	private void enemyTurn() throws IOException{
		_monitor.setMessage("Enemy thinking ....");
		int getSignal = recive();
		int x = getSignal / 10;
		int y = getSignal % 10;
		if(x==8 && y==8){
			_enemyPass++;
			if(_enemyPass == 3){
				_monitor.setMessage("勝ちました");
				_monitor.lock();
				return;
			}
		}else{
			_board.putPiece(x,y,new Piece(_enemyPiece) );
		}
	}


	/**
	 *  マウス入力を許可する
	 */
	public void mouseEnable(){
		_putFlag = true;
		_coordinates = null;
	}


	/**
	 *  マウス入力を不許可にする
	 */
	public void mouseDisable(){
		_putFlag = false;
	}


	/**
	 *  入力座標を返す
	 */
	public Coordinates getCoordinates(){
		return(_coordinates);
	}

	/**
	 *  入力座標を確保する
	 */
	public void setCoordinates(int x,int y){
		mouseDisable();
		_coordinates = new Coordinates(x,y);
	}


	/**
	 *  MouseEventを拾う
	 */
	public void mousePressed(MouseEvent e){
		// 自分の順番にならないと入力を受け付けない。
		if(_putFlag == true){
			int x = (e.getX()-10) / 50;
			int y = (e.getY()-10) / 50;

			Piece piece;
			if( _color.compareTo("black") == 0){
				piece = new Piece(Piece.BLACK);
			}else{
				piece = new Piece(Piece.WHITE);
			}

			int res = _board.getReversePiece(x,y,piece);
			if(res != 0){
				setCoordinates(x,y);
			}
		}
	}

	/* 使わないマウスイベント */
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mouseClicked(MouseEvent e){}
	public void mouseReleased(MouseEvent e){}

}
