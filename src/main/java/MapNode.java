/*
 * MIT License
 *
 * Copyright (c) %today.year Jacob Wysko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.util.List;

public class MapNode implements GTA5Node {
	final String guid;
	final String name;
	final Position position;
	final List<Attribute> attributes;
	int osmID;
	
	public MapNode(String guid, String name, Position position, List<Attribute> attributes) {
		this.guid = guid;
		this.name = name;
		this.position = position;
		this.attributes = attributes;
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("MapNode{");
		sb.append("guid='").append(guid).append('\'');
		sb.append(", name='").append(name).append('\'');
		sb.append(", position=").append(position);
		sb.append(", attributes=").append(attributes);
		sb.append('}');
		return sb.toString();
	}
	
	@Override
	public int compareGUID(GTA5Node o) {
		if (o instanceof MapNode) {
			return this.guid.compareTo(((MapNode) o).guid);
		} else {
			return this.guid.compareTo(((MapLink) o).guid);
		}
		
	}
}
