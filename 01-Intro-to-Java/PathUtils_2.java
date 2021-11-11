public class PathUtils {
    public static void main(String[] args) {
        System.out.println(getCanonicalPath("/home/"));
        System.out.println(getCanonicalPath("/../"));
        System.out.println(getCanonicalPath("/home//foo/"));
        System.out.println(getCanonicalPath("/a/./b/../../c/"));
        System.out.println(getCanonicalPath("/a/./b/..../.../c/../../")); // out: /a/b/....
        System.out.println(getCanonicalPath("/a/./b/..../.../c/../.././../")); // out: /a/b
        System.out.println(getCanonicalPath("///////home/files//docs/notes////sub///e/e/../.././"));
    }

    private static String getCanonicalPath(String path) {
        StringBuilder canonicalPath = new StringBuilder();

        for (int i = 0; i < path.length(); i++) {
            if (path.charAt(i) == '/') continue;
            int end = path.indexOf('/', i);

            if (end == -1) {
                end = path.length();
            }
            String dir = path.substring(i, end);

            if (dir.equals(".")) continue;
            if (dir.equals("..")) {
                int rm_i = canonicalPath.lastIndexOf("/");

                // Move to parent if such exists
                if (rm_i != -1)
                    canonicalPath.delete(rm_i, canonicalPath.length());
            } else {
                canonicalPath.append("/");
                canonicalPath.append(dir);
            }

            // Skip to the next slash
            i = end;
        }

        if (canonicalPath.length() == 0) return "/";

        return canonicalPath.toString();
    }
}
