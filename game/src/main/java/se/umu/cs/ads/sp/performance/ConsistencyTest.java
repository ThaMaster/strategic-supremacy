package se.umu.cs.ads.sp.performance;

import se.umu.cs.ads.sp.util.Position;

import java.io.IOException;

public class ConsistencyTest extends ITest {

    private int errors;

    public ConsistencyTest(Long id) {
        super(id);
    }

    @Override
    public void start() {
        errors = 0;
    }

    @Override
    public void finish() {

    }

    @Override
    public void output() throws IOException {

    }

    public void checkData(Position localPosition, Position remotePosition) {
        if (Position.distance(localPosition, remotePosition) > TestConstants.POSITION_ERROR_MARGIN) {
            errors++;
            System.out.println(errors);
        }
    }
}
