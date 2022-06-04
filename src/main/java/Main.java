import java.io.File;
import java.util.concurrent.ForkJoinPool;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static final double Tb = Math.pow(2, 40);
    public static final double Gb = Math.pow(2, 30);
    public static final double Mb = Math.pow(2, 20);
    public static final double Kb = Math.pow(2, 10);

    public static void main(String[] args) {
        String folderPath = "d:/Installs/";
        File file = new File(folderPath);

        long start = System.currentTimeMillis();
        System.out.println(getFolderSize(file));
        System.out.println(getHumanReadableSize(getFolderSize(file), 2));
        String sizeTest = getHumanReadableSize(getFolderSize(file), 2);
        System.out.println(getBytesFromString(sizeTest));
        long duration = System.currentTimeMillis() - start;
        System.out.println(duration + " ms");

        start = System.currentTimeMillis();
        FolderSizeCalculator calculator = new FolderSizeCalculator(file);
        ForkJoinPool pool = new ForkJoinPool();
        long size = pool.invoke(calculator);
        System.out.println(size);
        duration = System.currentTimeMillis() - start;
        System.out.println(duration + " ms");
    }

    public static long getFolderSize(File folder) {
        if (folder.isFile()) {
            return folder.length();
        }
        long sum = 0;
        File[] files = folder.listFiles();
        for (File file : files) {
            sum += getFolderSize(file);
        }
        return sum;
    }

    public static String getHumanReadableSize(long size, int accuracy) {
        if (size > Tb) {
            return Double.toString(getHumanAccuracy(size / Tb, accuracy)) + "Tb";
        } else if (size > Gb) {
            return Double.toString(getHumanAccuracy(size / Gb, accuracy)) + "Gb";
        } else if (size > Mb) {
            return Double.toString(getHumanAccuracy(size / Mb, accuracy)) + "Mb";
        } else if (size > Kb) {
            return Double.toString(getHumanAccuracy(size / Kb, accuracy)) + "Kb";
        } else {
            return Long.toString(size) + "B";
        }
    }

    public static String getHumanReadableSize(long size) {
        return getHumanReadableSize(size, 0);
    }

    public static double getHumanAccuracy(double size, int accuracy) {
        double accuracySize = Math.pow(10, accuracy);
        return Math.round(size * accuracySize) / accuracySize;
    }

    public static long getBytesFromStringResult(long number, String unit) {
        return switch (unit) {
            case "TB", "T", "Tb" -> number * (long) Tb;
            case "GB", "Gb", "G" -> number * (long) Gb;
            case "MB", "Mb", "M" -> number * (long) Mb;
            case "KB", "Kb", "K" -> number * (long) Kb;
            case "B", "b" -> number;
        };
    }

    public static long getBytesFromString(String size) {
        final String regex = "([0-9.]+)([A-Za-z]{1,2})";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(size);
        m.find();
        return getBytesFromStringResult((long) Double.parseDouble(m.group(1)), m.group(2));
    }
}
