/** Game of Life
 *
 * @author Alessandro Bigano VR359928
 * 
 * Le immagini,i nomi e l'ordine delle configurazioni utilizzate sono state prese dal sito
 * http://en.wikipedia.org/wiki/Conway's_Game_of_Life
 * 
 */
package GameOfLife;

import java.awt.Dimension;
/**
 * Classe principale
 */
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JSlider;
import javax.swing.JOptionPane;


@SuppressWarnings("serial")
public class GameOfLife extends JFrame {
	// Calcolo le dimensioni dello schermo
	Dimension screenSize=Toolkit.getDefaultToolkit().getScreenSize();
	// Imposto la larghezza della finestra dell'applicazione (4/5 della larghezza dello schermo)
	int width=(screenSize.width/5)*4;
	// Imposto l'altezza della finestra dell'applicazione (4/5 dell'altezza dello schermo)
	int height=(screenSize.height/5)*4;
	// Numero di colonne della griglia
	int row;
	// Numero di colonne della griglia
	int col;
	// Variabile di selezione del widget clickato 
	int select=0;
	// Variabile che indica se il gioco è in pausa o è attivo
	boolean gioca=false;
	// Dimensione in pixel di ogni singola cella
	final int dim=10;
	// Colore delle cellule vive
	final Color viva=Color.WHITE;
	// Colore delle cellule morte
	final Color morta=Color.BLACK;
	// Matrice principale delle cellule di Game of Life
	Cella griglia[][];
	// Matrice ausiliaria per la generazione successiva
	boolean next[][];
	// Dichiarazione dello slider per controllare la velocità di passaggio tra generazioni
	JSlider slider_velocità;	
	// Vettore dei thread 
	ThreadComp thread[];

	/**
	 * Costruttore della finestra del Game of Life 
	 * - Crea la finestra
	 * - Stampa il menu di selezione dei widget, i pulsanti "play/pause", "refresh" e lo slider
	 *   di selezione della velocità di refresh
	 * - Stampa la griglia delle cellule
	 * 
	 * @param threads : numero dei thread immessi dall'utente
	 */
	public GameOfLife(int threads) {
		super("Game of Life");
		creaFinestra();	
		Menu();
		Griglia();
        if(threads>row)
            threads=row;
        if(threads<1)
            threads=1;
		thread=new  ThreadComp[threads];
		Flusso();
	}

	/**
	 * Costruttore della finestra:
	 * - Imposta le dimensioni
	 * - Calcola il numero di righe e colonne della griglia
	 * - Imposta la modalità di chiusura dell'applicazione
	 * - Imposta il background col colore bianco
	 * - Blocca le dimensioni della finestra
	 */
	public void creaFinestra() {
		setSize(width,height);
		row=(height)/dim;
		col=width/dim;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(null);
		getContentPane().setBackground(Color.WHITE);
		setResizable(false);
	}

	/**
	 * Il seguente metodo stampa (da sinistra a destra in alto):
	 * - Il pulsante "play/pausa"
	 * - Il vettore dei widget richiesti dalle specifiche
	 * - Il pulsante "refresh" per far morire tutte le cellule presenti sulla griglia
	 *   e mettere il pausa il gioco
	 * - Lo slider della velocità
	 */
	private void Menu() {
		Play play=new Play();
		getContentPane().add(play);
		play.setBounds(0,0,60,60);	

		Refresh refresh= new Refresh();
		getContentPane().add(refresh);
		refresh.setBounds(420,0,60,60);

		Config arr[]=new Config[6];

		arr[0]=new Config("blinker");
		getContentPane().add(arr[0]);
		arr[0].setBounds(60,0,60,60);

		arr[1]=new Config("toad");
		getContentPane().add(arr[1]);
		arr[1].setBounds(120,0,60,60);

		arr[2]=new Config("beacon");
		getContentPane().add(arr[2]);
		arr[2].setBounds(180,0,60,60);

		arr[3]=new Config("pulsar");
		getContentPane().add(arr[3]);
		arr[3].setBounds(240,0,60,60);

		arr[4]=new Config("glider");
		getContentPane().add(arr[4]);
		arr[4].setBounds(300,0,60,60);

		arr[5]=new Config("spaceship");
		getContentPane().add(arr[5]);
		arr[5].setBounds(360,0,60,60);

		//print speed slider
		slider_velocità=new JSlider(JSlider.HORIZONTAL,1,30,15);
		slider_velocità.setBackground(Color.WHITE);
		getContentPane().add(slider_velocità);
		slider_velocità.setBounds(width-480,0, 480, 59);
	}

