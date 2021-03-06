import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class PropertiesComparator {

    private static class FileBadException extends Exception {}

    private static int equalSignAt(String s) {
        return s.indexOf("=");
    }

    private static void printHelp() {
        System.err.println("Please provide at least the bundle.properties and one bundle_XX.properties file.");
    }

    private static void printMissingTags(String sourcePath, String destPath) {
        try {
            File source = new File(sourcePath);
            File dest   = new File(destPath);

            if (!source.getName().endsWith(".properties") || !dest.getName().endsWith("properties")) {
                throw new FileBadException();
            }

            Scanner fileSource = new Scanner(source);
            Scanner fileDest   = new Scanner(dest);
            Set<String> tags = new HashSet<>();

            if(!(source.isFile() && dest.isFile())) {
                throw new FileNotFoundException("Please supply two files.");
            }

            // check what tags are present in the destination file
            String fileDestTag;
            while(fileDest.hasNext()) {
                do {
                    fileDestTag = fileDest.nextLine();
                } while(fileDestTag.isEmpty() || fileDestTag.startsWith("[ ]*#"));
                // while lines are empty or start with a comment.

                tags.add(fileDestTag.substring(0, equalSignAt(fileDestTag)));
            }

            // print all tags that are not part of the destination file
            String fileSourceTag;
            while(fileSource.hasNext()) {
                do {
                    fileSourceTag = fileSource.nextLine();
                } while (fileSourceTag.isEmpty());

                if(!tags.contains(fileSourceTag.substring(0, equalSignAt(fileSourceTag)))) {
                    System.out.println(fileSourceTag);
                }
            }

            fileDest.close();
            fileSource.close();

        } catch (FileNotFoundException e) {
            printHelp();
            System.exit(1);
        }
        catch (IndexOutOfBoundsException e) {
            // catches the following cases:
            // 1) user provides no input
            // 2) user provides only one file
            printHelp();
            System.exit(2);
        }
        catch (FileBadException e) {
            System.err.println("One or more of the files might not be a properties file.");
            System.exit(3);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Set<File> filesToCompare = new HashSet<>();

        File files = new File(".");
        if(files.list().length > 0) {
            for (String filename : files.list()) {

                if (filename.startsWith("bundle_") && filename.endsWith(".properties")) {
                    filesToCompare.add(new File(filename));
                }

            }


            for (File file : filesToCompare) {
                System.out.println("========== " + file.getName() + " ==========");
                printMissingTags(args[0], file.getName());
                System.out.println();
            }
        }
    }
}
