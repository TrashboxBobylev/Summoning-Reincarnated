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

package com.watabou.utils;

import java.util.ArrayList;

public class Rect {

	public int left;
	public int top;
	public int right;
	public int bottom;
	
	public Rect() {
		this( 0, 0, 0, 0 );
	}
	
	public Rect( Rect rect ) {
		this( rect.left, rect.top, rect.right, rect.bottom );
	}
	
	public Rect( int left, int top, int right, int bottom ) {
		this.left	= left;
		this.top	= top;
		this.right	= right;
		this.bottom	= bottom;
	}
	
	public int width() {
		return right - left;
	}
	
	public int height() {
		return bottom - top;
	}
	
	public int square() {
		return width() * height();
	}
	
	public Rect set( int left, int top, int right, int bottom ) {
		this.left	= left;
		this.top	= top;
		this.right	= right;
		this.bottom	= bottom;
		return this;
	}
	
	public Rect set( Rect rect ) {
		return set( rect.left, rect.top, rect.right, rect.bottom );
	}
	
	public Rect setPos( int x, int y ) {
		return set( x, y, x + (right - left), y + (bottom - top));
	}
	
	public Rect shift( int x, int y ) {
		return set( left+x, top+y, right+x, bottom+y );
	}
	
	public Rect resize( int w, int h ){
		return set( left, top, left+w, top+h);
	}
	
	public boolean isEmpty() {
		return right <= left || bottom <= top;
	}
	
	public Rect setEmpty() {
		left = right = top = bottom = 0;
		return this;
	}
	
	public Rect intersect( Rect other ) {
		Rect result = new Rect();
		result.left		= Math.max( left, other.left );
		result.right	= Math.min( right, other.right );
		result.top		= Math.max( top, other.top );
		result.bottom	= Math.min( bottom, other.bottom );
		return result;
	}
	
	public Rect union( Rect other ){
		Rect result = new Rect();
		result.left		= Math.min( left, other.left );
		result.right	= Math.max( right, other.right );
		result.top		= Math.min( top, other.top );
		result.bottom	= Math.max( bottom, other.bottom );
		return result;
	}
	
	public Rect union( int x, int y ) {
		if (isEmpty()) {
			return set( x, y, x + 1, y + 1 );
		} else {
			if (x < left) {
				left = x;
			} else if (x >= right) {
				right = x + 1;
			}
			if (y < top) {
				top = y;
			} else if (y >= bottom) {
				bottom = y + 1;
			}
			return this;
		}
	}
	
	public Rect union( Point p ) {
		return union( p.x, p.y );
	}
	
	public boolean inside( Point p ) {
		return p.x >= left && p.x < right && p.y >= top && p.y < bottom;
	}

	public Point center() {
		return new Point(
				(left + right) / 2 + (((right - left) % 2) == 0 ? Random.Int( 2 ) : 0),
				(top + bottom) / 2 + (((bottom - top) % 2) == 0 ? Random.Int( 2 ) : 0) );
	}
	
	public Rect shrink( int d ) {
		return new Rect( left + d, top + d, right - d, bottom - d );
	}
	
	public Rect shrink() {
		return shrink( 1 );
	}

	public Rect scale( int d ){
		return new Rect( left * d, top * d, right * d, bottom * d );
	}

	public ArrayList<Point> getPoints() {
		ArrayList<Point> points = new ArrayList<>();
		for (int i = left; i <= right; i++)
			for (int j = top; j <= bottom; j++)
				points.add(new Point(i, j));
		return points;
	}
	
}
