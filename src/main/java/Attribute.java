public class Attribute {
	final String key;
	final String type;
	final String value;
	
	public Attribute(String key, String type, String value) {
		this.key = key;
		this.type = type;
		this.value = value;
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("Attribute{");
		sb.append("key='").append(key).append('\'');
		sb.append(", type='").append(type).append('\'');
		sb.append(", value='").append(value).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
