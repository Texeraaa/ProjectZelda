package com.nrstudios.world;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.nrstudios.entities.Bullet;
import com.nrstudios.entities.Enemy;
import com.nrstudios.entities.Entity;
import com.nrstudios.entities.Player;
import com.nrstudios.entities.Weapon;
import com.nrstudios.entities.lifePack;
import com.nrstudios.graficos.Spritesheet;
import com.nrstudios.main.Game;

public class World {

	public static Tile[] tiles;
	public static int WIDTH,HEIGHT;
	public static final int TILE_SIZE = 16;
	
	
	
	public World(String path) {
		try {
			BufferedImage map = ImageIO.read(getClass().getResource(path));
			int[] pixels = new int[map.getWidth() * map.getHeight()];
			WIDTH = map.getWidth();
			HEIGHT = map.getHeight();
			tiles =  new Tile[map.getWidth() * map.getHeight()];
			map.getRGB(0, 0, map.getWidth(), map.getHeight(), pixels, 0, map.getWidth());
			for(int xx = 0; xx < map.getWidth(); xx++) {
				for(int yy = 0; yy < map.getHeight(); yy++) {
					int pixelAtual = pixels[xx + (yy*map.getWidth())];
					
					//ITENS
					
						tiles[xx + (yy*WIDTH)] = new FloorTile(xx*16,yy*16,Tile.TILE_FLOOR); //CHAO
					
							if(pixelAtual == 0xFF000000) {
								tiles[xx + (yy*WIDTH)] = new FloorTile(xx*16,yy*16,Tile.TILE_FLOOR);
								//FLOOR
							
							}else if(pixelAtual == 0xFFFFFFFF) {
								tiles[xx + (yy*WIDTH)] = new WallTile(xx*16,yy*16,Tile.TILE_WALL);
								//WALL
							
							}else if(pixelAtual == 0xFF0024FF) {
								tiles[xx + (yy*WIDTH)] = new FloorTile(xx*16,yy*16,Tile.TILE_FLOOR);
								Game.player.setX(xx*16);
								Game.player.setY(yy*16);
								//PLAYER
								
							}else if(pixelAtual == 0xFFAC3232) {
								Enemy en = new Enemy (xx*16,yy*16,16,16,Entity.ENEMY_EN);
								Game.entities.add(en);
								Game.enemies.add(en);
								//ENEMY
							
							}else if(pixelAtual == 0xFFDF7126) {
								Game.entities.add(new Weapon(xx*16,yy*16,16,16,Entity.WEAPON_EN));
								//WEAPON
								
							}else if(pixelAtual == 0xFFD77BBA) {
								lifePack pack = new lifePack(xx*16,yy*16,16,16,Entity.LIFEPACK_EN);
								pack.setMask(8, 8, 8, 8);
								Game.entities.add(pack);
								//LIFEPACK
								
							}else if(pixelAtual == 0xFFFBF236) {
								Game.entities.add(new Bullet(xx*16,yy*16,16,16,Entity.BULLET_EN));
								//BULLET
								
							}
							
						}	
					}
			
			
				//RENDER
			
				} catch (IOException e) {
			e.printStackTrace();
		}
	}
			//TILES
	public static boolean isFree(int xnext, int ynext) {
		int x1 = xnext / TILE_SIZE;
		int y1 = ynext / TILE_SIZE;
		
		int x2 = (xnext + TILE_SIZE-1) / TILE_SIZE;
		int y2 = ynext / TILE_SIZE;
		
		int x3 = xnext / TILE_SIZE;
		int y3 = (ynext + TILE_SIZE-1) / TILE_SIZE;
		
		int x4 = (xnext + TILE_SIZE-1) / TILE_SIZE;
		int y4 = (ynext + TILE_SIZE-1) / TILE_SIZE;
		
		
		
		return ! ((tiles[x1 + (y1*World.WIDTH)] instanceof WallTile) ||
				(tiles[x2 + (y2*World.WIDTH)] instanceof WallTile) ||
				(tiles[x3 + (y3*World.WIDTH)] instanceof WallTile) ||
				(tiles[x4 + (y4*World.WIDTH)] instanceof WallTile)); 
				
	}
	
	public static void restartGame(String level) {
		Game.entities.clear();
		Game.enemies.clear();
		Game.bullets.clear();
		Player.hasGun = false;
		Game.entities = new ArrayList<Entity>();
		Game.enemies = new ArrayList<Enemy>();
		Game.spritesheet = new Spritesheet("/Spritesheet.png");
		Game.player = new Player(0,0,16,16,Game.spritesheet.getSprite(16*2, 0, 16, 16));
		Game.entities.add(Game.player);
		Game.world = new World("/" + level);
	return;
	}
	
		public void render(Graphics g) {
			int xstart = Camera.x/16;
			int ystart = Camera.y/16;
			
			int xfinal = xstart + (Game.Width / 16);
			int yfinal = ystart + (Game.Height / 16);
			
			for(int xx = xstart; xx <= xfinal; xx++) {
			for(int yy= ystart; yy <= yfinal; yy++)  {
				if(xx < 0 || yy < 0 || xx >= WIDTH || yy >= HEIGHT)
					continue;					
					Tile tile = tiles[xx + (yy*WIDTH)];
					tile.render(g);
				}
			}
		}
		
	public static void renderMiniMap() {
		for(int i = 0; i < Game.minimapPixels.length; i++) {
			Game.minimapPixels[i] = 0;
		}
		for(int xx = 0; xx < WIDTH; xx++) {
			for(int yy = 0; yy < HEIGHT; yy++) {
				if(tiles[xx + (yy*WIDTH)] instanceof WallTile) {
					Game.minimapPixels[xx + (yy*WIDTH)] = 0xFF1C1D1D;
					
				}if(tiles[xx + (yy*WIDTH)] instanceof FloorTile) {
					Game.minimapPixels[xx + (yy*WIDTH)] = 0xFF071200;
				}
			}
		
		int	xPlayer = Game.player.getX()/16;
		int	yPlayer = Game.player.getY()/16;
		Game.minimapPixels[xPlayer + (yPlayer*WIDTH)] = 0xff011b39;
		}
}
}
