package com.nrstudios.main;


	import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.nrstudios.entities.BulletShoot;
import com.nrstudios.entities.Enemy;
import com.nrstudios.entities.Entity;
import com.nrstudios.entities.Player;
import com.nrstudios.graficos.Pixel;
import com.nrstudios.graficos.Spritesheet;
import com.nrstudios.graficos.UI;
import com.nrstudios.world.World;
	
	public class Game extends Canvas implements Runnable,KeyListener,MouseListener,MouseMotionListener{ 

		private static final long serialVersionUID = 1L;
		public static JFrame frame;
		private Thread thread;
		private boolean isRunning = true;
		
		public static final int Width = 240;	
		public static final int Height = 160;
		public static final int SCALE = 3;
		
		private int CUR_LEVEL = 1,MAX_LEVEL = 2;
		private BufferedImage image;
		
		public static BufferedImage minimap;
		
		public static List<Entity> entities;
		public static List<Enemy> enemies;
		public static List<BulletShoot> bullets;	
		public static Spritesheet spritesheet;
		
		public static World world;
		
		public static Player player;
		
		public static Random rand;
		
		public UI Ui;
		
		public static String gameState = "MENU";
		private boolean showMessageGameOver = true;
		private int framesGameOver = 0;
		private boolean restartGame = false;
		
		//cutcene
		public static int entrada = 1;
		public static int comecar = 2;
		public static int jogando = 3;
		public static int estado_cena = entrada;
		
		public int timeCena = 0, MaxTimeCena = 60*3;
		
		
		public Menu menu;
		
		
		public int[] pixels;
		public BufferedImage lightMap;
		public int[] lightMapPixels;
		public static int[] minimapPixels;
		
		public boolean saveGame = false;
		
		public int mx,my;
		
		public Game() {
			rand = new Random();
			addKeyListener(this);
			addMouseListener(this);
			addMouseMotionListener(this);
			//setPreferredSize(new Dimension(Toolkit.getDefaultToolkit().getScreenSize()));
			setPreferredSize(new Dimension(Width*SCALE, Height*SCALE));
			initFrame();
			//INICIALIZANDO ENTITIES
			
			Ui = new UI();
			image = new BufferedImage(Width,Height,BufferedImage.TYPE_INT_RGB);
			try {
				lightMap = ImageIO.read(getClass().getResource("/lightmap.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			lightMapPixels = new int[lightMap.getWidth() * lightMap.getHeight()];
			//ARRAYLIST
			lightMap.getRGB(0, 0, lightMap.getWidth(), lightMap.getHeight(), lightMapPixels, 0, lightMap.getWidth());
			pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
			entities = new ArrayList<Entity>();
			enemies = new ArrayList<Enemy>();
			bullets = new ArrayList<BulletShoot>();
			
			//SPRITES
			spritesheet = new Spritesheet("/Spritesheet.png");
			player = new Player(0,0,16,16,spritesheet.getSprite(32, 0, 16, 16));
			entities.add(player);
			world = new World("/level1.png");
			
			minimap = new BufferedImage(World.WIDTH, World.HEIGHT,BufferedImage.TYPE_INT_RGB);
			minimapPixels = ((DataBufferInt)minimap.getRaster().getDataBuffer()).getData();
			
			menu = new Menu();
		}	
		
		public void initFrame() {
			frame = new JFrame("Zelda FODA");
			frame.add(this);
			frame.setResizable(false);
			//frame.setUndecorated(true);
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
			
		}
			
		public synchronized void start() {
			thread = new Thread(this);
			isRunning = true;
			thread.start();
		}
		
		public synchronized void stop() {
			isRunning = false;
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		public void tick() {
			if(gameState == "NORMAL") {
				if(this.saveGame) {
					this.saveGame = false;
					String[] opt1 = {"level",};
					int[] opt2 = {this.CUR_LEVEL,};
					Menu.saveGame(opt1,opt2,10);
				}
			this.restartGame = false;
			
			if(Game.estado_cena == Game.jogando) {
			for(int i = 0; i < entities.size(); i++) {
				Entity e = entities.get(i);
				e.tick();
				
				}
			
			for(int i = 0; i < bullets.size(); i++) {
				bullets.get(i).tick();
				}
				}else {
					if(Game.estado_cena	== Game.entrada) {
						if(player.x < 120) {
							player.x++;
						}else {
							System.out.println("a");
							Game.estado_cena = Game.comecar;
						}
					}else if(Game.estado_cena == Game.comecar) {
						timeCena++;
						if(timeCena == MaxTimeCena) {
							Game.estado_cena = Game.jogando;
						}
					}	
				}
			if(enemies.size() == 0) {
				//PROXIMO LEVEL
				CUR_LEVEL++;
				if(CUR_LEVEL > MAX_LEVEL) {
					CUR_LEVEL = 1;
					}
				String newWorld = "level" + CUR_LEVEL + ".png";
				World.restartGame(newWorld);
				} 
			}else if(gameState == "GAME_OVER") {
					this.framesGameOver++;
					if(this.framesGameOver == 50) {
						this.framesGameOver = 0;
						if(this.showMessageGameOver)
							this.showMessageGameOver = false;
							else
								this.showMessageGameOver = true;
					}
					
					if(restartGame) {
						this.restartGame = false;
						this.gameState = "NORMAL";
						CUR_LEVEL = 1;
						String newWorld = "level" + CUR_LEVEL + ".png";
						World.restartGame(newWorld);	
					}
				}else if(gameState == "MENU") {
					player.updateCamera();
					menu.tick();
				}
			}
		
		public void applyLight() {
			for(int xx = 0; xx < Width; xx++) {
				for(int yy = 0; yy < Height; yy++) {
					if(lightMapPixels[xx+yy*Width] == 0xFF000000) {
						int pixel = Pixel.getLightBlend(pixels[xx+yy*Width], 0x282828, 0);
						pixels[xx+yy*Width] = pixel;
					}				
				}
			}
		}
		
		public void render() {
			BufferStrategy bs = this.getBufferStrategy();
			if(bs == null) {
				this.createBufferStrategy(3);
				return;
			}
			Graphics g = image.getGraphics();
			g.setColor(new Color(0, 0, 0));
			g.fillRect(0, 0, Width,Height);
			
			/*render*/
			world.render(g);
			Collections.sort(entities,Entity.nodeSorter);
			for(int i = 0; i < entities.size(); i++) {
				Entity e = entities.get(i);
				e.render(g);
			}
				for(int i = 0; i < bullets.size(); i++) {
					bullets.get(i).render(g);
				
			}
			applyLight();
			//UI
			Ui.render(g);
			 /***/
			g.dispose();
			g = bs.getDrawGraphics();
			//AQUI É ONDE RENDERIZAMOS O JOGO
			g.drawImage(image, 0, 0, Width*SCALE, Height*SCALE,null);
			g.setFont(new Font("arial",Font.BOLD,17));
			g.setColor(Color.white);
			g.drawString("Flechas: " + player.bullet, 10, 50);
			if(gameState == "GAME_OVER") {
				Graphics2D g2 = (Graphics2D) g;
				g2.setColor(new Color(0, 0, 0, 100));
				g2.fillRect(0, 0, Width*SCALE, Height*SCALE);
				g.setFont(new Font("arial",Font.BOLD,36));
				g.setColor(Color.white);
				g.drawString("Game Over" , (Width*SCALE) / 2 - 90,(Height*SCALE) / 2 - 20);
				g.setFont(new Font("arial",Font.BOLD,32));
				if(showMessageGameOver)
				g.drawString(">Pressione Enter para reiniciar<" , (Width*SCALE) / 2 - 240,(Height*SCALE) / 2 + 20);
			}else if(gameState == "MENU") {
				menu.render(g);
			}
			if(Game.estado_cena == Game.comecar) {
				g.drawString("Bem-vindo", (Width*SCALE) / 2 - 80,(Height*SCALE) / 2 - 20);
			}
				World.renderMiniMap();
			g.drawImage(minimap,615,5,World.WIDTH * 5,World.HEIGHT * 5,null);
			bs.show();
			
		}
		
		public static void main(String args[]) {
			Game game = new Game();
			game.start();
		}
		
		public void run() {
			long lastTime = System.nanoTime();
			double amountOfTicks = 60.0;
			double ns = 1000000000 / amountOfTicks;
			double delta = 0;
			int frames = 0;
			double timer = System.currentTimeMillis();
			requestFocus();
			while(isRunning) {
				long now = System.nanoTime();
				delta+= (now - lastTime) / ns;	
				lastTime = now;
				if(delta >= 1){
					tick();
					render();
					frames++;
					delta--;	
				}
				
			
			if(System.currentTimeMillis() -	 timer >= 1000) {
				System.out.println("FPS: "+ frames);
				frames = 0;
				timer+=1000;
			}
		    
		}	
			
			stop();
			
				}

		@Override
		public void keyTyped(KeyEvent e) {
			
		}
		
		@Override
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_RIGHT ||
					e.getKeyCode() == KeyEvent.VK_D) {
					player.right = true;
			}else if(e.getKeyCode() == KeyEvent.VK_LEFT ||
					e.getKeyCode() == KeyEvent.VK_A) {
				player.left = true;
				
		}
			
			if(e.getKeyCode() == KeyEvent.VK_UP ||
					e.getKeyCode() == KeyEvent.VK_W) {
				player.up = true;
				
				if(gameState == "MENU") {
					menu.up = true;
				}
				
			}else if(e.getKeyCode() == KeyEvent.VK_DOWN ||
					e.getKeyCode() == KeyEvent.VK_S) {
				player.down = true;
				
				if(gameState == "MENU") {
					menu.down = true;
				}
			}
			
			if(e.getKeyCode() == KeyEvent.VK_SPACE) {
				player.shoot = true;
			}
			
			if(e.getKeyCode() == KeyEvent.VK_ENTER){
				this.restartGame = true;
				if(gameState == "MENU") {
					menu.enter = true;
				}
			}
			
			if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				gameState = "MENU";
				menu.pause = true;
			}
			if(e.getKeyCode() == KeyEvent.VK_SPACE) {
				if(gameState == "NORMAL")
					this.saveGame = true;
			}
			
		}

		@Override
		public void keyReleased(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_RIGHT ||
					e.getKeyCode() == KeyEvent.VK_D) {
					player.right = false;
			}else if(e.getKeyCode() == KeyEvent.VK_LEFT ||
					e.getKeyCode() == KeyEvent.VK_A) {
				player.left = false;
				
		}
			
			if(e.getKeyCode() == KeyEvent.VK_UP ||
					e.getKeyCode() == KeyEvent.VK_W){
				player.up = false;
			}else if(e.getKeyCode() == KeyEvent.VK_DOWN ||
					e.getKeyCode() == KeyEvent.VK_S) {
				player.down = false;
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
		
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
		player.mouseShoot = true;
		player.mx = (e.getX()/3);
		player.my = (e.getY()/3);
			
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			
			
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			this.mx = e.getX();
			this.my = e.getY();
			
		}
		
		
		}
		

