package aaagt.ccollection.analizator;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;


public class Main {

    private static final String DONE = "done";

    private static final ArrayBlockingQueue<String> aQueue = new ArrayBlockingQueue(100);
    private static final ArrayBlockingQueue<String> bQueue = new ArrayBlockingQueue(100);
    private static final ArrayBlockingQueue<String> cQueue = new ArrayBlockingQueue(100);

    public static void main(String[] args) throws InterruptedException {
        Thread generatorTread = new Thread(Main::generateTexts);
        generatorTread.start();

        Thread aTread = new Thread(findStringWithMaxSymbol(aQueue, 'a'));
        Thread bTread = new Thread(findStringWithMaxSymbol(bQueue, 'b'));
        Thread cTread = new Thread(findStringWithMaxSymbol(cQueue, 'c'));

        aTread.start();
        bTread.start();
        cTread.start();

        generatorTread.join();
        aTread.join();
        bTread.join();
        cTread.join();
    }

    private static void generateTexts() {
        for (int i = 0; i < 10_000; i++) {
            var text = generateText("abc", 100_000);
            try {
                aQueue.put(text);
                bQueue.put(text);
                cQueue.put(text);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            aQueue.put(DONE);
            bQueue.put(DONE);
            cQueue.put(DONE);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static Runnable findStringWithMaxSymbol(
            ArrayBlockingQueue<String> sourceQueue,
            char charToCount
    ) {
        return () -> {
            try {
                String msg = null;
                String withMax = null;
                int maxCount = 0;
                int count = 0;

                while (!((msg = sourceQueue.take()).equals(DONE))) {
                    for (int i = 0; i < msg.length(); i++) {
                        if (msg.charAt(i) == charToCount) {
                            count++;
                        }
                    }
                    if (maxCount < count) {
                        maxCount = count;
                        withMax = msg;
                    }
                    count = 0;
                }

                System.out.printf("Строка содержащая максимальное количество символов %s(%d): %s\n\n\n", charToCount, maxCount, withMax);
            } catch (InterruptedException e) {
                System.err.println(e.getMessage());
            }
        };
    }

}
