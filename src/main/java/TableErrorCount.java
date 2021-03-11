public class TableErrorCount {
    private int noPrimaryKey;

    private int repeatIndex;

    private int characterIncorrect;

    private int irregularName;

    private int precisionError;

    private int noIncrementalTimestamp;

    private int noColumnComment;

    private int saveBitData;

    public TableErrorCount() {
        noPrimaryKey = 0;
        repeatIndex = 0;
        characterIncorrect = 0;
        irregularName = 0;
        precisionError = 0;
        noIncrementalTimestamp = 0;
        noColumnComment = 0;
        saveBitData = 0;
    }

    public void addNoPrimaryKey(){
        noPrimaryKey++;
    }

    public void addRepeatIndex(){
        repeatIndex++;
    }

    public void addCharacterIncorrect(){
        characterIncorrect++;
    }

    public void addIrregularName(){
        irregularName++;
    }

    public void addPrecisionError(){
        precisionError++;
    }

    public void addNoIncrementalTimestamp(){
        noIncrementalTimestamp++;
    }

    public void addNoColumnComment(){
        noColumnComment++;
    }

    public void addSaveBitData(){
        saveBitData++;
    }

    public int getNoPrimaryKey() {
        return noPrimaryKey;
    }

    public void setNoPrimaryKey(int noPrimaryKey) {
        this.noPrimaryKey = noPrimaryKey;
    }

    public int getRepeatIndex() {
        return repeatIndex;
    }

    public void setRepeatIndex(int repeatIndex) {
        this.repeatIndex = repeatIndex;
    }

    public int getCharacterIncorrect() {
        return characterIncorrect;
    }

    public void setCharacterIncorrect(int characterIncorrect) {
        this.characterIncorrect = characterIncorrect;
    }

    public int getIrregularName() {
        return irregularName;
    }

    public void setIrregularName(int irregularName) {
        this.irregularName = irregularName;
    }

    public int getPrecisionError() {
        return precisionError;
    }

    public void setPrecisionError(int precisionError) {
        this.precisionError = precisionError;
    }

    public int getNoIncrementalTimestamp() {
        return noIncrementalTimestamp;
    }

    public void setNoIncrementalTimestamp(int noIncrementalTimestamp) {
        this.noIncrementalTimestamp = noIncrementalTimestamp;
    }

    public int getNoColumnComment() {
        return noColumnComment;
    }

    public void setNoColumnComment(int noColumnComment) {
        this.noColumnComment = noColumnComment;
    }

    public int getSaveBitData() {
        return saveBitData;
    }

    public void setSaveBitData(int saveBitData) {
        this.saveBitData = saveBitData;
    }
}
