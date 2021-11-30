package cc.ejyf.jfly.tuple;

public class Tuple6<T, U, V, W, X, Y> extends Tuple5<T, U, V, W, X> {
    public final Y e6;

    public Tuple6(T e1, U e2, V e3, W e4, X e5, Y e6) {
        super(e1, e2, e3, e4, e5);
        this.e6 = e6;
    }
}
