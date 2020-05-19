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
