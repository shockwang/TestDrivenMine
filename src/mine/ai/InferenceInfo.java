package mine.ai;

import java.util.Set;

public class InferenceInfo {
	public int mineNum;
	public Set<AiCell> aiCellSet;
	
	public InferenceInfo(int mineNum, Set<AiCell> aiCellSet) {
		this.mineNum = mineNum;
		this.aiCellSet = aiCellSet;
	}
}
