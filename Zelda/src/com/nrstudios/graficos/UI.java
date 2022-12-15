package com.nrstudios.graficos;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import com.nrstudios.main.Game;

public class UI {

	public void render(Graphics g) {
		g.setColor(Color.black);
		g.fillRect(1, 1, 90, 8);
		g.setColor(Color.red);
		g.fillRect(1, 1, (int)((Game.player.life/Game.player.maxLife)*90), 8);
		g.setColor(Color.white);
		g.setFont(new Font("arial", Font.BOLD, 8));
		g.drawString((int) Game.player.life+"/"+(int)Game.player.maxLife, 8, 8);
	}
}
