package cc.ejyf.jfly;


import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Generator {
    private final static String AIUEO = "0M9N8B7V6C5X4Z3A2S1DQFWGEHRJTKYLUPIOqpwoeirutyalskdjfhgmznxbcv";

    /**
     * 生成token
     *
     * @param blockCount  2~100
     * @param blockLength
     * @return
     */
    public static String generateToken(int blockCount, int blockLength) {
        Random rand = new Random(System.nanoTime());
        return IntStream.rangeClosed(0, blockCount)
                .parallel()
                .unordered()
                .limit(Math.min(Math.max(blockCount, 2), 100))
                .mapToObj(s -> {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < blockLength; i++) {
                        sb.append(AIUEO.charAt(rand.nextInt(AIUEO.length())));
                    }
                    return sb.toString();
                }).collect(Collectors.joining("-"));
    }
}
