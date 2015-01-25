import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

/*---------------------------------------------------------------
 *  AI
 *---------------------------------------------------------------*/
public class AI1 implements Runnable{

	private		Socket				_socket = null;				/* ソケット */
	private		DataInputStream		_dataInputStream = null;	/* 入力ストリーム */
	private		DataOutputStream	_dataOutputStream = null;	/* 出力ストリーム */

	private		Monitor				_monitor = null;			/* モニタ */
	private		Board				_board = null;				/* ゲーム盤 */
	private		String				_color;						/* AIが担当する色 */
	public		Piece				_myPiece;					/* AIが担当する色のピース */
	public		Piece				_enemyPiece;				/* 相手が担当する色のピース */

	private volatile Thread			_blinker = null;			/* オブジェクトのスレッド */

	private		int					_myPass;					/* 自分のパスの回数 */
	private		int					_enemyPass;					/* 相手のパスの回数 */
	private		int					_wait;						/* ウェイト */

	/**
	 *  main文
	 */
	public static void main(String args[]){
		try{
			AI1 ai = new AI1(InetAddress.getByName(args[0]),args[1]);
			ai.start();
		}catch(UnknownHostException e){
			System.out.println("IP Error");
			System.out.println("Can't conect to "+args[0]+" :"+args[1]);
			System.exit(0);
		}catch(Exception e){
			System.out.println("Usage  : java Controller \"IPアドレス\"or\"ホスト名\"  \"石の色 \n");
			System.out.println("Sample : java Controller trombone 9999 white");
			System.exit(0);
		}
	}

	/**
	 *  <コンストラクタ>
	 *
	 * @param	InetAddress		piserverAddressece		サーバのInetAddress
	 * @param	int				port					接続するポート
	 * @param	String			color					クライアントの担当する色
	 * @param	int				wait					ウェイト
	 */
	public AI1(InetAddress serverAddress,String color) throws Exception{
		/*
		 *  担当色 etc の設定
		 */
	        int port;
		color = color.toLowerCase();
		if(color.compareTo("white") != 0 && color.compareTo("black") != 0){
			throw(new Exception());
		}
		_color = color;
		if(_color.compareTo("white") == 0){
			_myPiece = new Piece(Piece.WHITE);
			_enemyPiece = new Piece(Piece.BLACK);
			port = 9999;
		}else{
			_myPiece = new Piece(Piece.BLACK);
			_enemyPiece = new Piece(Piece.WHITE);
			port = 9998;
		}
		_myPass = 0;
		_enemyPass = 0;
		// _wait = 1500;
		_wait = 10;

		/*
		 *  サーバに接続
		 */
		try{
			_socket = new Socket(serverAddress,port);
			_dataInputStream = new DataInputStream( _socket.getInputStream() );
			_dataOutputStream = new DataOutputStream( _socket.getOutputStream() );
		}catch(Exception e){
			throw(new UnknownHostException());
		}

		/*
		 *  ゲーム盤の用意
		 */
		_board = new Board();
		_board.init();

		/*
		 *  モニタの用意
		 */
		_monitor = new Monitor(_board);
		_monitor.start();
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
	 *  スレッド
	 */
	public void run(){
		Thread thisThread = Thread.currentThread();
		int x,y;

		try{
			/*
			 *  自分の色が白ならば、相手が打つのを待つ
			 */
			if(_color.compareTo("white") == 0){
				_monitor.setMessage("Enemy thinking ....");
				int getSignal = recive();
				x = getSignal / 10;
				y = getSignal % 10;
				_board.putPiece(x,y,new Piece(Piece.BLACK) );
			}

			while(_blinker == thisThread){
				/*
				 *  自分の順番
				 */
				_monitor.setMessage("Now thinking ....");

			    	Coordinates coordinates = think();

				_board.putPiece(coordinates.x,coordinates.y,new Piece(_myPiece) );
				int sendSignal = 10 * coordinates.x + coordinates.y;
				Thread.sleep(_wait);
				send(sendSignal);
				_monitor.setMessage("Enemy thinking ....");

				/*
				 * 相手の順番
				 */
				int getSignal = recive();
				x = getSignal / 10;
				y = getSignal % 10;
				if(x==8 && y== 8){
					_enemyPass++;
					if(_enemyPass == 3){
						_monitor.setMessage("ゲーム終了");
						break;
					}
				}else{
					_board.putPiece(x,y,new Piece(_enemyPiece) );
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
	 *  データ送信
	 *
	 * @param	int		message		送信する信号
	 */
	private void send(int message) throws IOException {
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
	private int recive() throws IOException {
		try{
			return( _dataInputStream.readInt() );
		}catch(Exception e){
			throw(new IOException() );
		}
	}


	/**
	 *  人工知能部分
	 */
	private Coordinates think(){
		/*
		 *  自分が置ける場所を探す
		 */
		Vector<Heuristics> vector = new Vector<Heuristics>();

     System.out.println("-------------------------------------------");
	debug_CanPut();
//      Check.boardstate(_board,_myPiece);

	/* 序盤、中盤、終盤の見極め */
	int _totalPiece = _board.getPutPieceNum();
	int _step = 0;
	double xx[] = new double[3];
	Fuzzy step1 = new Fuzzy(4, 4, 4, 20);
	Fuzzy step2 = new Fuzzy(20, 20, 20, 50);
	Fuzzy step3 = new Fuzzy(50, 64, 64, 64);
	xx[0]=Fuzzy.truth(_totalPiece,step1);
	xx[1]=Fuzzy.truth(_totalPiece,step2);
	xx[2]=Fuzzy.truth(_totalPiece,step3);
	double max=xx[0];
	for(int i = 1; i < 3; i++) {
		if (max < xx[i]) {
			max = xx[i];
			_step = i;
		}
	}

		for(int y=0;y < 8;y++){
		   for(int x=0;x < 8;x++){
		      if(_board.getPiece(x,y) == null){
			 if(_board.getReversePiece(x,y,_myPiece) != 0){
			    Coordinates coordinate = new Coordinates(x,y);
			    Heuristics heuristics = new Heuristics(coordinate,_board,_myPiece,_step);
			    vector.addElement(heuristics);
		         }
		      }
		   }
		}

		if(vector.size() <= 0){
			/*
			 *  パス（負け）
			 */
			_myPass++;
			if(_myPass == 3){
				_monitor.setMessage("ゲーム終了");
			}
			return(new Coordinates(8,8));
		}else{
			Heuristics best = (Heuristics)vector.elementAt(0);
			for(int i=1;i < vector.size();i++){
				Heuristics heuristics = (Heuristics)vector.elementAt(i);
				if( best.getScore() < heuristics.getScore() ){
					best = heuristics;
				}
			}

			System.out.println("選ばれた座標は、("+best.getCoordinates().x+","+best.getCoordinates().y+") です。\n");

			return( best.getCoordinates() );
		}
	}



	/**
	 *  AIが置ける場所を出力する
	 *  (デバック用)
	 */
	private void debug_CanPut(){
		System.out.print("  0 1 2 3 4 5 6 7\n");
		for(int y=0;y < 8;y++){
			System.out.print(y+" ");
			for(int x=0;x < 8;x++){
				if(_board.getPiece(x,y) == null){
					int point;
					if(_board.getReversePiece(x,y,_myPiece) != 0){
						System.out.print("☆");
					}else{
						System.out.print("□");
					}
				}else{
					if(_board.getPiece(x,y).getColor() == Piece.BLACK){
						System.out.print("●");
					}else{
						System.out.print("○");
					}
				}
			}
			System.out.print("\n");
		}
		System.out.print("\n");
	}

}
