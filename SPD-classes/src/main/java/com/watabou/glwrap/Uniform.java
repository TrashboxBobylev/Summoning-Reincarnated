/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
 *
 * Summoning Pixel Dungeon Reincarnated
 * Copyright (C) 2023-2025 Trashbox Bobylev
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.watabou.glwrap;

import com.badlogic.gdx.Gdx;

public class Uniform {

	private int location;
	
	public Uniform(int location) {
		this.location = location;
	}
	
	public int location() {
		return location;
	}
	
	public void enable() {
		Gdx.gl.glEnableVertexAttribArray(location);
	}
	
	public void disable() {
		Gdx.gl.glDisableVertexAttribArray(location);
	}
	
	public void value1f(float value) {
		Gdx.gl.glUniform1f(location, value);
	}
	
	public void value2f(float v1, float v2) {
		Gdx.gl.glUniform2f(location, v1, v2);
	}
	
	public void value4f(float v1, float v2, float v3, float v4) {
		Gdx.gl.glUniform4f(location, v1, v2, v3, v4);
	}
	
	public void valueM3(float[] value) {
		Gdx.gl.glUniformMatrix3fv(location, 1, false, value, 0);
	}
	
	public void valueM4(float[] value) {
		Gdx.gl.glUniformMatrix4fv(location, 1, false, value, 0);
	}
}