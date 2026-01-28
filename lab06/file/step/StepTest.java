package bg.sofia.uni.fmi.mjt.file.step;

import bg.sofia.uni.fmi.mjt.file.File;
import bg.sofia.uni.fmi.mjt.file.exception.EmptyFileException;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class StepTest {

    // Checking if throws for null arguments 

    @Test
    void testCheckEmptyFileThrowsOnEmptyContent() {
        CheckEmptyFile step = new CheckEmptyFile();
        File emptyFile = new File("");
        File nullFile = null;


        assertThrows(EmptyFileException.class, () -> step.process(emptyFile),
                "Should throw EmptyFileException if file content is empty");

        assertThrows(EmptyFileException.class, () -> step.process(nullFile),
                "Should throw EmptyFileException if file is null");
    }

    @Test
    void testCheckCountFilesThrowsOnNullFiles() {
        CountFiles step = new CountFiles();
        Collection<File> coll = null;


        assertThrows(IllegalArgumentException.class, () -> step.process(coll),
                "Should throw IllegalArgumentException if file collection is null");
    }

    @Test
    void testCheckPrintFilesThrowsOnNullFiles() {
        PrintFiles step = new PrintFiles();
        Collection<File> coll = null;


        assertThrows(IllegalArgumentException.class, () -> step.process(coll),
                "Should throw IllegalArgumentException if file collection is null");
    }

    @Test
    void testCheckUpperCaseFileThrowsOnNullFile() {
        UpperCaseFile step = new UpperCaseFile();
        File file = null;


        assertThrows(IllegalArgumentException.class, () -> step.process(file),
                "Should throw IllegalArgumentException if file is null");
    }

    @Test
    void testCheckSplitFileThrowsOnNullFile() {
        SplitFile step = new SplitFile();
        File file = null;


        assertThrows(IllegalArgumentException.class, () -> step.process(file),
                "Should throw IllegalArgumentException if file is null");
    }

    @Test
    void testCheckEmptyFileReturnsValidFile() {
        CheckEmptyFile step = new CheckEmptyFile();
        File validFile = new File("valid");

        File result = step.process(validFile);
        assertEquals(validFile, result);
    }

    // Tests for Count Files class

    @Test
    void testCountFilesReturnsCorrectSize() {
        CountFiles step = new CountFiles();
        Collection<File> files = List.of(new File("a"), new File("b"), new File("c"));

        assertEquals(3, step.process(files));
    }

    // Tests for Split File class

    @Test
    void testSplitFileSplitsByWhitespace() {
        SplitFile step = new SplitFile();
        File file = new File("one two three");

        Set<File> result = step.process(file);

        assertEquals(3, result.size());
        assertTrue(result.stream().anyMatch(f -> f.getContent().equals("one")));
        assertTrue(result.stream().anyMatch(f -> f.getContent().equals("two")));
        assertTrue(result.stream().anyMatch(f -> f.getContent().equals("three")));
    }

    @Test
    void testSplitFileHandlesMultipleSpaces() {
        SplitFile step = new SplitFile();
        File file = new File("one    two");

        Set<File> result = step.process(file);
        assertEquals(2, result.size());
    }

    // Tests for Upper Case Files class

    @Test
    void testUpperCaseFileTransformsContent() {
        UpperCaseFile step = new UpperCaseFile();
        File file = new File("hello world");

        step.process(file);

 //this assertion used to fail in the first implementation
        assertEquals("HELLO WORLD", file.getContent(), 
            "File content should be converted to Uppercase");
    }

    // Tests for Print Files class

    @Test
    void testPrintFilesWritesToStdOut() {
        PrintFiles step = new PrintFiles();
        Collection<File> input = List.of(new File("print me"));

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {
            step.process(input);
            assertEquals("print me", outContent.toString().trim());
        } finally {
            System.setOut(originalOut);
        }
    }
}