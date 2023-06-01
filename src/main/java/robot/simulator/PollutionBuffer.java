package robot.simulator;


import common.utils.Logger;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class PollutionBuffer implements Buffer {


    private final int BUFFER_SIZE = 8;


    private final double OVERLAP_FACTOR = 0.5;


    private final LinkedList<Measurement> measurements;


    public PollutionBuffer() {
        this.measurements = new LinkedList<>();
    }


    @Override
    public synchronized void addMeasurement(Measurement m) {
        while (measurements.size() >= BUFFER_SIZE) {
            try {
                wait(); // wait for the consumer to empty the buffer
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        measurements.add(m);

        if (measurements.size() == BUFFER_SIZE) {
            notifyAll(); // notify the consumer that the buffer is full
        }
    }

    @Override
    public synchronized List<Measurement> readAllAndClean() {
        if (measurements.size() < BUFFER_SIZE) {
            try {
                wait(); // wait for the producer to fill the buffer
            } catch (InterruptedException e) {
                Logger.logException(e);
            }
        }

        List<Measurement> result = new ArrayList<>();
        int windowSize = (int) (BUFFER_SIZE * OVERLAP_FACTOR);

        for (int i = 0; i < windowSize; i++) {
            result.add(measurements.get(i));
        }

        // make space for the next window
        for (int i = 0; i < windowSize; i++) {
            measurements.removeFirst();
        }

        notifyAll(); // notify the producer that the buffer is not full anymore

        return result;
    }
}
