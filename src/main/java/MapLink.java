import java.util.List;

public class MapLink implements GTA5Node {
	final String guid;
	final String name;
	final Position position;
	final List<Attribute> attributes;
	final List<String> referencesGuids;
	int osmID;
	
	public MapLink(String guid, String name, Position position, List<Attribute> attributes, List<String> references) {
		this.guid = guid;
		this.name = name;
		this.position = position;
		this.attributes = attributes;
		this.referencesGuids = references;
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("MapLink{");
		sb.append("guid='").append(guid).append('\'');
		sb.append(", name='").append(name).append('\'');
		sb.append(", position=").append(position);
		sb.append(", attributes=").append(attributes);
		sb.append(", referencesGuids=").append(referencesGuids);
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
