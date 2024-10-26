package se.umu.cs.ads.sp.performance;

public class LatencyTest extends PerformanceTest{
    private int numClients;
    private int numResponses = 0;
    public LatencyTest(Long id) {
        super(id);
        numResponses = 0;
    }

    public void setNumClients(int amount){
        numClients = amount;
    }

    @Override
    public void finish(){
        numResponses++;
        if(numResponses == numClients) {
            this.endTime = System.currentTimeMillis();
            System.out.println("L3 Latency -> " + (this.endTime - this.startTime));
        }
    }
}
