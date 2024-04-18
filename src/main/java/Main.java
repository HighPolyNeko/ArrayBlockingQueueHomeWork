import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
    public static BlockingQueue<String> queueA = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> queueB = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> queueC = new ArrayBlockingQueue<>(100);

    public static void main(String[] args) throws InterruptedException {
        // генерация текста
        Thread generated = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                String text = generateText("abc", 100000);
                try {
                    queueA.put(text);
                    queueB.put(text);
                    queueC.put(text);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        generated.start();

        // роверка текста
        List<Thread> threads = new ArrayList<>();


        threads.add(new Thread(() -> {
            try {
                System.out.println("a - " + charCounter(queueA, 'a'));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }));
        threads.add(new Thread(() -> {
            try {
                System.out.println("b - " + charCounter(queueB, 'b'));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }));
        threads.add(new Thread(() -> {
            try {
                System.out.println("c - " + charCounter(queueC, 'c'));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }));

        for (Thread thread : threads) {
            thread.start();
        }
        generated.join();
        for (Thread thread : threads) {
            thread.join();
        }
    }

    public static int charCounter(BlockingQueue<String> queue, char ch) throws InterruptedException {
        int count = 0;
        int max = 0;
        String text;

        for (int i = 0; i < 10000; i++) {
            text = queue.take();

            for (char c : text.toCharArray()) {
                if (c == ch) {
                    count++;
                }
            }
            if (count > max) {
                max = count;
            }
            count = 0;
        }

        return max;
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}