	/**
	 * La seguente classe costruisce il pulsante "play/pausa"
	 * e ne descrive il comportamento.
	 * E' presente, inoltre, un metodo che ritorna true/false 
	 * a seconda che il gioco sia in pausa o meno.
	 */
	public class Play extends JButton{
		protected Play(){
			super(new ImageIcon("images/play.png"));
			this.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					select=0;
					gioca=gioca?false:true;
				}
			});
		}
		public boolean inPausa(){
			return !gioca;
		}
	}

	/**
	 * La seguente classe costruisce il pulsante "refresh"
	 * e ne descrive il comportamento.
	 */
	public class Refresh extends JButton {
		protected Refresh() {
			super(new ImageIcon("images/refresh.png"));
			this.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					for(int x=0;x<row;x++)
						for(int y=0;y<col;y++)
							griglia[x][y].die();
					gioca=false;
				}

			});
		}

	}

	/**
	 * La seguente classe costruisce i pulsanti delle configurazioni (widget)
	 * richiesti dalle specifiche del progetto.
	 */
	public class Config extends JButton{

		/**
		 * Il seguente costruttore crea i pulsanti delle varie configurazioni e 
		 * ne descrive i comportamenti.
		 * 
		 * @param configurazione : stringa per la costruzione delle icone dei widget
		 */
		protected Config(String configurazione){
			super(new ImageIcon("images/" + configurazione + ".png"));
			switch(configurazione){
			case "blinker":
				this.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent event) {
						select=select!=1?1:0;
					}
				});
				break;
			case "toad":
				this.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent event) {
						select=select!=2?2:0;
					}
				});
				break;
			case "beacon":
				this.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent event) {
						select=select!=3?3:0;
					}
				});
				break;
			case "pulsar":
				this.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent event) {
						select=select!=4?4:0;
					}
				});
				break;
			case "glider":
				this.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent event) {
						select=select!=5?5:0;
					}
				});
				break;
			case "spaceship":
				this.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent event) {
						select=select!=6?6:0;
					}
				});
			}
		} 
	}

	/**
	 * Il seguente metodo scandisce la matrice Griglia e crea le cellule 
	 */
	private void Griglia() {
		griglia=new Cella[row][col];
		next=new boolean[row][col];
		for(int x=0;x<row;x++){
			for(int y=0;y<col;y++){
				getContentPane().add(griglia[x][y]=new Cella(x,y));
			}
		}
		setVisible(true);
	}

	/**
	 * La seguente classe definisce l'elemento principale della gioco (la cellula)
	 * e i diversi comportamenti che esso assume.
	 */

	public class Cella extends JButton {
		// Riga che contiene la cellula
		private final int row_now;
		// Colonna che contiene la cellula
		private final int col_now;
		// La cellula viene inizializzata "morta"
		private boolean dead=true;

		/**
		 * Il costruttore seguente crea una cellula morta
		 * 
		 * @param rows: riga che contiene la cellula
		 * @param cols: colonna che contiene la cellula
		 */
		protected Cella(int rows, int cols) {
			super();
			row_now=rows;
			col_now=cols;

			// Stampo la cellula
			setBounds((cols*dim), rows*dim+60, dim, dim);
			setBackground(morta); 
			this.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent event) {
					switch(select){
					// se la cellula è morta, viene fatta nascere (e viceversa)
					case 0:
						if (griglia[row_now][col_now].Morta())
							griglia[row_now][col_now].born();
						else
							griglia[row_now][col_now].die();
						break;
						//blinker
					case 1:
						griglia[row_now][col_now].born();
						if((row_now+1)<row)
							griglia[row_now+1][col_now].born();
						if((row_now+2)<row)
							griglia[row_now+2][col_now].born();
						break;
						//toad
					case 2:
						griglia[row_now][col_now].born();
						if((row_now+1)<row){
							griglia[row_now+1][col_now].born();
							if((col_now-1)>0)
								griglia[row_now+1][col_now-1].born();
							if((col_now+1)<col){
								griglia[row_now+1][col_now+1].born();
								griglia[row_now][col_now+1].born();
							}
							if((col_now+2)<col)
								griglia[row_now][col_now+2].born();
						}else{
							if((col_now+1)<col){
								griglia[row_now][col_now+1].born();
							}
							if((col_now+2)<col)
								griglia[row_now][col_now+2].born();
						}
						break;
						//beacon
					case 3:
						griglia[row_now][col_now].born();
						if((row_now+1)<row){
							griglia[row_now+1][col_now].born();
							if((col_now+1)<col){
								griglia[row_now+1][col_now+1].born();
								griglia[row_now][col_now+1].born();
								if((col_now+2)<col && (row_now+2)<row){
									griglia[row_now+2][col_now+2].born();
									if((row_now+3)<row){
										griglia[row_now+3][col_now+2].born();
										if((col_now+3)<col){
											griglia[row_now+3][col_now+3].born();
											griglia[row_now+2][col_now+3].born();
										}
									}else{
										if((col_now+3)<col){
											griglia[row_now+2][col_now+3].born();
										}
									}
								}
							}
						}else{
							if((col_now+1)<col)
								griglia[row_now][col_now+1].born();
						}
						break;
						//pulsar
					case 4:
						griglia[row_now][col_now].born();
						if((row_now+5)<row)
							griglia[row_now+5][col_now].born();
						if((row_now+7)<row)
							griglia[row_now+7][col_now].born();
						if((row_now+12)<row)
							griglia[row_now+12][col_now].born();
						if((col_now-2)>=0){
							if((row_now+2)<row)
								griglia[row_now+2][col_now-2].born(); 
							if((row_now+3)<row)
								griglia[row_now+3][col_now-2].born();
							if((row_now+4)<row)
								griglia[row_now+4][col_now-2].born();
							if((row_now+8)<row)
								griglia[row_now+8][col_now-2].born();
							if((row_now+9)<row)
								griglia[row_now+9][col_now-2].born();
							if((row_now+10)<row)
								griglia[row_now+10][col_now-2].born();
						}
						if((col_now+1)<col){
							griglia[row_now][col_now+1].born();
							if((row_now+5)<row)
								griglia[row_now+5][col_now+1].born();
							if((row_now+7)<row)
								griglia[row_now+7][col_now+1].born();
							if((row_now+12)<row)
								griglia[row_now+12][col_now+1].born();     
							if((col_now+2)<col){
								griglia[row_now][col_now+2].born();
								if((row_now+5)<row)
									griglia[row_now+5][col_now+2].born();
								if((row_now+7)<row)
									griglia[row_now+7][col_now+2].born();
								if((row_now+12)<row)
									griglia[row_now+12][col_now+2].born();
								if((col_now+3)<col){
									if((row_now+2)<row)
										griglia[row_now+2][col_now+3].born(); 
									if((row_now+3)<row)
										griglia[row_now+3][col_now+3].born();
									if((row_now+4)<row)
										griglia[row_now+4][col_now+3].born();
									if((row_now+8)<row)
										griglia[row_now+8][col_now+3].born();
									if((row_now+9)<row)
										griglia[row_now+9][col_now+3].born();
									if((row_now+10)<row)
										griglia[row_now+10][col_now+3].born();
									if((col_now+5)<col){
										if((row_now+2)<row)
											griglia[row_now+2][col_now+5].born(); 
										if((row_now+3)<row)
											griglia[row_now+3][col_now+5].born();
										if((row_now+4)<row)
											griglia[row_now+4][col_now+5].born();
										if((row_now+8)<row)
											griglia[row_now+8][col_now+5].born();
										if((row_now+9)<row)
											griglia[row_now+9][col_now+5].born();
										if((row_now+10)<row)
											griglia[row_now+10][col_now+5].born();
										if((col_now+6)<col){
											griglia[row_now][col_now+6].born();
											if((row_now+5)<row)
												griglia[row_now+5][col_now+6].born();
											if((row_now+7)<row)
												griglia[row_now+7][col_now+6].born();
											if((row_now+12)<row)
												griglia[row_now+12][col_now+6].born();
											if((col_now+7)<col){
												griglia[row_now][col_now+7].born();
												if((row_now+5)<row)
													griglia[row_now+5][col_now+7].born();
												if((row_now+7)<row)
													griglia[row_now+7][col_now+7].born();
												if((row_now+12)<row)
													griglia[row_now+12][col_now+7].born();
												if((col_now+8)<col){
													griglia[row_now][col_now+8].born();
													if((row_now+5)<row)
														griglia[row_now+5][col_now+8].born();
													if((row_now+7)<row)
														griglia[row_now+7][col_now+8].born();
													if((row_now+12)<row)
														griglia[row_now+12][col_now+8].born();
													if((col_now+10)<col){
														if((row_now+2)<row)
															griglia[row_now+2][col_now+10].born(); 
														if((row_now+3)<row)
															griglia[row_now+3][col_now+10].born();
														if((row_now+4)<row)
															griglia[row_now+4][col_now+10].born();
														if((row_now+8)<row)
															griglia[row_now+8][col_now+10].born();
														if((row_now+9)<row)
															griglia[row_now+9][col_now+10].born();
														if((row_now+10)<row)
															griglia[row_now+10][col_now+10].born();
													}
												}
											}
										}
									}
								}
							}
						}
						break;
						//glider
					case 5:
						griglia[row_now][col_now].born();
						if((col_now+1)<col){
							if((row_now+1)<row){
								griglia[row_now+1][col_now+1].born();
								if((row_now+2)<row)
									griglia[row_now+2][col_now+1].born();
							}    
							if((col_now+2)<col){
								griglia[row_now][col_now+2].born();
								if((row_now+1)<row)
									griglia[row_now+1][col_now+2].born();
							}
						}
						break;
						//lightweight spaceship
					case 6:
						griglia[row_now][col_now].born();
						if((col_now+3)<col)
							griglia[row_now][col_now+3].born();
						if((row_now+1)<row){
							if((col_now+4)<col)
								griglia[row_now+1][col_now+4].born();
							if((row_now+2)<row){
								griglia[row_now+2][col_now].born();
								if((col_now+4)<col)
									griglia[row_now+2][col_now+4].born();
								if((row_now+3)<row){
									if((col_now+1)<col){
										griglia[row_now+3][col_now+1].born();
										if((col_now+2)<col){
											griglia[row_now+3][col_now+2].born();
											if((col_now+3)<col){
												griglia[row_now+3][col_now+3].born();
												if((col_now+4)<col)
													griglia[row_now+3][col_now+4].born();
											}
										}
									}
								}
							}
						}
					} 
				}
			});
		}

		/**
		 * Il seguente metodo fa morire la cellula
		 */
		public void die(){
			setBackground(morta);
			dead=true;
		}

		/**
		 * Il seguente metodo fa nascere la cellula
		 */
		public void born(){
			setBackground(viva);
			dead=false;
		}
		/**
		 * Il seguente metodono ritorna lo stato della cellula
		 * 
		 * @return : ritorna true se la cellula è morta, false se la cellula è viva
		 */
		public boolean Morta(){
			return dead;
		}
	}

	/**
	 * La seguente è la classe Main:
	 * - Chiede all'utente di immettere il numero di thread desiderati
	 * - Se l'input è un numero intero viene lanciata l'applicazione 
	 *   con il numero di thread indicati
	 * - Se l'input non è un intero viene lanciata una NumberFormatException
	 */
	public static void main(String[] args) {
		GameOfLife frame;
		String number;
		try{
			number=JOptionPane.showInputDialog(null,
					"Immettere il numero di thread utilizzati per Game of Life:",
					"Inizializzazione",
					JOptionPane.PLAIN_MESSAGE );
			frame = new GameOfLife(Integer.parseInt(number));
		}catch(NumberFormatException e){
			System.out.println(e.toString());
			frame = new GameOfLife(1);
		}
		

		frame.setVisible(true);

	}

    /**
     * Il seguente metodo attiva i thread nel momento in cui l'utente 
     * preme il tasto "play/pausa" e regola il mutamento del mondo del Game of Life
     */
	private void Flusso(){
		int threads=thread.length;
		while(true){
			
			try{
				Thread.sleep(1200/slider_velocità.getValue());
			}catch(InterruptedException e){
				System.out.println(e.toString());
			}

			if(gioca){
				for(int i=0;i<threads;i++){
					thread[i]=new  ThreadComp(threads, i);
					thread[i].start();
				}
				for(int i=0;i<threads;i++)
					try {
						thread[i].join();
					} catch (InterruptedException e) {
						System.out.println(e.toString());
					}
				

				for(int x=0;x<row;x++)
					for(int y=0;y<col;y++){
						if(next[x][y])
							griglia[x][y].born();
						else 
							griglia[x][y].die();
					}
				
				repaint();
			}
		}
	}

	/**
	 * La seguente classe descrive il comportamento dei thread
	 */
	public class  ThreadComp extends Thread {
		// numero di righe da non considerare tra una scansione e l'altra
		private final int skip;
		// riga su cui lavorare
		private int row_now;

		/**
		 * Il seguente costruttore imposta la riga da cui partire
		 * e il numero di righe da non considerare
		 * 
		 * @param start : riga di partenza
		 * @param skp : numero di righe da non considerare
		 */
		protected  ThreadComp(int skp,int start){
			row_now=start;
			skip=skp;
		}
		@Override
		public void run(){
			int vicini;
			while(row_now<row){
				for(int c=0;c<col;c++){
					vicini=popolazione(row_now,c);
					if(!griglia[row_now][c].Morta() && 
							(vicini<3 || vicini>4)) 
						next[row_now][c]=false;
					
					else
						if(griglia[row_now][c].Morta() && vicini==3)
							next[row_now][c]=true;					
						else
							next[row_now][c]=!griglia[row_now][c].Morta();
					
				}
				row_now+=skip;
			}

		}
		
		/**
		 * Il seguente metodo controlla i vicini della cellula 
		 * e restituisce il numero dei vicini "vivi"
		 * 
		 * @param row_now : riga che contiene la cellula da esaminare
		 * @param col_now : colonna che contiene la cellula da esaminare
		 * @return : numero di vicini vivi
		 */
		private int popolazione(int row_now, int col_now){
			int vicini=0;
			if(!griglia[row_now][col_now].Morta())
				vicini++;
			if(row_now>0){
				if(!griglia[row_now-1][col_now].Morta())
					vicini++;
				if(row_now<row-1){
					if(!griglia[row_now+1][col_now].Morta())
						vicini++;
					if(col_now>0){
						if(!griglia[row_now][col_now-1].Morta())
							vicini++;
						if(!griglia[row_now+1][col_now-1].Morta())
							vicini++;
						if(!griglia[row_now-1][col_now-1].Morta())
							vicini++;
					}
					if(col_now<col-1){
						if(!griglia[row_now-1][col_now+1].Morta())
							vicini++;
						if(!griglia[row_now+1][col_now+1].Morta())
							vicini++;
						if(!griglia[row_now][col_now+1].Morta())
							vicini++;

					}
				}
				else{
					if(col_now<col-1){
						if(!griglia[row_now][col_now+1].Morta())
							vicini++;
						if(!griglia[row_now-1][col_now+1].Morta())
							vicini++;
					}
					if(col_now>0){
						if(!griglia[row_now-1][col_now-1].Morta())
							vicini++;
						if(!griglia[row_now][col_now-1].Morta())
							vicini++;
					}

				}
			}
			else{
				if(!griglia[row_now+1][col_now].Morta())
					vicini++;
				if(col_now>0){
					if(!griglia[row_now][col_now-1].Morta())
						vicini++;
					if(!griglia[row_now+1][col_now-1].Morta())
						vicini++;
				}
				if(col_now<col-1){
					if(!griglia[row_now][col_now+1].Morta())
						vicini++;
					if(!griglia[row_now+1][col_now+1].Morta())
						vicini++;
				}

			}
			return vicini;
		}
	}
}


