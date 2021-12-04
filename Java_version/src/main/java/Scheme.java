/**
 * @author: tangshao
 * @Date: 2021/12/3
 */
public enum Scheme {
    Loop(1), PeterReif(2), ModifiedButterfly(3);

    public final int index;

    private Scheme(int index) {
        this.index = index;
    }

    public int getIndex() {
        return this.index;
    }
}
