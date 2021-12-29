
public class PathUtils {

    public static String getCanonicalPath(String path) {
        StringBuilder canonicalPath = new StringBuilder();

        String[] tokens = path.split("/");

        for (String dir : tokens) {
            if (dir.matches("[.]?")) continue;

            if (dir.equals("..")) {
                int rm_i = canonicalPath.lastIndexOf("/");

                // Move to parent if such exists
                if (rm_i != -1)
                    canonicalPath.delete(rm_i, canonicalPath.length());
            } else if (!dir.isEmpty()) {
                canonicalPath.append("/");
                canonicalPath.append(dir);
            }
        }

        if (canonicalPath.length() == 0) return "/";

        return canonicalPath.toString();
    }
}
