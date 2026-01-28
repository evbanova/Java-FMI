package bg.sofia.uni.fmi.mjt.steganography;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ImageCodecImpl implements ImageCodec {
    private static final int BUFFER_SIZE = 5;

    /**
     * Iterates over all files from @coverSourceDirectory and @secretSourceDirectory in a lexicographic order and
     * picks up image ones - those with extensions png. Embeds all the images from @secretSourceDirectory into the
     * corresponding images from @coverSourceDirectory. Both directories are guaranteed to have the same number of
     * images and the secret image will always be embeddable in the cover image.
     * The resulting images are saved with the name of the corresponding cover image into the @outputDirectory.
     */
    public void embedPNGImages(String coverSourceDirectory, String secretSourceDirectory,
                               String outputDirectory) {
        try {
            SharedBuffer buffer = new SharedBuffer(BUFFER_SIZE);

            Type type = Type.EMBED;
            List<Thread> consumerThreads = startConsumerThreads(buffer, outputDirectory, type);
            List<Thread> producerThreads = startEmbedProducerThreadsString(coverSourceDirectory,
                                                            secretSourceDirectory, buffer);

            waitForThreads(producerThreads);
            buffer.setProducersFinished();
            waitForThreads(consumerThreads);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread was interrupted while processing images", e);
        }
    }

    private List<Path> getFiles(String sourceDirectory) {
        Path path = Path.of(sourceDirectory);

        try (Stream<Path> stream = Files.list(path)) {
            return stream.filter(p -> p.toString().endsWith(".png"))
                    .sorted().toList();
        } catch (IOException e) {
            throw new UncheckedIOException("Could not list files in directory", e);
        }
    }

    private List<Thread> startConsumerThreads(SharedBuffer buffer, String outputDirectory,
                                              Type type) {
        int cores = Runtime.getRuntime().availableProcessors();

        List<Thread> consumerThreads = new ArrayList<>();
        for (int i = 0; i < cores; i++) {
            Thread c;
            if (type == Type.EMBED) {
                c = new Thread(new EmbedConsumer(buffer, outputDirectory));
            } else {
                c = new Thread(new ExtractConsumer(buffer, outputDirectory));
            }
            consumerThreads.add(c);
            c.start();
        }
        return  consumerThreads;
    }

    private List<Thread> startEmbedProducerThreadsString(String coverSourceDirectory,
                            String secretSourceDirectory, SharedBuffer buffer) {
        List<Path> covers = getFiles(coverSourceDirectory);
        List<Path> secrets = getFiles(secretSourceDirectory);

        List<Thread> producerThreads = new ArrayList<>();
        for (int i = 0; i < covers.size(); i++) {
            Thread p = new Thread(new Producer(covers.get(i), secrets.get(i), buffer));
            producerThreads.add(p);
            p.start();
        }
        return  producerThreads;
    }

    private List<Thread> startExtractProducerThreadsString(String sourceDirectory, SharedBuffer buffer) {
        List<Path> files = getFiles(sourceDirectory);
        List<Thread> producerThreads = new ArrayList<>();

        for (Path file : files) {
            Thread p = new Thread(new Producer(file, buffer));
            producerThreads.add(p);
            p.start();
        }
        return producerThreads;
    }

    private void waitForThreads(List<Thread> threads) throws InterruptedException {
        for (Thread p : threads) {
            p.join();
        }
    }

    /***
     * Iterates over all files from @sourceDirectory and picks up image ones - those with extensions png.
     * Extracts from them the embedded images and saves the in the @outputDirectory with the same name as
     * the corresponding image from the @sourceDirectory.
     *
     * @param sourceDirectory the source directory containing the embedded images
     * @param outputDirectory the output directory in which the extracted images are saved
     */
    public void extractPNGImages(String sourceDirectory, String outputDirectory) {
        try {
            SharedBuffer buffer = new SharedBuffer(BUFFER_SIZE);

            Type type = Type.EXTRACT;
            List<Thread> consumerThreads = startConsumerThreads(buffer, outputDirectory, type);

            List<Thread> producerThreads = startExtractProducerThreadsString(sourceDirectory, buffer);

            waitForThreads(producerThreads);
            buffer.setProducersFinished();
            waitForThreads(consumerThreads);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread was interrupted while extracting images", e);
        }
    }
}
