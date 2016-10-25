package mine;

public class Cell {
	public enum CellStatus {
		CLOSED,
		OPENED,
		FLAGGED;
	}
	
	public enum CellType {
		MINE,
		NUMBER;
	}
	
	private CellStatus status;
	private CellType type;
	private int number;
	private int x, y;
	
	public Cell(int x, int y) {
		this.type = CellType.NUMBER;
		this.number = 0;
		this.status = CellStatus.CLOSED;
		this.x = x;
		this.y = y;
	}
	
	public void setStatus(CellStatus status) {
		this.status = status;
	}
	
	public CellStatus getStatus() {
		return status;
	}
	
	public void setType(CellType type) {
		this.type = type;
	}
	
	public CellType getType() {
		return type;
	}
	
	public void setNumber(int number) {
		this.number = number;
	}
	
	public int getNumber() {
		return number;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
}
