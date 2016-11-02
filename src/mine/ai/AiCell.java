package mine.ai;

import mine.Cell;

public class AiCell {
	public enum AiAction {
		OPEN,
		FLAG
	}
	
	public enum AiStatus {
		UNDONE,
		DONE
	}
	
	private int mineProbability;
	private Cell relatedCell;
	private AiStatus aiStatus;
	private AiAction aiAction;
	private InferenceInfo inferenceInfo; 
	
	public AiCell(Cell c) {
		mineProbability = 100;
		this.relatedCell = c;
		this.aiStatus = AiStatus.UNDONE;
		this.aiAction = null;
		this.inferenceInfo = null;
	}
	
	public void setMineProbability(int p) {
		this.mineProbability = p;
		this.aiStatus = AiStatus.UNDONE;
	}
	
	public int getMineProbability() {
		return this.mineProbability;
	}
	
	public Cell getRelatedCell() {
		return this.relatedCell;
	}
	
	public void setAiStatus(AiStatus s) {
		this.aiStatus = s;
	}
	
	public AiStatus getAiStatus() {
		return this.aiStatus;
	}
	
	public void setAiAction(AiAction action) {
		this.aiAction = action;
	}
	
	public AiAction getAiAction() {
		return this.aiAction;
	}
	
	public void setInferenceInfo(InferenceInfo info) {
		this.inferenceInfo = info;
	}
	
	public InferenceInfo getInferenceInfo() {
		return this.inferenceInfo;
	}
}
