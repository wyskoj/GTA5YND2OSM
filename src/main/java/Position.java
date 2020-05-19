public class Position {
	final double x;
	final double y;
	final double z;
	
	public Position(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Position(String x, String y, String z) {
		this.x = Double.parseDouble(x);
		this.y = Double.parseDouble(y);
		this.z = Double.parseDouble(z);
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("Position{");
		sb.append("x=").append(x);
		sb.append(", y=").append(y);
		sb.append(", z=").append(z);
		sb.append('}');
		return sb.toString();
	}
}
