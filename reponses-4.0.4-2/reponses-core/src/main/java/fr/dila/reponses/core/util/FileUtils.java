package fr.dila.reponses.core.util;

public final class FileUtils {

    private FileUtils() {}

    /**
     * Returns a clean filename, stripping upload path on client side.
     * For instance, it turns "/tmp/2349876398/foo.pdf" into "foo.pdf"
     * <p>
     * Fixes NXP-544
     *
     * @param filename the filename
     * @return the stripped filename
     */
    public static String getCleanFileName(String filename) {
        String res = null;
        int lastWinSeparator = filename.lastIndexOf('\\');
        int lastUnixSeparator = filename.lastIndexOf('/');
        int lastSeparator = Math.max(lastWinSeparator, lastUnixSeparator);
        if (lastSeparator != -1) {
            res = filename.substring(lastSeparator + 1, filename.length());
        } else {
            res = filename;
        }
        return res;
    }
}
