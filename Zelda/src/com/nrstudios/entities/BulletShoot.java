	package com.nrstudios.entities;


import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.nrstudios.main.Game;
import com.nrstudios.world.Camera;

public class BulletShoot extends Entity{
	
	private double dx;
	private double dy;
	private double spd = 4.0;
	
	public boolean right,up,left,down;
	
	private int life = 100,curLife = 45;
	
	private BufferedImage ArrowRight;
	private BufferedImage ArrowLeft;
	
	public BulletShoot(int x, int y, int width, int height, BufferedImage sprite,double dx,double dy) {
		super(x, y, width, height, sprite);
		ArrowRight = Game.spritesheet.getSprite(16*34, 0, 16, 16);
		ArrowLeft = Game.spritesheet.getSprite(16*35, 0, 16, 16);
		this.dx = dx;
		this.dy = dy;
	}
	
	public void tick() {
		x+=dx*spd;
		y+=dy*spd;
		curLife++;
		if(curLife == life) {
			Game.bullets.remove(this);
			return;	
		}
		
		
	}
	public void render(Graphics g) {
			
			if(Player.dir == Player.right_dir) {
		g.drawImage(ArrowRight, this.getX() - Camera.x, this.getY() - Camera.y, null);
			}else if(Player.dir == Player.left_dir) {
		g.drawImage(ArrowLeft, this.getX() - Camera.x, this.getY() - Camera.y, null);		
		
		}
	}
}
