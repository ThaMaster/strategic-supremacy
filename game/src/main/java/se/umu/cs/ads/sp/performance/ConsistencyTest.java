package se.umu.cs.ads.sp.performance;

import se.umu.cs.ads.sp.util.Position;

import java.io.IOException;
import java.nio.file.Path;

public class ConsistencyTest implements ITest {

    private int errors;

    public ConsistencyTest() {
        start();
    }

    @Override
    public void start() {
        errors = 0;
    }

    @Override
    public void finish() {

    }

    @Override
    public void setTargetFile(Path path) {

    }

    @Override
    public void output() throws IOException {

    }

    @Override
    public Long getId() {
        return null;
    }

    public void checkData(int localData, int remoteData) {
    }

    public void checkData(Position localPosition, Position remotePosition) {
        if (Position.distance(localPosition, remotePosition) > TestConstants.POSITION_ERROR_MARGIN) {
            errors++;
        }
    }
}
